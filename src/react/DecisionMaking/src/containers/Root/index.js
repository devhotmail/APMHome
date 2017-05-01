import React, { Component } from 'react'
import { connect } from 'dva'

import Chart from '#/components/Chart'
import PackChart from '#/components/PackChart'

// import Wrapper from './wrapper'
import FakeWrapper from './fakeWrapper'

const BubleChart = FakeWrapper(PackChart)
// const BubleChart = FakeWrapper(Chart)

class Root extends Component {
  render() {
    return <BubleChart />
  }
}

export default connect(state => ({
  data: state.financial.data
}))(Root)
