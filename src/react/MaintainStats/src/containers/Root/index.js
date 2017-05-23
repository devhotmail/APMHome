/* @flow */
import React, { Component } from 'react'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'
import FilterBar from 'dew-filterbar'
import Pager from 'dew-pager'
import RingSectorLayout from 'ring-sector-layout'
import AnnulusSector from 'ring-sector-layout/dist/AnnulusSector'
import AnnulusSectorStack from 'ring-sector-layout/dist/AnnulusSectorStack'

import { quality, completion } from '#/constants'
import PartGroup from './PartGroup'
import PartAsset from './PartAsset'

import styles from './styles.scss'

const purple = '#b781b4'
const prasinous = '#6ab6a6'

@connect(state => ({
  group: state.group,
  asset: state.asset,
  filter: state.filter
}))
export default class Root extends Component {
  state = {
    groupAD: 0,
    assetAD: 0
  }

  componentDidMount () {
    this.props.dispatch({
      type: 'filter/data/get'
    })
  }

  render () {
    const { group, asset, location, filter } = this.props
    const { groupPage, assetPage, dept, type } = location.query

    const { groupAD, assetAD } = this.state

    const filterOpts = [
      {
        type: 'range',
        key: 'range'
      },
      {
        type: 'select',
        key: 'dept',
        value: dept,
        options: filter.depts,
        placeholder: '全部科室'
      },
      {
        type: 'select',
        key: 'type',
        value: type,
        options: filter.types,
        placeholder: '全部设备类型'
      }
    ]

    return (
      <div className={styles.container}>
        <div className={styles.filters}>
          <FilterBar options={filterOpts} onChange={this.handleFilterChange} />
        </div>
        <div className={styles.chartWrapper}>
          <Pager
            current={parseInt(groupPage)}
            pageSize={group.pageSize}
            total={group.total}
            onChange={this.handlePageChange('group')} />      
          <div className={styles.group}>
            {
              group.items.length
                ? <PartGroup
                    data={group.items}
                    selectedGroupId={location.query.groupId}
                    animationDirection={groupAD}
                    onClick={this.handleGroupClick}
                    switcher={completion} />
                : null
            }
          </div>
          <div className={styles.asset}>
            {
              asset.items.length
                ? <PartAsset
                    data={asset.items}
                    animationDirection={assetAD}
                    switcher={completion} />
                : null
            }
          </div>
          <Pager
            current={parseInt(assetPage)}
            pageSize={asset.pageSize}
            total={asset.total}
            onChange={this.handlePageChange('asset')} />
        </div>
      </div>
    )
  }

  handleGroupClick = (groupId: string) => e => {
    e.preventDefault()
    this.changeQuery({ groupId })
  }

  handlePageChange = (key: string) => (current: number, last: number) => {
    this.changeQuery({
      [`${key}Page`]: current
    })

    this.setState((state, props) => ({
      ...state,
      [`${key}AD`]: last - current
    }))
  }

  handleFilterChange = (payload) => {
    const { key, value } = payload
    if (key === 'range') {
      this.changeQuery(value)
    } else {
      this.changeQuery({
        [key]: value
      })
    }
  }  

  changeQuery = (params: Object) => {
    const { dispatch, location } = this.props

    dispatch(routerRedux.push({
      pathname: '/',
      query: {
        ...location.query,
        ...params
      }
    }))    
  }
}
