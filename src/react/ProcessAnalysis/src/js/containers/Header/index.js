// @flow
import React, { PureComponent } from 'react'
import { connect } from 'react-redux'
import { translate } from 'react-i18next'
import autobind from 'autobind-decorator'
import { DatePicker } from 'antd'
import SelectHelper from 'components/SelectHelper'
import { ParamUpdate } from 'actions'
import getRangePresets, { DisabledDate } from 'utils/getRangePresets'
import './header.scss'

const RangePicker = DatePicker.RangePicker
const DatePresets = getRangePresets([
  'oneWeek', 'oneMonth', 'oneYear', 'currentMonth',
  'yearBeforeLast', 'lastYear', 'currentYear'
])

const Ranges = DatePresets.reduce((prev, cur) => {
  prev[cur.text] = [
    cur.start,
    cur.end
  ]
  return prev
}, {})

function mapState2Props(state) {
  let { 
    parameters: { filterBy, orderBy, period },
    context: { org, name },
    meta: { departments, assetTypes }
  } = state
  return { filterBy, orderBy, period, org, name, departments, assetTypes }
}
function mapDispatch2Props(dispatch) {
  return {
    onFilterChange: (type) => (data) => dispatch(ParamUpdate('filter', { type, data })),
    onPeriodChange: (data) => dispatch(ParamUpdate('period', data))
  }
}

@connect(mapState2Props, mapDispatch2Props)
@autobind
export class Header extends PureComponent {

  _defaultOption(key) {
    return this.props.t(key)
  }
  _sortOptions() {
    let { t } = this.props
    return [
      { key: 'response_time', label: t('response_time') },
      { key: 'ettr', label: t('ettr') },
      { key: 'arrival_time', label: t('arrival_time') },
      { key: 'composite_order', label: t('composite_order') },
    ]
  }
  filtersDept() {
    let { t, departments = [] } = this.props
    return [
      { key: 'all_dept', label: t('all_dept') },
    ].concat(departments.map(t => ({ key: String(t.id), label: t.name })))
    
  }
  filtersAssetType() {
    let { t, assetTypes = [] } = this.props
    return [
      { key: 'all_asset_type', label: t('all_asset_type') },
    ].concat(assetTypes.map(d => ({ key: String(d.id), label: d.name})))
  }

  render() {
    let { t, period, filterBy,
      onFilterChange, onPeriodChange
    } = this.props
    return (<nav id="header" className="header level">

      <div className="nav-left">
        <div className="nav-item">
          <RangePicker
            showTime
            format="YYYY-MM-DD"
            ranges={Ranges}
            disabledDate={DisabledDate}
            defaultValue={[period.from, period.to]}
            onChange={onPeriodChange}
          />
        </div>
      </div>
      <div className="nav-center">
        <div className="nav-item">{t('filter_by')}</div>
        <div className="nav-item">
          { SelectHelper(filterBy['assettype'], this.filtersAssetType(), onFilterChange('assettype')) }
        </div>
        <div className="nav-item">
          { SelectHelper(filterBy['dept'], this.filtersDept(), onFilterChange('dept')) }
        </div>
      </div>

    </nav>)
  }
}

export default translate()(Header)