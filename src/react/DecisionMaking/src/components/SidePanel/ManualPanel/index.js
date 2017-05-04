import React, { Component } from 'react'
import { Table, Icon } from 'antd'

import styles from './styles.scss'

const columns = [{
  title: 'Name',
  dataIndex: 'name',
  key: 'name',
}]

export default class ManualPanel extends Component {
  render () {
    const { config, focus } = this.props
    if (!config) return null

    const configListOne = config.filter(n => n.depth === 1)

    return (
      <div className={styles.manualPanel}>
        <ul>
          {
            configListOne.length ? configListOne.map((n, index) => {
              const cls = n.id === focus.data.id && n.depth === focus.depth
                ? 'active'
                : ''
              const onClick = e => this.props.dispatch({
                type: 'focus/data/setByInfo',
                payload: n
              })
              return <li key={index} className={cls} onClick={onClick}>{n.name}</li>
            }) : null
          }
        </ul>
      </div>
    )
  }
}
