/* @flow */
import axios from 'axios'
import { routerRedux } from 'dva/router'
import { notification } from 'antd'
import moment from 'moment'

import { now, dateFormat, pageSize } from '#/constants'

const defaultRange = {
  from: moment(now).clone().subtract(1, 'year').format(dateFormat),
  to: moment(now).clone().format(dateFormat)
}

const defaultPage = 1

export default {
  namespace: 'list',
  state: {
    loading: false,
    root: undefined,
    range: undefined,
    items: [],
    pageSize: pageSize,
    page: defaultPage,
    total: 0,
    query: {}
  },
  subscriptions: {
    setup ({ dispatch, history }) {
      return history.listen(({query}) => {
        const { from, to, page } = query

        if (!from || !to) {
          return dispatch(routerRedux.push({
            pathname: '/',
            query: {
              ...query,
              ...defaultRange
            }
          }))
        }

        if (!page) {
          return dispatch(routerRedux.push({
            pathname: '/',
            query: {
              ...query,
              page: defaultPage
            }
          }))
        }

        dispatch({
          type: 'data/get',
          payload: { from, to, page }
        })
      })
    }
  },
  effects: {
    *['data/get'] ({ payload: params }, { put, call, select, take }) {
      try {
        const { pageSize, query } = yield select(state => state.list)

        const flag = ['from', 'to', 'page'].reduce((prev, cur) => {
          if (query[cur] !== params[cur]) prev = false
          return prev
        }, true)
        if (flag) return

        yield put({ type: 'loading/on' })

        const { page, ...restQuery } = params

        const { data } = yield call(
          axios,
          {
            url: process.env.API_HOST + '/staff',
            params: {
              ...restQuery,
              start: (page - 1) * pageSize,
              limit: pageSize
            }
          }
        )

        if (
          (data.pages && !data.pages.total)
          || (Array.isArray(data.items) && !data.items.length)
        ) {
          yield put({
            type: 'data/status/empty'
          })
        } else {
          yield put({
            type: 'data/get/succeed',
            payload: data
          })

          yield put({
            type: 'query/update',
            payload: params
          })

          yield put({
            type: 'range/set',
            payload: data.range
          })

          yield put({
            type: 'focus/set',
            payload: {
              ...data.summary,
              owner_name: '所有人'
            }
          })

          yield put({ type: 'loading/off' })
        }
      } catch (err) {
        yield put({
          type: 'data/status/failed',
          payload: err
        })
      }
    },
    *['data/status/failed'] ({ payload: err }) {
      notification.error({
        message: '数据加载出错，请尝试刷新页面',
        description: err.message
      })
    },
    *['data/status/empty'] () {
      notification.warning({
        message: '暂无可用设备数据',
        description: '请稍后再试'
      })      
    }    
  },
  reducers: {
    ['data/get/succeed'] (state, { payload }) {
      return {
        ...state,
        total: payload.pages.total,
        items: payload.items,
        root: payload.summary
      }
    },
    ['range/set'] (state, { payload }) {
      return {
        ...state,
        range: payload
      }
    },
    ['query/update'] (state, { payload }) {
      return {
        ...state,
        query: payload
      }
    },
    ['loading/on'] (state, action) {
      return {
        ...state,
        loading: true
      }
    },
    ['loading/off'] (state, action) {
      return {
        ...state,
        loading: false
      }
    }    
  }
}
