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
  asset: state.asset
}))
export default class Root extends Component {
  state = {
    filterOpts: [
      {
        type: 'range',
        key: 'range',
        value: {
          // from: '2016-10-05',
          // to: '2017-01-25'
        },
        // ...otherProps API same as Antd RangePicker
      },
      {
        type: 'radio',
        key: 'radio',
        value: 1,
        options: [
          {
            key: 1,
            value: '1'
          },
          {
            key: 13,
            value: '13'
          },
          {
            key: 12,
            value: '12'
          }
        ],
        // ...otherProps API same as Antd Button
      },
      {
        type: 'select',
        key: 'select',
        value: 1,
        options: [
          {
            key: 1,
            value: '科室一'
          },
          {
            key: 13,
            value: '科室十三'
          },
          {
            key: 12,
            value: '科室十二'
          }
        ],
        placeholder: '全部科室'
        // ...otherProps API same as Antd Select
      }
    ],
    groupAD: 0,
    assetAD: 0
  }

  render () {
    const { filterOpts, groupAD, assetAD } = this.state

    const { group, asset, location } = this.props
    const { groupPage, assetPage } = location.query

    return (
      <div className={styles.container}>
        <div className={styles.filters}>
          <FilterBar options={filterOpts} onChange={this.handleChange} />
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
                  animationDirection={groupAD}
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

  handlePageChange = (key: string) => (current: number, last: number) => {
    const { dispatch, location } = this.props

    dispatch(routerRedux.push({
      pathname: '/',
      query: {
        ...location.query,
        [`${key}Page`]: current
      }
    }))

    this.setState((state, props) => ({
      ...state,
      [`${key}AD`]: last - current
    }))
  }

  handleChange = (payload) => {
    const { key, value } = payload
    console.log(payload)
  }
}
