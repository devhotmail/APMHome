/* @flow */
import React, { Component } from 'react'
import { Menu, Dropdown, Button, Icon, Radio } from 'antd'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'

import styles from './styles.scss'

@connect(state => ({
  user: state.user.info
}))
export default class FilterBar extends Component {
  render () {
    const { user: { isHead }, location: { query }, className} = this.props

    return (
      <div className={"flex flex--justify-content--space-between p-a-1 " + className}>
        <Radio.Group
          onChange={e => console.log(e)}
          className={styles['radio-group']}
          defaultValue="history"
          size="large"
        >
          <Radio.Button value="history">
            历史
          </Radio.Button>
          <Radio.Button value="future">
            预测
          </Radio.Button>
        </Radio.Group>
      </div>
    )
  }
}
