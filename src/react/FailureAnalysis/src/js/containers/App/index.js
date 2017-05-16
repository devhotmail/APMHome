//@flow
/* eslint camelcase:0 */
import React, { Component } from 'react'
import { translate } from 'react-i18next'
import { connect } from 'react-redux'
import autobind from 'autobind-decorator'
import _ from 'lodash'
import { message } from 'antd'
import EventBus from 'eventbusjs'
import GearListChart from 'components/GearListChart'
import Header from 'containers/Header'
import Legend from 'components/Legend'
import LegendTable from 'components/LegendTable'
import Tooltip from 'components/Tooltip'
import Pagination from 'components/Pagination'
import withClientRect from '../../HOC/withClientRect'
import selectHelper from 'components/SelectHelper'
import { ParamUpdate, PageChange } from 'actions'
import colors from 'utils/colors'
import './app.scss'
import cache from 'utils/cache'
import { MetaUpdate } from 'actions'
import { DataTypeMapping } from 'services/api'
import { ToPrecentage } from 'utils/helpers'

const Placeholder = { strips: { color: '#F9F9F9', weight: 1, type: 'placeholder' } }
const Items = [
  {color: colors.blue, key: 'primary_failure'},
  {color: colors.gray, key: 'secondary_failure'},
]
const ParameterTypes = [
  {color: colors.purple, key: 'operation_rate'},
  {color: colors.yellow, key: 'ftfr', type: 'radio'},
  {color: colors.green, key: 'incident_count'},
]
const CheckBoxes = [
  {color: colors.red, key: 'same_period_last_year'},
]
const DisplayOptions = [
  { key: 'display_asset_type' },
  { key: 'display_brand' },
  { key: 'display_dept' },
]

function mergeItem(current, lastYear) {
  let copy = _.cloneDeep(current)
  copy.strips = copy.strips.concat(_.cloneDeep(lastYear.strips))
  return copy
}
function DataOrPlaceHolder(items, lastYearItems, placeholderSize) {
  // ignore placeholder and empty data
  if (items && items.length && items[0].strips.type !== 'placeholder') {
    if (lastYearItems && lastYearItems.length) {
      items = items.map( (item, i) => mergeItem(item, lastYearItems[i]))
    }
    return items
  }
  return App.getPlaceholder(placeholderSize)
}

function getCurrentPage(skip, top) {
  return Math.ceil(skip / top) || 1
}

function ensureSize(width, height) {
  width = _.clamp(width, 1000, 1500)
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
    updatePagination: (type, pageNumber) => {
      dispatch(PageChange(type, pageNumber))
    },
    fetchBriefs: (type, extraParam) => {
      dispatch({ type: 'update/briefs/' + type, data: extraParam })
    },
    fetchReasons: (data = {}) => dispatch({type: 'update/reasons', data }),
    updateMeta: () => dispatch(MetaUpdate())
  }
}

function mapState2Props(state) {
  let { parameters : { pagination, display, dataType, showLastYear } } = state
  return { pagination, display, dataType, showLastYear }
}

@connect(mapState2Props, mapDispatch2Porps)
@autobind
export class App extends Component<void, Props, void> {

  static getPlaceholder = _.memoize(count => _.range(count)
                           .map(() => Placeholder))
  state = {
    tooltipX: -861112,
    tooltipY: -861112,
    leftItems: [],
    centerItems: [],
    rightItems: [],
    lastYear: {
      leftItems: [],
      rightItems: [],
    },
    selectedDevice: {
      show: false,
      name: '',
      current: {},
      lastYear: {},
    },
  }

  showTooltip(evt) {
    let { t, dataType } = this.props 
    if (evt.type === 'mouseleave') {
      this.setState({ tooltipX: -861112, tooltipY: -861112 })
    } else {
      let stripData = evt.stripData
      let tooltip = stripData.type === 'placeholder' ? null : getLabel(stripData, dataType)
      this.setState({ tooltipX: evt.clientX, tooltipY: evt.clientY, tooltip: tooltip })
    }
    function getLabel(strip, type) {
      let unit = t('incident_count_unit')
      if (strip.data.count) {
        return strip.data.count + unit
      }
      if (strip.data.val) {
        let val = strip.data.val[DataTypeMapping[type]]
        return type === 'incident_count' ? (val + unit) : ToPrecentage(val)
      }
      return 'N.A.'
    }
  }

  clickLeftTooth(evt) {
    // 1, central chart: fetch reasons
    let { display } = this.props
    let key = evt.stripData.data.key
    let param = {}
    if (display === 'display_asset_type') {
      param.type = key.id
    } else if (display === 'display_brand') {
      param.supplier = key.id
    } else if (display === 'display_dept') {
      param.dept = key.id
    }
    this.props.fetchReasons(param)
    // 2 refresh lengend table
    this.showDevice(evt.strips)
    this.clearFocus('right')

    // 3 refesh right chart
    this.props.fetchBriefs('right', param)
  }

  clickRightTooth(evt) {
    this.showDevice(evt.strips)
    this.props.fetchReasons({ asset: evt.stripData.data.key.id })
    this.clearFocus('left')
  }

  showDevice(strips) {
    let [ current, lastYear ] = strips
    const device = {
      show: true,
      name: current.data.key.name,
      current: {
        'operation_rate': ToPrecentage(current.data.val.avail) ,
        'ftfr': ToPrecentage(current.data.val.ftfr),
        'incident_count': current.data.val.fix,
      },
      lastYear: {
        'operation_rate': lastYear ? ToPrecentage(lastYear.data.val.avail) : '-',
        'ftfr': lastYear ? ToPrecentage(lastYear.data.val.ftfr) : '-',
        'incident_count': lastYear ? lastYear.data.val.fix : '-',
      }
    }
    this.setState({ selectedDevice: device })
  }

  hideDevice() {
    let { selectedDevice } = this.state
    selectedDevice.show = false
    this.setState({ selectedDevice: selectedDevice })
  }

  loadAll() {
    let { fetchBriefs, fetchReasons } = this.props
    fetchBriefs('left')
    fetchReasons({})
    fetchBriefs('right')
  }

  onRightPagerChange = value => {
    this.props.updatePagination('right', value)
  }

  onLeftPagerChange = value => {
    this.props.updatePagination('left', value)
  }

  getDisplayOptions() {
    return DisplayOptions.map(o => ({ key: o.key, label: this.props.t(o.key)}))
  }

  mountBriefData(evt) {
    let { t } = this.props
    let [ current, lastYear ] = evt.target
    if (!current.length) {
      let target = t(current.type === 'left' ? 'group_info' : 'asset_info')
      message.info(target + ': ' + t('no_more_data'))
      return
    }
    if (current.type === 'left') {
      this.setState({ leftItems: current, lastYear: { leftItems: lastYear, rightItems: this.state.lastYear.rightItems } })
    } else if (current.type === 'right'){
      this.setState({ rightItems: current, lastYear: { rightItems: lastYear, leftItems: this.state.lastYear.leftItems } })
    }
    this.clearFocus(current.type)
  }

  mountReason(evt) {
    let { t } = this.props
    let reasons = evt.target
    if (reasons.length) {
      this.setState({ centerItems: reasons })
    } else {
      message.info(t('failure_cause') + ': ' + t('no_more_data'))
    }
  }

  clearFocus(type) {
    if (type === 'left') {
      this.refs.leftChart.clearFocus()
    } else if (type === 'right') {
      this.refs.rightChart.clearFocus()
    }
  }

  constructor(props) {
    super(props)
    EventBus.addEventListener('brief-data', this.mountBriefData )
    EventBus.addEventListener('reason-data', this.mountReason )
    let { updateMeta } = this.props
    if (!cache.get('departments') || !cache.get('assettypes')) {
      updateMeta()
    }
    
  }

  componentWillMount() {
    this.loadAll()
  }

  render() {
    let { tooltipX, tooltipY, tooltip, leftItems, centerItems, rightItems, lastYear, selectedDevice } = this.state
    let { updateDisplayType, pagination, clientRect, display } = this.props
    let { left, right } = pagination
    let { outer_R, outer_r, inner_R, inner_r  } = ensureSize(clientRect.width, clientRect.height)

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
              items={DataOrPlaceHolder(leftItems, lastYear.leftItems, pagination.left.top)} 
              />
            <GearListChart
              id="center-chart"
              startAngle={90} endAngle={90} 
              outerRadius={Math.max(inner_R, 200)} innerRadius={Math.max(inner_r, 160)}
              margin={8}
              onMouseMove={this.showTooltip}
              onMouseLeave={this.showTooltip}
              items={DataOrPlaceHolder(centerItems, null, 12)} />
            <div id="legend-container">
              <Legend items={Items}>
                <LegendTable items={ParameterTypes} selectedDevice={selectedDevice.show && selectedDevice} checkBoxes={CheckBoxes}/>
              </Legend>
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
              items={DataOrPlaceHolder(rightItems, lastYear.rightItems, pagination.right.top)} />

          </div>
          { tooltip && <Tooltip mouseX={tooltipX} mouseY={tooltipY} offsetY={-13} anchor="hcb">
            <div style={{color: tooltip && tooltip.color}} className="tooltip-content">{tooltip}</div>
          </Tooltip> }
        </div>
      </div>
    )
  } 
}

export default translate()(withClientRect(App))
