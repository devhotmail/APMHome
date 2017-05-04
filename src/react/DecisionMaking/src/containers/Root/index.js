import React, { Component } from 'react'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'

import RoleProvider from '#/components/RoleProvider'
import SizeProvider from '#/components/SizeProvider'
import PackChart from '#/components/PackChart'
import SidePanel from '#/components/SidePanel'
import FilterBar from '#/components/FilterBar'

import styles from './styles.scss'

const Chart = SizeProvider(PackChart)
const Panel = connect(state => ({
  focus: state.focus,
  config: state.config.data
}))(SidePanel)

class Root extends Component {
  render() {
    return <div className={styles.container}>
      <div className={styles.main}>
        <div className={styles.filterBar}>
          <FilterBar {...this.props} />
        </div>
        <div className={styles.chartWrapper}>
          <Chart {...this.props} />
        </div>
      </div>
      <div className={styles.sidebar}>
        <Panel />
      </div>
    </div>
  }
}

export default connect(state => ({
  data: state.financial.data
}))(RoleProvider(Root))
