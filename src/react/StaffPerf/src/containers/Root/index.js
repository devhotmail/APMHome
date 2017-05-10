import React, { Component } from 'react'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'
import moment from 'moment'

import { now, dateFormat } from '#/constants'

import SizeProvider from '#/components/SizeProvider'
import StackChart from '#/components/StackChart'
import FilterBar from '#/components/FilterBar'
import StatusBar from '#/components/StatusBar'
import Pager from '#/components/Pager'

import styles from './styles.scss'

const Chart = SizeProvider(StackChart)

const from = moment(now).clone().subtract(1, 'year').format(dateFormat)
const to = moment(now).clone().format(dateFormat)

class Root extends Component {
  render () {
    const { loading, location, pageSize, total } = this.props
    const { page } = location.query
    return <div className={styles.container}>
      <div className={styles.main}>
        <div className={styles.filterBar}>
          <FilterBar {...this.props} />
        </div>
        <div className={styles.content}>
          <div className={styles.chartWrapper}>
            { loading ? <StatusBar type="loading" /> : <Chart /> }
          </div>
        </div>        
      </div>
      <div className={styles.sidebar}>
        <Pager
          current={parseInt(page)}
          pageSize={pageSize}
          total={total}
          onChange={this.handleChange} />
      </div>      
    </div>
  }

  handleChange = (nextPage) => {
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
  data: state.list.data,
  loading: state.list.loading,
  current: state.list.currentPage,
  pageSize: state.list.pageSize,
  total: state.list.total
}))(Root)
