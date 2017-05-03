import React, { Component } from 'react'
import { connect } from 'dva'

import SizeProvider from '#/components/SizeProvider'
import PackChart from '#/components/PackChart'
import SidePanel from '#/components/SidePanel'

// import Wrapper from './wrapper'

import styles from './styles.scss'

const Chart = SizeProvider(PackChart)
const Panel = connect(state => ({
  focus: state.focus.data,
  config: state.financial.config
}))(SidePanel)

class Root extends Component {
  render() {
    return <div className={styles.container}>
      <div className={styles.main}>
        <Chart {...this.props} />
      </div>
      <div className={styles.sidebar}>
        <Panel />
      </div>
    </div>
  }
}

export default connect(state => ({
  data: state.financial.data
}))(Root)
