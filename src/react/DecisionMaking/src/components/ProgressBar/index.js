/* @flow */
import React, { Component } from 'react'
import { Progress } from 'antd'

import styles from './styles.scss'

type PropsT = {
  color: string,
  title: string,
  percent: number,
  textDesc: React$Element
}

export default class ProgressBar extends Component<*, PropsT, *> {
  static defaultProps = { color: '#6b6b6b' }

  render () {
    const { color, title, percent, textDesc } = this.props
    return (
      <div style={{color}} className={styles.progressBar}>
        <div className={styles.title}>{title}</div>
        <div className={styles.barWrapper}>
          <div style={{width: `${percent*100}%`}} className={styles.bar}></div>
        </div>
        <div className={styles.desc}>{textDesc}</div>
      </div>
    )
  }
}
