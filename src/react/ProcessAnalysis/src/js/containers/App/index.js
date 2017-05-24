//@flow
/* eslint camelcase:0 */
import React, { Component } from 'react'
import { translate } from 'react-i18next'
import { connect } from 'react-redux'
import autobind from 'autobind-decorator'
import { range, memoize, isEqual, debounce, clamp, last } from 'lodash-es'
import { message, InputNumber, Button, Radio } from 'antd'
import moment from 'moment'
import EventBus from 'eventbusjs'
import GearListChart from 'react-gear-list-chart'
import Header from 'containers/Header'
import Pagination from 'components/Pagination'
import Donut from 'components/DonutChart'
import Orbit from 'components/OrbitChart'
import ReversedRange from 'components/ReversedRange'
import withClientRect from '../../HOC/withClientRect'
import selectHelper from 'components/SelectHelper'
import { ParamUpdate, PageChange } from 'actions'
import cache from 'utils/cache'
import { CurrentPage } from 'utils/helpers'
import { MetaUpdate } from 'actions'
import classnames from 'classnames'
import colors from 'utils/colors'
import { BriefConv, DetailConv } from 'converters'
import { warn } from 'utils/logger'
import './app.scss'

const RadioButton = Radio.Button
const RadioGroup = Radio.Group
const Placeholder = { strips: { color: '#F9F9F9', weight: 1, type: 'placeholder' } }
const DisplayOptions = [
  { key: 'display_asset_type' },
  { key: 'display_brand' },
  { key: 'display_dept' },
]
const BallsStub = [
  { key: 'reportTime', i18n: 'report_incident' },
  { key: 'dispatchTime', i18n: 'dispatch_incident' },
  { key: 'respond', i18n: 'accept_incident' },
  { key: 'arrived', i18n: 'signin_incident' },
  { key: 'workingTime', i18n: 'fixing_incident' },
  { key: 'ETTR', i18n: 'close_incident' }
]

function DataOrPlaceHolder(items, placeholderSize) {
  // ignore placeholder and empty data
  if (items && items.length && items[0].strips.type !== 'placeholder') {
    return items
  }
  return App.getPlaceholder(placeholderSize)
}

const ONE_DAY = 3600 * 24
const ONE_HOUR = 3600
const ONE_MIN = 60
function HumanizeDuration(valueInSec) {
  let topDigit = valueInSec / ONE_DAY
  if (Number.isInteger(topDigit)){
    return [topDigit, 'day']
  }
  topDigit = valueInSec / ONE_HOUR
  if (Number.isInteger(topDigit)){
    return [topDigit, 'hour']
  }
  return [(valueInSec / ONE_MIN) | 0, 'min']
}

function GetDonutChartRow(label, value) {
  value = value ? moment.duration(value * 1000).humanize() : ''
  return { label, value }
}

function GetDistance(ball, gross) {
  // start/end angle are fixed
  if (gross == undefined || ball.key === 'reportTime') {
    return 0
  }
  if (ball.key === 'ETTR') {
    return 300
  }
  let max = gross.ETTR
  let distance = (gross[ball.key] / max) * 300
  return distance
}

function ensureSize(width, height) {
  width = clamp(width, 1000, 1500)
  if (height < 900) {
    width = 1100
  }
  return {
    outer_R: width * .275,
    outer_r: width * .2,
    inner_R: width * .175,
    inner_r: width * .142
  }
}

function mapDispatch2Porps(dispatch) {
  return {
    updateDisplayType: (value) => {
      dispatch(ParamUpdate('display', value.key))
    },
    updateDataType: (value) => {
      dispatch(ParamUpdate('datatype', value))
    },
    updatePagination: (type, pageNumber) => {
      dispatch(PageChange(type, pageNumber))
    },
    updateDistribution: (value, dataType) => {
      let suffix = 'Response'
      if (dataType === 'arrival_time') {
        suffix = 'Arrival'
      } else if (dataType === 'ettr') {
        suffix = 'Ettr'
      }
      dispatch(ParamUpdate('distribution' + suffix, value))
    },
    fetchBriefs: extraParam => {
      dispatch({ type: 'get/briefs', data: extraParam })
    },
    fetchDetails: extraParam => {
      dispatch({ type: 'get/details', data: extraParam })
    },
    fetchGross: extraParam => {
      dispatch({ type: 'get/gross', data: extraParam })
  },
    updateMeta: () => dispatch(MetaUpdate())
  }
}

function mapState2Props(state) {
  let { parameters : { pagination, display, dataType, distributionEttr, distributionResponse, distributionArrival } } = state
  return { pagination, display, dataType, distributionEttr, distributionResponse, distributionArrival }
}

@connect(mapState2Props, mapDispatch2Porps)
@autobind
export class App extends Component<void, Props, void> {

  static getPlaceholder = memoize(count => range(count)
                           .map(() => Placeholder))
  state = {
    briefs: [],
    centerItems: [],
    details: [],
    generalGross: {},
    selected: null,
    distriMax: 0,
    distriUnit: 'min'
  }

  clickLeftTooth(evt) {
    let data = evt.stripData.data
    if (this.refs.leftChart.isFocused() && this.state.selected.id === data.id ) {
      this.setState({ selected: null })
    } else {
      this.setState({ selected: data })
      this.clearFocus('right')
    }
  }

  clickRightTooth(evt) {
    let data = evt.stripData.data
    if (this.refs.rightChart.isFocused() && this.state.selected.id === data.id) {
      this.setState({ selected: null })
    } else {
      let data = evt.stripData.data
      this.setState({ selected: data})
      this.clearFocus('left')
    }
  }
  onRightPagerChange = value => {
    this.props.updatePagination('detail', value)
  }
  onLeftPagerChange = value => {
    this.props.updatePagination('brief', value)
  }
  initDistributionMax() {
    let [ max, unit ] = HumanizeDuration(last(this.getCurrentDistribution()), this.props.dataType)
    this.setState({
      distriMax: max,
      distriUnit: unit
    })
  }
  onClickDonut(evt) {
    let id = evt.currentTarget.id
    let { dataType } = this.props
    if (id !== dataType) {
      this.props.updateDataType(id)
      this.initDistributionMax()
    }
  }

  loadAll() {
    let { fetchBriefs, fetchDetails, fetchGross } = this.props
    fetchBriefs()
    fetchDetails()
    fetchGross()
  }

  getDisplayOptions() {
    return DisplayOptions.map(o => ({ key: o.key, label: this.props.t(o.key)}))
  }

  mountBriefData(evt, data) {
    let { t, dataType } = this.props
    let briefs = data.data || []
    if (briefs.length === 0) {
      message.info(t('group_info') + ': ' + t('no_more_data'))
    }
    this.setState({ briefs: BriefConv(briefs, dataType) })
    this.clearFocus('left')
  }

  mountDetailData(evt, data) {
    let { t, dataType } = this.props
    let details = data.data || []
    if (details.length === 0) {
      message.info(t('asset_info') + ': ' + t('no_more_data'))
    }
    this.setState({ details: DetailConv(details, dataType) })
    this.clearFocus('right')
  }

  mountGrossData(evt, data) {
    let gross = data.data.data // -.-;
    this.setState({ generalGross: gross })
  }

  clearFocus(type) {
    if (type === 'left') {
      this.refs.leftChart.clearFocus()
    } else if (type === 'right') {
      this.refs.rightChart.clearFocus()
    }
  }

  getLaneColor() {
    let dataType = this.props.dataType
    switch (dataType) {
      case 'ettr':
        return colors.purple
      case 'response_time':
        return colors.yellow
      case 'arrival_time':
        return colors.green
      default:
        throw Error('Invalid dataType, no base color found')
    }
  }
  getBalls() {
    let { t } = this.props
    let { selected, generalGross } = this.state
    // update label
    let balls = BallsStub.map(b => Object.assign({label: t(b.i18n), distance: GetDistance(b, selected || generalGross)}, b)) 
    // update lane color
    let dataType = this.props.dataType
    let connectIndex = -1
    switch (dataType) {
      case 'ettr':
        connectIndex = 5
        break
      case 'response_time':
        connectIndex = 2
        break
      case 'arrival_time':
        connectIndex = 3
        break
      default:
        warn('Invalid type')
    }
    balls[connectIndex] && (balls[connectIndex].connectPrevious = true) 
    return balls
  }

  onSliderChange = value => {
    let { dataType, updateDistribution } = this.props

    let distribution = this.getCurrentDistribution()
    if (!isEqual(value, distribution)) {
      updateDistribution(value, dataType)
    }
  }
  getMaxInSec() {
    let { distriMax, distriUnit } = this.state
    let factor = 60 // min
    if (distriUnit === 'hour') {
      factor = 3600
    } else if (distriUnit === 'day') {
      factor = 3600 * 24
    }
    warn(factor)
    return distriMax * factor 
  }

  updateDistributionMax = () => {
    let { dataType, updateDistribution } = this.props
    let distribution = this.getCurrentDistribution()
    let step = this.getMaxInSec() / (distribution.length - 1)
    updateDistribution(distribution.map((v, i) => i * step), dataType)
    warn("TODO: refresh 3 donuts")
  }

  getCurrentDistribution() {
    let { dataType, distributionEttr, distributionArrival, distributionResponse } = this.props
    let distribution
    if (dataType === 'ettr') {
      distribution = distributionEttr
    } else if (dataType === 'arrival_time') {
      distribution = distributionArrival
    } else { // response_time
      distribution = distributionResponse
    }
    return distribution
  }
  constructor(props) {
    super(props)
    EventBus.addEventListener('brief-data', this.mountBriefData )
    EventBus.addEventListener('detail-data', this.mountDetailData )
    EventBus.addEventListener('gross-data', this.mountGrossData )
    EventBus.addEventListener('distribution-data', this.mountDistribution )
    let { updateMeta } = this.props
    if (!cache.get('departments') || !cache.get('assettypes')) {
      updateMeta()
    }
  }
  componentWillMount() {
    this.loadAll()
    this.initDistributionMax()
  }

  componentWillReceiveProps(nextProps) {
    let { dataType, distributionEttr, distributionArrival, distributionResponse } = nextProps
    let distribution
    if (dataType === 'ettr') {
      distribution = distributionEttr
    } else if (dataType === 'arrival_time') {
      distribution = distributionArrival
    } else { // response_time
      distribution = distributionResponse
    }
    let max = last(distribution) 
    if (max !== this.state.distributionMax) {
      this.setState({ distributionMax: max})
    }
  }
  render() {
    let { briefs, details, selected, distriMax, distriUnit, generalGross } = this.state
    let { t, updateDisplayType, pagination, clientRect, display, dataType, 
      distributionEttr, distributionResponse, distributionArrival
    } = this.props
    let { left, right } = pagination
    let { outer_R, outer_r, inner_R, inner_r  } = ensureSize(clientRect.width, clientRect.height)
    let onClickDonut = this.onClickDonut
    let gross = selected || generalGross
    
    return (
      <div id="app-container" className="is-fullwidth">
        <Header/>
        <div className="chart-container is-fullwidth">
          <div className="full-chart container">

            <div className="display-select">{selectHelper(display, this.getDisplayOptions(), updateDisplayType)}</div>
            <Pagination current={CurrentPage(left.skip, left.top)} pageSize={left.top} total={left.total} 
              className="pager-left" onChange={this.onLeftPagerChange}/>
            <Pagination current={CurrentPage(right.skip, right.top)} pageSize={right.top} total={right.total} 
              className="pager-right" onChange={this.onRightPagerChange}/>
            <GearListChart 
              id="left-chart"
              ref="leftChart"
              startAngle={110} endAngle={250} 
              outerRadius={outer_R} innerRadius={outer_r}
              margin={7}
              onClick={this.clickLeftTooth}
              onMouseMove={this.showTooltip}
              onMouseLeave={this.showTooltip}
              clockwise={false}
              items={DataOrPlaceHolder(briefs, pagination.left.top)} 
              />
            <Orbit 
              id="center-chart" 
              radius={180}
              ballRadius={30}
              laneColor={this.getLaneColor()}
              balls={this.getBalls()}
            />
            <div id="legend-container">
              <h1 className="center-chart-title">{gross.name || t('all_chosen_assets')}</h1>
              <Donut 
                id="ettr"
                className={classnames("donut-chart-ettr", dataType === 'ettr' ? 'active' : '' )}
                baseColor={colors.purple}
                onClick={onClickDonut} 
                title={t('ettr')}
                rows={[GetDonutChartRow(t('average'), gross.ETTR)]}
              />
              <Donut 
                id="arrival_time"
                className={classnames("donut-chart-arrival", dataType === 'arrival_time' ? 'active' : '' )}
                baseColor={colors.green}
                onClick={onClickDonut} 
                title={t('arrival_time')}
                rows={[GetDonutChartRow(t('average'), gross.arrived)]}
              />
              <Donut 
                id="response_time"
                className={classnames("donut-chart-response", dataType === 'response_time' ? 'active' : '' )}
                baseColor={colors.yellow}
                onClick={onClickDonut} 
                title={t('response_time')}
                rows={[GetDonutChartRow(t('average'), gross.respond)]}
              />
            </div>
            <GearListChart 
              id="right-chart" 
              ref="rightChart"
              startAngle={290} endAngle={70}
              outerRadius={outer_R} innerRadius={outer_r}
              margin={3}
              onClick={this.clickRightTooth}
              onMouseMove={this.showTooltip}
              onMouseLeave={this.showTooltip}
              items={DataOrPlaceHolder(details, pagination.right.top)} />

            <div className="range-wrapper">
              <ReversedRange
                className={classnames('slider-ettr', dataType === 'ettr' ? '' : 'hidden')}
                value={distributionEttr}
                onChange={this.onSliderChange}
              />
              <ReversedRange
                className={classnames('slider-arrival_time', dataType === 'arrival_time' ? '' : 'hidden')}
                value={distributionArrival}
                onChange={this.onSliderChange}
              />
              <ReversedRange
                className={classnames('slider-response_time', dataType === 'response_time' ? '' : 'hidden')}
                value={distributionResponse}
                onChange={this.onSliderChange}
              />
              <InputNumber min={0} value={distriMax} size="small" onChange={val => this.setState({ distriMax: val})} />
              <RadioGroup value={distriUnit} size="small" onChange={e => this.setState({ distriUnit: e.target.value})}>
                <RadioButton value="min">{t('min')}</RadioButton>
                <RadioButton value="hour">{t('hour')}</RadioButton>
                <RadioButton value="day">{t('day')}</RadioButton>
              </RadioGroup>
              <Button type="primary" size="small" onClick={this.updateDistributionMax}>{t('submit')}</Button>
            </div>
          </div>
        </div>
      </div>
    )
  } 
}

export default translate()(withClientRect(App))
