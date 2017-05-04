/* @flow */
import React, { Component } from 'react'
import { Menu, Dropdown, Button, Icon } from 'antd'
import { routerRedux } from 'dva/router'

import { currentYear } from '#/constants'

import styles from './styles.scss'

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

    const { query } = this.props.location
    // const { isHead } = this.props.userInfo

    return (
      <div className="flex flex--justify-content--space-between p-a-1">
        <Dropdown overlay={menu} trigger={['click']} placement='bottomCenter'>
          <Button>
            { query.year || currentYear } <Icon type="down" />
          </Button>
        </Dropdown>
        {this.renderGroupBy(query)}
      </div>
    )
  }

  renderGroupBy = (query: Object) => {
    const groupbyOpts = [
      {
        key: 'dept',
        text: '按科室',
        onClick: this.handleGroupbyClick('dept')
      },
      {
        key: 'type',
        text: '按设备类型',
        onClick: this.handleGroupbyClick('type')
      }      
    ]

    return <div>
      {
        groupbyOpts.map(({key, text, onClick}) => {
          return (
            <Button
              key={key}
              className="m-l-1"
              type={query.groupby === key ? 'primary' : ''}
              onClick={onClick}>
              {text}
            </Button>
          )
        })
      }
    </div>
  }

  handleGroupbyClick = groupby => (e: Event) => {
    e.preventDefault()
    this.handlePush({ groupby })
  }

  handleMenuClick = (e: Event) => {
    this.handlePush({ year: e.key })
  }

  handlePush = (newQuery) => {
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
