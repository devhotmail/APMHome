/* @flow */
import axios from 'axios'
import { routerRedux } from 'dva/router'
import { notification } from 'antd'
import moment from 'moment'

import { now, dateFormat, pageSize, defaultPage } from '#/constants'
import { mockRoot } from '#/utils'

const defaultRange = {
  from: moment(now).clone().subtract(1, 'year').format(dateFormat),
  to: moment(now).clone().format(dateFormat)
}

export default {
  namespace: 'group',
  state: {
    loading: false,
    root: undefined,
    items: [],
    pageSize: pageSize,
    page: defaultPage,
    total: 0,
    query: {}
  },
  subscriptions: {
    setup ({ dispatch, history }) {
      return history.listen(({ query }) => {
        const { from, to, groupby, groupPage, dept, type } = query

        if (!from || !to) {
          return dispatch(routerRedux.push({
            pathname: '/',
            query: {
              ...query,
              ...defaultRange
            }
          }))
        }

        if (!groupPage) {
          return dispatch(routerRedux.push({
            pathname: '/',
            query: {
              ...query,
              groupPage: defaultPage
            }
          }))
        }

        if (!groupby) {
          return dispatch(routerRedux.push({
            pathname: '/',
            query: {
              ...query,
              groupby: 'type',
              groupId: undefined
            }
          }))
        }

        dispatch({
          type: 'data/get',
          payload: { from, to, page: groupPage, groupby, dept, type }
        })
      })
    }
  },
  effects: {
    *['data/get'] ({ payload: params }, { put, call, select, take }) {
      try {
        const { pageSize, query } = yield select(state => state.group)

        // equal compare for pure object
        if (JSON.stringify(query) === JSON.stringify(params)) return

        yield put({ type: 'loading/on' })

        const { page, ...restQuery } = params

        const { data } = yield call(
          axios,
          {
            url: process.env.API_HOST + '/pm' + (process.env.NODE_ENV === 'development' ? '/left' : ''),
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
          yield put({ type: 'loading/off' })
        } else {
          yield put({
            type: 'data/get/succeed',
            payload: data
          })

          yield put({
            type: 'query/update',
            payload: params
          })

          const root = yield call(mockRoot, data.root)

          yield put({
            type: 'root/set',
            payload: root
          })

          yield put({
            type: 'focus/set',
            payload: root
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
        items: payload.items
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
