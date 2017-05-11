/* @flow */
import React, { Component } from 'react'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'

import SizeProvider from '#/components/SizeProvider'
import FilterBar from '#/components/FilterBar'
import StatusBar from '#/components/StatusBar'
import Pager from '#/components/Pager'
import StackChart from '#/components/StackChart'
import CoreCircle from '#/components/CoreCircle'

import DataProvider from './DataProvider'

import styles from './styles.scss'

const PerfChart = SizeProvider(DataProvider(StackChart))

class Root extends Component {
  render () {
    const {
      loading, location,
      pageSize, total,
      filter,
      items, focus, range
    } = this.props
    const { page } = location.query

    return <div className={styles.container}>
      <div className={styles.main}>
        <div className={styles.filterBar}>
          <FilterBar {...this.props} />
        </div>
        <div className={styles.content}>
          <div className={styles.chartWrapper}>
            <PerfChart
              focus={focus}
              filter={filter}
              items={items}
              range={range}
              setFocus={this.handleSetFocus}
              backRoot={this.handleBackRoot} />
            <div className={styles.core}>
              <CoreCircle
                focus={focus}
                range={range}
                onClick={this.handleFilterClick} />
            </div>
            {/*{
              loading
                ? <StatusBar type="loading" />
                : <PerfChart data={data} />
            }*/}
          </div>
        </div>        
      </div>
      <div className={styles.sidebar}>
        {
          total
            ? <Pager
              current={parseInt(page)}
              pageSize={pageSize}
              total={total}
              onChange={this.handleChange} />
            : null
        }
      </div>      
    </div>
  }

  handleBackRoot = () => {
    console.log(this.props)
    this.handleSetFocus(this.props.root)
  }

  handleSetFocus = (node: cursorT) => {
    this.props.dispatch({
      type: 'focus/set',
      payload: node
    })
  }

  handleFilterClick = filter => e => {
    this.props.dispatch({
      type: 'filter/set',
      payload: filter
    })
  }

  handleChange = (nextPage: number) => {
    const { dispatch, location } = this.props

    dispatch(routerRedux.push({
      pathname: '/',
      query: {
        ...location.query,
        page: nextPage
      }
    }))
  }
}

export default connect(state => ({
  focus: state.focus.data,
  filter: state.filter.data,
  ...state.list
}))(Root)
