/* @flow */
import axios from 'axios'
import { routerRedux } from 'dva/router'
import { notification } from 'antd'

import { pageSize } from '#/constants'

export default {
  namespace: 'list',
  state: {
    loading: false,
    data: undefined,
    page: 1,
    pageSize: pageSize,
    total: 1,
    from: undefined,
    to: undefined
  },
  subscriptions: {
    setup ({ dispatch, history }) {
      return history.listen(({query}) => {
        const { from, to } = query

        if (!from || !to) {
          return dispatch(routerRedux.push({
            pathname: '/',
            query: {
              ...query,
              from: '2016-05-10',
              to: '2017-05-10'
            }
          }))
        }

        if (!query.page) {
          return dispatch(routerRedux.push({
            pathname: '/',
            query: {
              ...query,
              page: 1
            }
          }))
        }

        dispatch({
          type: 'data/get',
          payload: query
        })
      })
    }
  },
  effects: {
    *['data/get'] ({ payload }, { put, call, select, take }) {
      try {
        yield put({ type: 'loading/on' })
        const { pageSize } = yield select(state => state.list)

        const { page, ...query } = payload

        const { data } = yield call(
          axios,
          {
            url: process.env.API_HOST + '/staff',
            params: {
              ...query,
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
        data: payload
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
