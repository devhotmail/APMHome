import React, { Component } from 'react'
import { Table, Icon } from 'antd'

import ConfigDept from '../ConfigDept'
import ConfigType from '../ConfigType'

import styles from './styles.scss'

export default class ManualPanel extends Component {
  render () {
    const { config, focus } = this.props
    if (!config) return null
    if (!focus) return null

    const depths = config.map(n => n.depth)
    .filter((n, index, arr) => arr.indexOf(n) === index)

    return (
      <div className={styles.manualPanel}>
        <div className="lead m-b-1">使用率预测</div>
        <div className="font-small text-muted">基于系统自动预测的增长进行手工调节</div>
        {
          depths.length === 2
            ? <ConfigDept depths={depths} setFocus={this.handleSetFocus} {...this.props} />
            : null
        }
        {
          depths.length === 1
            ? <ConfigType depths={depths} setFocus={this.handleSetFocus} {...this.props} />
            : null
        }
      </div>
    )
  }

  handleSetFocus = cursor =>  e => {
    this.props.dispatch({
      type: 'focus/data/set',
      payload: cursor
    })
  }
}
