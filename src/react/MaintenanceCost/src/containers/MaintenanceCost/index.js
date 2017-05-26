import React from 'react'
import { connect } from 'dva'
import Chart from '#/components/Chart'
import styles from './index.scss'

class MaintenanceCost extends React.PureComponent {
  render() {
    return <Chart />
  }
}

export default
connect(({ profit, filters }) => ({ profit, filters }))(MaintenanceCost)
