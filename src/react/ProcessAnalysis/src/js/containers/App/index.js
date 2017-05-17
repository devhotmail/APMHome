//@flow
/* eslint camelcase:0 */
import React, { Component } from 'react'
import { translate } from 'react-i18next'
import { connect } from 'react-redux'
import autobind from 'autobind-decorator'
import _ from 'lodash'
import { message } from 'antd'
import EventBus from 'eventbusjs'
import GearListChart from 'react-gear-list-chart'
import Header from 'containers/Header'
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
const DisplayOptions = [
  { key: 'display_asset_type' },
  { key: 'display_brand' },
  { key: 'display_dept' },
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
  let { parameters : { pagination, display, dataType } } = state
  return { pagination, display, dataType }
}

@connect(mapState2Props, mapDispatch2Porps)
@autobind
export class App extends Component<void, Props, void> {

  static getPlaceholder = _.memoize(count => _.range(count)
                           .map(() => Placeholder))
  state = {
    leftItems: [],
    centerItems: [],
    rightItems: []
  }

  clickLeftTooth(evt) {

  }

  clickRightTooth(evt) {

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
    let [ current ] = evt.target
    if (!current.length) {
      let target = t(current.type === 'left' ? 'group_info' : 'asset_info')
      message.info(target + ': ' + t('no_more_data'))
      return
    }
    if (current.type === 'left') {
      this.setState({ leftItems: current })
    } else if (current.type === 'right'){
      this.setState({ rightItems: current })
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
    let { leftItems, rightItems } = this.state
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
              items={DataOrPlaceHolder(leftItems, pagination.left.top)} 
              />
            <div id="legend-container">
              here goes orbit chart
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
              items={DataOrPlaceHolder(rightItems, pagination.right.top)} />

          </div>
        </div>
      </div>
    )
  } 
}

export default translate()(withClientRect(App))
