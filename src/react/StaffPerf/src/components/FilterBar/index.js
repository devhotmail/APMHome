/* @flow */
import React, { Component } from 'react'
import { Menu, Dropdown, Button, Icon } from 'antd'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'

import { currentYear } from '#/constants'

import styles from './styles.scss'

@connect(state => ({
  user: state.user.info
}))
export default class FilterBar extends Component {
  render () {
    const menu = <Menu onClick={this.handleMenuClick}>
      {
        [currentYear, currentYear + 1].map(year => {
          return <Menu.Item key={year}>
            <span>{year}</span>
          </Menu.Item>
        })
      }
    </Menu>

    const { location: { query }} = this.props

    return (
      <div className="flex flex--justify-content--space-between p-a-1">
        <div className={styles.year}>
          <Dropdown overlay={menu} trigger={['click']} placement='bottomCenter'>
            <Button>
              { query.year || currentYear } <Icon type="down" />
            </Button>
          </Dropdown>
        </div>
        <div className={styles.groupby}>
        </div>
      </div>
    )
  }

  handleMenuClick = (e: Event) => {
    this.handlePush({ year: e.key })
  }

  handlePush = (newQuery: Object) => {
    const { location, dispatch } = this.props

    dispatch(routerRedux.push({
      pathname: '/',
      query: {
        ...location.query,
        ...newQuery
      }
    }))
  }
}
