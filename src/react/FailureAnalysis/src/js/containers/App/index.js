//@flow
import React, { Component } from 'react'
import { translate } from 'react-i18next'
import { connect } from 'react-redux'
import autobind from 'autobind-decorator'
import _ from 'lodash'
import Radium from 'radium'
import EventBus from 'eventbusjs'
import GearListChart from 'components/GearListChart'
import Header from 'containers/Header'
import Legend from 'components/Legend'
import LegendTable from 'components/LegendTable'
import Tooltip from 'components/Tooltip'
import Pagination from 'components/Pagination'
import withClientRect from '../../HOC/withClientRect'
import selectHelper from 'components/SelectHelper'
import { ParamUpdate } from 'actions'
import colors from 'utils/colors'
import { GenerateTeethData as GTD, RandomInt } from 'utils/helpers'
import './app.scss'
import cache from 'utils/cache'
import { MetaUpdate } from 'actions'

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

function calc(width, height) {
  if (width < 1000) {
    width = 1000
  }
  if (width > 1500) {
    width = 1500
  }
  if (height < 900) {
    width = 1100
  }
  return {
    outer_R: width * .4125 / 1.5,
    outer_r: width * .3 / 1.5,
    inner_R: width * .2625 / 1.5,
    inner_r: width * .2125 / 1.5
  }
}

function mapDispatch2Porps(dispatch) {
  return {
    updateDisplayType: (value) => {
      dispatch(ParamUpdate('display', value.key))
    },
    fetchBriefs: (type) => {
      dispatch({ type: 'update/briefs/' + type })
    },
    fetchReasons: () => dispatch({type: 'update/reasons'}),
    updateMeta: () => dispatch(MetaUpdate())
  }
}

function mapState2Props(state) {
  let { parameters : { pagination } } = state
  return { pagination }
}

@connect(mapState2Props, mapDispatch2Porps)
@autobind
export class App extends Component<void, Props, void> {
  static GenerateTeethData = _.memoize(GTD)
  static getPlaceholder = _.memoize(count => _.range(count)
                           .map(() => Placeholder))
  state = {
    tooltipX: -861112,
    tooltipY: -861112,
    leftItems: [],
    centerItems: [],
    rightItems: [],
    selectedDevice: null,
  }

  showTooltip(evt) {
    if (evt.type === 'mouseleave') {
      this.setState({ tooltipX: -861112, tooltipY: -861112 })
    } else {
      let stripData = evt.stripData
      let tooltip = stripData.type === 'placeholder' ? null : { label: getLabel(stripData) }
      this.setState({ tooltipX: evt.clientX, tooltipY: evt.clientY, tooltip: tooltip })
    }
    function getLabel(strip) {
      
      if (strip.data.count) {
        return strip.data.count
      }
      if (strip.data.val) {
        return (strip.data.val.avail * 100).toFixed(2) + '%'
      }
      return 'N.A.'
    }
  }

  device() {
    const device = {
      name: 'CT-1',
      data: {
        current: {
          'operation_rate': "80%",
          'ftfr': '86%',
          'incident_count': '32'
        },
        lastYear: {
          'operation_rate': "43%",
          'ftfr': '23%',
          'incident_count': '20'
        }
      }
    }
    this.setState({ selectedDevice: this.state.selectedDevice === null ? device: null})
  }
  load() {
    let {fetchBriefs, fetchReasons} = this.props
    fetchBriefs('left')
    fetchReasons()
    fetchBriefs('right')
  }
  loadDummy() {
    setTimeout(() => {
      let items = GTD(6, 'bar', 3, [colors.purple, colors.green, colors.yellow])
      items.forEach(item => _.orderBy(item.strips, ['color']))
      this.setState({leftItems: items})
    }, RandomInt(600))
    setTimeout(() => {
      let items = GTD(12, 'bar', 1, [colors.blue, colors.gray])
      this.setState({centerItems: _.orderBy(items, _ => _.strips[0].color, ['desc'])})
    }, RandomInt(600))
    setTimeout(() => {
      let items = GTD(16, 'spokerib', 3, [colors.purple, colors.green, colors.yellow])
      items.forEach(item => _.orderBy(item.strips, ['color']))
      this.setState({rightItems: items})
    }, RandomInt(600))
  }
  _onRightPagerChange() {
    //todo
  }
  _onLeftPagerChange() {
    // todo
  }
  _getDisplayOptions() {
    return DisplayOptions.map(o => ({ key: o.key, label: this.props.t(o.key)}))
  }
  loadBriefData(evt) {
    let briefs = evt.target
    if (briefs.type === 'left') {
      this.setState({ leftItems: briefs })
    } else if (briefs.type === 'right'){
      this.setState({ rightItems: briefs })
    }
  }
  loadReason(evt) {
    let reasons = evt.target
    this.setState({ centerItems: reasons })
  }
  constructor(props) {
    super(props)
    EventBus.addEventListener('brief-data', this.loadBriefData )
    EventBus.addEventListener('reason-data', this.loadReason )
    let { updateMeta } = this.props
    if (!cache.get('departments') || !cache.get('assettypes')) {
      updateMeta()
    }
  }
  render() {
    let { tooltipX, tooltipY, tooltip, leftItems, centerItems, rightItems, selectedDevice } = this.state
    let { updateDisplayType, pagination, fetchBriefs, fetchReasons, clientRect } = this.props
    let { outer_R, outer_r, inner_R, inner_r  } = calc(clientRect.width, clientRect.height)
    if (!leftItems || leftItems.length === 0) {
      fetchBriefs('left')
    }
    if (!centerItems || centerItems.length === 0) {
      fetchReasons()
    }
    if (!rightItems || rightItems.length === 0) {
      fetchBriefs('right')
    }
    return (
      <div id="app-container" className="is-fullwidth">
        <Header/>
        <div className="chart-container is-fullwidth">
          <div className="full-chart container">

            <div className="display-select">{selectHelper('display_asset_type', this._getDisplayOptions(), updateDisplayType)}</div>
            {/*<Pagination current={1} pageSize={10} total={100} className="pager-left" onChange={this._onLeftPagerChange}/>
            <Pagination current={1} pageSize={10} total={100} className="pager-right" onChange={this._onRightPagerChange}/>*/}
            <GearListChart 
              id="left-chart"
              startAngle={110} endAngle={250} 
              outerRadius={outer_R} innerRadius={outer_r}
              margin={7}
              onClick={() => null}
              onMouseMove={this.showTooltip}
              onMouseLeave={this.showTooltip}
              clockwise={false}
              items={leftItems || App.getPlaceholder(pagination.left.top)} 
              />
            <GearListChart
              id="center-chart"
              startAngle={90} endAngle={90} 
              outerRadius={Math.max(inner_R, 200)} innerRadius={Math.max(inner_r, 160)}
              margin={8}
              onMouseMove={this.showTooltip}
              onMouseLeave={this.showTooltip}
              items={centerItems || App.getPlaceholder(12)} />
            <div id="legend-container">
              <Legend items={Items}>
                <LegendTable items={ParameterTypes} selectedDevice={selectedDevice} checkBoxes={CheckBoxes}/>
              </Legend>
            </div>
            <GearListChart 
              id="right-chart" 
              startAngle={290} endAngle={70} 
              outerRadius={outer_R} innerRadius={outer_r}
              margin={3}
              onClick={this.device}
              onMouseMove={this.showTooltip}
              onMouseLeave={this.showTooltip}
              items={rightItems || App.getPlaceholder(pagination.right.top)} />

          </div>
          { tooltip && tooltip.label && <Tooltip mouseX={tooltipX} mouseY={tooltipY} anchor="rb">
            <div style={{color: tooltip && tooltip.color}} className="tooltip-content">{tooltip.label}</div>
          </Tooltip>}
        </div>
        <button onClick={this.device}>Select Device</button>
        <button onClick={this.load}>Load Data</button>
        <button onClick={this.loadDummy}>Load Dummy</button>
      </div>
    )
  } 
}

export default translate()(withClientRect(App))
