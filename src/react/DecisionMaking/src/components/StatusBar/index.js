/* @flow */
import React, { Component } from 'react'

import ReactIcon from '../ReactIcon'

import styles from './styles.scss'

const statusSet = {
  empty: {
    icon: 'inbox',
    desc: '暂无可用设备数据'
  },
  error: {
    icon: 'exclamation',
    desc: '数据加载出错，请尝试刷新页面'
  },
  loading: {
    icon: 'spinner',
    desc: '数据加载中...'
  }
}

type PropT = {
  type: string
}

export default class StatusBar extends Component<*, PropT, *> {
  render () {
    const { type } = this.props
    if (!type) return null
    const suit = statusSet[type]
    if (!suit) return null
    return (
      <div className={styles.statusBar}>
        <ReactIcon className={`${styles.icon} ${styles[type]}`} symbolId={suit.icon} />
        <div className={styles.desc}>{suit.desc}</div>
      </div>
    )
  }
}
