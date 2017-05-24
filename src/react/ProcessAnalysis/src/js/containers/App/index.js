//@flow
/* eslint camelcase:0 */
import React, { Component } from 'react'
import { translate } from 'react-i18next'
import { connect } from 'react-redux'
import autobind from 'autobind-decorator'
import { last, range, memoize, isEqual, debounce, clamp } from 'lodash-es'
import { message } from 'antd'
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
import { MetaUpdate } from 'actions'
import classnames from 'classnames'
import colors from 'utils/colors'
import { BriefConv, DetailConv } from 'converters'
import { log, warn } from 'utils/logger'
import './app.scss'

const Placeholder = { strips: { color: '#F9F9F9', weight: 1, type: 'placeholder' } }
const DisplayOptions = [
  { key: 'display_asset_type' },
  { key: 'display_brand' },
  { key: 'display_dept' },
]
const BallsStub = [
  { key: 'report_incident', distance: 0 },
  { key: 'dispatch_incident', distance: 45 },
  { key: 'accept_incident', distance: 123 },
  { key: 'signin_incident', distance: 190 },
  { key: 'fixing_incident', distance: 230 },
  { key: 'close_incident', distance: 300 }
]

function DataOrPlaceHolder(items, placeholderSize) {
  // ignore placeholder and empty data
  if (items && items.length && items[0].strips.type !== 'placeholder') {
    return items
  }
  return App.getPlaceholder(placeholderSize)
}

function getCurrentPage(skip, top) {
  return Math.ceil(skip / top) || 1
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
    updateDistribution: (value) => {
      dispatch(ParamUpdate('distribution', value))
    },
    fetchBriefs: extraParam => {
      dispatch({ type: 'get/briefs', data: extraParam })
    },
    fetchDetails: extraParam => {
      dispatch({ type: 'get/details', data: extraParam })
    },
    fetchGross: extraParam => {
      // todo
    },
    updateMeta: () => dispatch(MetaUpdate())
  }
}

function mapState2Props(state) {
  let { parameters : { pagination, display, dataType, distribution } } = state
  return { pagination, display, dataType, distribution }
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
    selected: {},
    ettrSummary: [],
    arrivalSummary: [],
    briefsonseSummary: []
  }

  clickLeftTooth(evt) {
    // todo
    this.clearFocus('right')
  }

  clickRightTooth(evt) {
    // todo
    this.clearFocus('left')
  }
  onRightPagerChange = value => {
    this.props.updatePagination('detail', value)
  }

  onLeftPagerChange = value => {
    this.props.updatePagination('brief', value)
  }

  onClickDonut(evt) {
    let id = evt.currentTarget.id
    if (id !== this.props.dataType) {
      this.props.updateDataType(id)
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
        throw Error('Invalid dataType, no corbriefsondent color')
    }
  }
  getBalls() {
    let { t } = this.props
    // update label
    let balls = BallsStub.map(b => Object.assign({label: t(b.key)}, b)) 
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
    let { distribution, updateDistribution } = this.props
    if (!isEqual(value, distribution)) {
      updateDistribution(value)
    }
  }

  updateDistributionMax = debounce(value => {
    let { distribution, updateDistribution } = this.props
    let step = value / (distribution.length - 1)
    updateDistribution(distribution.map((v, i) => i * step))
  }, 800)

  constructor(props) {
    super(props)
    EventBus.addEventListener('brief-data', this.mountBriefData )
    EventBus.addEventListener('detail-data', this.mountDetailData )
    EventBus.addEventListener('distribution-data', this.mountDistribution )
    let { updateMeta } = this.props
    if (!cache.get('departments') || !cache.get('assettypes')) {
      updateMeta()
    }
  }

  componentWillMount() {
    this.loadAll()
  }

  render() {
    let { briefs, details, ettrSummary, briefsonseSummary, arrivalSummary } = this.state
    let { t, updateDisplayType, pagination, clientRect, display, dataType, distribution } = this.props
    let { left, right } = pagination
    let { outer_R, outer_r, inner_R, inner_r  } = ensureSize(clientRect.width, clientRect.height)
    let onClickDonut = this.onClickDonut
    
    return (
      <div id="app-container" className="is-fullwidth">
        <Header/>
        <div className="chart-container is-fullwidth">
          <div className="full-chart container">

            <div className="display-select">{selectHelper(display, this.getDisplayOptions(), updateDisplayType)}</div>
            <Pagination current={getCurrentPage(left.skip, left.top)} pageSize={left.top} total={left.total} 
              className="pager-left" onChange={this.onLeftPagerChange}/>
            <Pagination current={getCurrentPage(right.skip, right.top)} pageSize={right.top} total={right.total} 
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
              <h1 className="center-chart-title">Title</h1>
              <Donut 
                id="ettr"
                className={classnames("donut-chart-ettr", dataType === 'ettr' ? 'active' : '' )}
                baseColor={colors.purple}
                onClick={onClickDonut} 
                title={t('ettr')}
                rows={ettrSummary}
              />
              <Donut 
                id="arrival_time"
                className={classnames("donut-chart-arrival", dataType === 'arrival_time' ? 'active' : '' )}
                baseColor={colors.green}
                onClick={onClickDonut} 
                title={t('arrival_time')}
                rows={arrivalSummary}
              />
              <Donut 
                id="response_time"
                className={classnames("donut-chart-response", dataType === 'response_time' ? 'active' : '' )}
                baseColor={colors.yellow}
                onClick={onClickDonut} 
                title={t('response_time')}
                rows={briefsonseSummary}
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
              {/*<input type="number" onChange={evt => this.updateDistributionMax(evt.target.value)} />*/}
              <ReversedRange
                className={'slider-' + dataType}
                value={distribution}
                onChange={this.onSliderChange}
              />
            </div>

            

          </div>
        </div>
      </div>
    )
  } 
}

export default translate()(withClientRect(App))
