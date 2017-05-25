/* @flow */
import React, { Component } from 'react'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'
import { Dropdown, Button, Icon, Menu } from 'antd'
import FilterBar from 'dew-filterbar'
import Pager from 'dew-pager'
import RingSectorLayout from 'ring-sector-layout'
import AnnulusSector from 'ring-sector-layout/dist/AnnulusSector'
import AnnulusSectorStack from 'ring-sector-layout/dist/AnnulusSectorStack'

import { QUALITY, COMPLETION, defaultPage } from '#/constants'
import CoreCircle from '#/components/CoreCircle'
import RoleProvider from '#/components/RoleProvider'

import PartGroup from './PartGroup'
import PartAsset from './PartAsset'

import styles from './styles.scss'

const purple = '#b781b4'
const prasinous = '#6ab6a6'

const roleFilterFn = isHead => n => {
  if (!isHead) return n.key !== 'dept'
  else return true
}

@connect(state => ({
  user: state.user.info,
  focus: state.focus.data,
  root: state.root.data,
  group: state.group,
  asset: state.asset,
  filter: state.filter,
  loading: !(!state.group.loading && !state.asset.loading)
}))
@RoleProvider
export default class Root extends Component {
  state = {
    groupAD: 0,
    assetAD: 0,
    groupbyOpts: [
      {
        key: 'type',
        text: '设备类型'
      },
      {
        key: 'supplier',
        text: '品牌'
      },
      {
        key: 'dept',
        text: '科室'
      }
    ],
    lastSelectedGroup: undefined
  }

  componentDidMount () {
    const { location, dispatch } = this.props

    dispatch({
      type: 'filter/data/get'
    })
    // remove selected asset and group when initial load
    dispatch(routerRedux.push({
      pathname: '/',
      query: {
        ...location.query,
        assetId: undefined,
        groupId: undefined
      }
    }))
  }

  render () {
    const { group, asset, location, filter, loading, focus, user } = this.props
    const { groupPage, assetPage, dept, type, groupby } = location.query

    const { groupAD, assetAD, groupbyOpts } = this.state
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
    ].filter(roleFilterFn(user.isHead))

    const menu = (
      <Menu onClick={this.handleGroupbyChange} selectedKeys={[groupby]} trigger={['click']}>
        {
          groupbyOpts.filter(roleFilterFn(user.isHead)).map((opt, i) => 
            <Menu.Item key={opt.key}>显示{opt.text}</Menu.Item>
          )
        }
      </Menu>
    )

    const { text: selectedGroupby } = groupbyOpts.find(n => n.key === groupby) || groupbyOpts[0]

    return (
      <div className={styles.container}>
        <div className={styles.filters}>
          <FilterBar options={filterOpts} onChange={this.handleFilterChange} />
        </div>
        <div className={styles.chartWrapper}>
          <div className={styles.core}>
            <CoreCircle
              loading={loading}
              switcher={filter.switcher}
              focus={focus}
              onClick={this.handleSwitcherChange} />
          </div>
          <div className={styles.leftPager}>
            <Pager
              current={parseInt(groupPage)}
              pageSize={group.pageSize}
              total={group.total}
              onChange={this.handleLeftPageChange} />
          </div>
          <div className={styles.rightPager}>
            <Pager
              current={parseInt(assetPage)}
              pageSize={asset.pageSize}
              total={asset.total}
              onChange={this.handleRightPageChange} />
          </div>          
          <div className={styles.group}>
            <div className={styles.groupby}>
              <Dropdown overlay={menu}>
                <Button style={{ marginLeft: 8 }}>
                  显示{selectedGroupby} <Icon type="down" />
                </Button>
              </Dropdown>
            </div>
            {
              group.items.length
                ? <PartGroup
                    data={group.items}
                    selectedGroupId={location.query.groupId}
                    animationDirection={groupAD}
                    onClick={this.handleGroupClick}
                    switcher={filter.switcher} />
                : null
            }
          </div>
          <div className={styles.asset}>
            {
              asset.items.length
                ? <PartAsset
                    data={asset.items}
                    selectedAssetId={location.query.assetId}
                    animationDirection={assetAD}
                    onClick={this.handleAssetClick}
                    switcher={filter.switcher} />
                : null
            }
          </div>
        </div>
      </div>
    )
  }

  handleSwitcherChange = key => e => {
    e.preventDefault()
    this.props.dispatch({
      type: 'filter/switcher/set',
      payload: key
    })
  }

  handleGroupbyChange = (e) => {
    const { location } = this.props

    this.changeQuery({
      groupby: e.key,
      groupId: undefined,
      groupPage: defaultPage
    })
  }

  handleGroupClick = (id: string, data: Object) => e => {
    e.preventDefault()
    const { dispatch, location, root } = this.props
    const { query: { groupId }  } = location

    // remove groupId when click the selected group
    const isGroupSelected = id == groupId

    const newGroupId = isGroupSelected ? undefined : id
    const payload = isGroupSelected ? root : data

    this.changeQuery({
      groupId: newGroupId,
      assetId: undefined
    })

    this.setState({
      lastSelectedGroup: payload
    })

    dispatch({
      type: 'focus/set',
      payload
    })
  }

  handleAssetClick = (id: string, data: Object) => e => {
    e.preventDefault()
    const { dispatch, location, root } = this.props
    const { query: { assetId }  } = location
    const { lastSelectedGroup } = this.state

    // remove groupId when click the selected group
    const isAssetSelected = id == assetId

    const newAssetId = isAssetSelected ? undefined : id
    // use lastSelectedGroup instead of root here
    const payload = isAssetSelected ? lastSelectedGroup : data

    this.changeQuery({ assetId: newAssetId })

    dispatch({
      type: 'focus/set',
      payload
    })
  }

  handleLeftPageChange = (current: number, last: number) => {
    this.changeQuery({
      groupPage: current,
      groupId: undefined
    })
    
    this.setState((state, props) => ({
      ...state,
      groupAD: last - current
    }))
  }

  handleRightPageChange = (current: number, last: number) => {
    this.changeQuery({
      assetPage: current,
      assetId: undefined
    })
    
    this.setState((state, props) => ({
      ...state,
      assetAD: last - current
    }))
  }

  handlePageChange = (key: string) => (current: number, last: number) => {
    // remove selected group id in query when pager changed
    this.changeQuery({
      [`${key}Page`]: current,
      groupId: undefined
    })

    this.setState((state, props) => ({
      ...state,
      [`${key}AD`]: last - current
    }))
  }

  handleFilterChange = (payload) => {
    const { key, value } = payload
    if (key === 'range') {
      this.changeQuery({
        ...value,
        assetPage: defaultPage
      })
    } else {
      this.changeQuery({
        [key]: value,
        assetPage: defaultPage,
        groupPage: defaultPage
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
