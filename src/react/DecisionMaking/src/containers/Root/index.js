import React, { Component } from 'react'
import { connect } from 'dva'

import SizeProvider from '#/components/SizeProvider'
import PackChart from '#/components/PackChart'
import SidePanel from '#/components/SidePanel'

// import Wrapper from './wrapper'
import FakeWrapper from './fakeWrapper'

import styles from './styles.scss'

const BubleChart = FakeWrapper(SizeProvider(PackChart))
// const BubleChart = FakeWrapper(Chart)

class Root extends Component {
  render() {
    return <div className={styles.container}>
      <div className={styles.main}>
        <BubleChart />
      </div>
      <div className={styles.sidebar}>
        <SidePanel />
      </div>
    </div>
  }
}

export default connect(state => ({
  data: state.financial.data
}))(Root)
