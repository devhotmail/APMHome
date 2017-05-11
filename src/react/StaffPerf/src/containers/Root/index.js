/* @flow */
import React, { Component } from 'react'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'

import SizeProvider from '#/components/SizeProvider'
import FilterBar from '#/components/FilterBar'
import StatusBar from '#/components/StatusBar'
import Pager from '#/components/Pager'
import StackChart from '#/components/StackChart'

import DataProvider from './DataProvider'
import { formatData } from './helper'

import styles from './styles.scss'

const PerfChart = SizeProvider(DataProvider(StackChart))

class Root extends Component {
  render () {
    const {
      loading, location,
      pageSize, total,
      items, root
    } = this.props
    const { page } = location.query

    const data = formatData(items, root)

    return <div className={styles.container}>
      <div className={styles.main}>
        <div className={styles.filterBar}>
          <FilterBar {...this.props} />
        </div>
        <div className={styles.content}>
          <div className={styles.chartWrapper}>
            <PerfChart data={data} />
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
  items: state.list.items,
  focus: state.list.focus,
  root: state.list.root,
  loading: state.list.loading,
  pageSize: state.list.pageSize,
  total: state.list.total
}))(Root)
