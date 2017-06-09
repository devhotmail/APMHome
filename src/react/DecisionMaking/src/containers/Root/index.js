import React, { PureComponent } from 'react'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'
import FilterBar from 'dew-filterbar'

import RoleProvider from '#/components/RoleProvider'
import SizeProvider from '#/components/SizeProvider'
import PackChart from '#/components/PackChart'
import SidePanel from '#/components/SidePanel'
import StatusBar from '#/components/StatusBar'

import { currentYear } from '#/constants'

import styles from './styles.scss'

const Chart = SizeProvider(PackChart)

const roleFilterFn = isHead => n => {
  if (!isHead) return n.key !== 'groupby'
  else return true
}

class Root extends PureComponent {
  render () {
    const { loading, location, user } = this.props
    const { groupby, year } = location.query

    const filterOpts = [
      {
        type: 'select',
        key: 'year',
        value: year,
        options: [currentYear, currentYear + 1].map(n => ({
          id: n,
          name: n + '年预测'
        })),
        allowClear: false
      },
      {
        type: 'radio',
        key: 'groupby',
        value: groupby,
        options: [
          {
            id: 'dept',
            name: '按科室'
          },
          {
            id: 'type',
            name: '按设备类型'
          }
        ]
      }
    ].filter(roleFilterFn(user.isHead))

    return <div className={styles.container}>
      <div className={styles.filterBar}>
        <FilterBar options={filterOpts} onChange={this.handleFilterChange} />
      </div>
      {
        loading
          ? <div className={styles.statusWrapper}>
            <StatusBar type="loading" />
          </div>
          : <div className={styles.content}>
            <div className={styles.main}>
              <div className={styles.chartWrapper}>
                <Chart />
              </div>
            </div>
            <div className={styles.sidebar}>
              <SidePanel />
            </div>
          </div>
      }
    </div>
  }

  handleFilterChange = (payload) => {
    const { key, value } = payload
    if (key === 'range') {
      this.changeQuery({
        ...value
      })
    } else {
      this.changeQuery({
        [key]: value
      })
    }
  }

  changeQuery = (params: Object) => {
    const { dispatch, location } = this.props

    dispatch(routerRedux.push({
      pathname: '/',
      query: {
        ...location.query,
        ...params
      }
    }))
  }
}

export default connect(state => ({
  user: state.user.info,
  data: state.finance.data,
  loading: state.finance.loading
}))(RoleProvider(Root))
