import React, { Component } from 'react'
import { connect } from 'dva'

import RoleProvider from '#/components/RoleProvider'
import SizeProvider from '#/components/SizeProvider'
import StackChart from '#/components/StackChart'
import FilterBar from '#/components/FilterBar'
import StatusBar from '#/components/StatusBar'

import styles from './styles.scss'

const Chart = SizeProvider(StackChart)

class Root extends Component {
  render () {
    const { loading } = this.props
    return <div className={styles.container}>
      <div className={styles.filterBar}>
        <FilterBar {...this.props} />
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
          </div>
      }
    </div>
  }
}

export default connect(state => ({
  data: state.finance.data,
  loading: state.finance.loading
}))(RoleProvider(Root))
