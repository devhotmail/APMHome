import React, { Component } from 'react'
import { Table, Icon, Button } from 'antd'

import ConfigDept from '../ConfigDept'
import ConfigType from '../ConfigType'

import styles from './styles.scss'

export default class ManualPanel extends Component {
  render () {
    const { config, focus } = this.props
    if (!config) return null
    if (!focus) return null

    const root = config[0]
    const { depth, height } = root
    const visibleConfig = config.filter(n => !~[depth, height].indexOf(n.depth))

    const depths = visibleConfig.map(n => n.depth)
    .filter((n, index, arr) => arr.indexOf(n) === index)

    return (
      <div className={styles.manualPanel}>
        <div className="lead m-b-1">使用率预测</div>
        <div className="m-b-1 font-small text-muted">基于系统自动预测的增长进行手工调节</div>
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
        <div className="m-y-1 text-center">
          <Button
            size="large"
            type="primary"
            style={{width: 120}}
            onClick={this.handleSumbit}>
            确认预期
          </Button>
        </div>
      </div>
    )
  }

  handleSumbit = () => {
    this.props.dispatch({
      type: 'config/changes/submit'
    })
  }

  handleSetFocus = cursor => {
    this.props.dispatch({
      type: 'focus/cursor/set',
      payload: cursor
    })
  }
}
