// @flow
import React, { PureComponent } from 'react'
import _ from 'lodash'
import { connect } from 'react-redux'
import { translate } from 'react-i18next'
import autobind from 'autobind-decorator'
import { DatePicker } from 'antd'
import SelectHelper from 'components/SelectHelper'
import { ParamUpdate, ParamType } from 'actions'
import './header.scss'

const RangePicker = DatePicker.RangePicker

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
    onOrderChange: (data) => dispatch(ParamUpdate('order', data)),
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
      {key: 'operation_rate', label: t('operation_rate')},
      {key: 'ftfr', label: t('ftfr')},
      {key: 'incident_count', label: t('incident_count')},
    ]
  }
  _filtersDept() {
    let { t, departments = []} = this.props
    return [
      { key: 'all_dept', label: t('all_dept') },
    ].concat(departments.map(t => ({ key: String(t.id), label: t.name })))
    
  }
  _filtersAssetType() {
    let { t, assetTypes = [] } = this.props
    return [
      { key: 'all_asset_type', label: t('all_asset_type') },
    ].concat(assetTypes.map(d => ({ key: String(d.id), label: d.name})))
  }

  render() {
    let { t, org, name, period, filterBy, orderBy,
      onFilterChange, onOrderChange, onPeriodChange
    } = this.props
    return (<nav id="header" className="header level">

      <div className="nav-left">
        <div className="nav-item">
          <RangePicker
            showTime
            format="YYYY-MM-DD"
            defaultValue={[period.from, period.to]}
            onChange={onPeriodChange}
          />
        </div>
      </div>
      <div className="nav-center">
        <div className="nav-item">{t('filter_by')}</div>
        <div className="nav-item">
          { SelectHelper(filterBy['assettype'], this._filtersAssetType(), onFilterChange('assettype')) }
        </div>
        <div className="nav-item">
          { SelectHelper(filterBy['dept'], this._filtersDept(), onFilterChange('dept')) }
        </div>
      </div>
      <div className="nav-right">
        <div className="nav-item">{t('sort_by')}</div>
        <div className="nav-item">
          { SelectHelper(orderBy, this._sortOptions(), onOrderChange) }
        </div>
        <div className="nav-item">{org}</div>
        <div className="nav-item">{name}</div>
      </div>

    </nav>)
  }
}

export default translate()(Header)