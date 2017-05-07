/* @flow */
import axios from 'axios'
import { routerRedux } from 'dva/router'

import { currentYear } from '#/constants'

import deptData from '#/mock/deptData'
import typeData from '#/mock/typeData'

export default {
  namespace: 'finance',
  state: {
    loading: false,
    systemData: undefined,
    manualData: undefined,
    query: {}
  },
  subscriptions: {
    setup({ dispatch, history }) {
      return history.listen(({query}) => {
        const { groupby, year } = query

        if (!groupby) return dispatch(routerRedux.push({
          pathname: '/',
          query: {
            ...query,
            groupby: 'type'
          }
        }))

        if (!year) return dispatch(routerRedux.push({
          pathname: '/',
          query: {
            ...query,
            year: currentYear
          }
        }))

        dispatch({
          type: 'data/get',
          payload: query
        })
      })
    }
  },
  effects: {
    *['data/get/1'] ({ payload: params }, { put, call, select }) {
      try {
        const mockData = params.groupby === 'dept' ? deptData : typeData

        yield put({
          type: 'data/get/succeed',
          payload: mockData
        })

        yield put({
          type: 'query/update',
          payload: params
        })

        yield put({
          type: 'nodeList/data/get',
          payload: mockData
        })
      } catch (err) {
        yield put({
          type: 'data/get/failed',
          payload: err
        })
      }
    },
    *['data/get'] ({ payload: params }, { put, call, select }) {
      try {
        const { data } = yield call(
          axios.get,
          {
            url: process.env.API_HOST + '/dmv2',
            params
          }
        )

        yield put({
          type: 'data/get/succeed',
          payload: data
        })

        yield put({
          type: 'query/update',
          payload: params
        })

        yield put({
          type: 'nodeList/data/get',
          payload: data
        })

      } catch (err) {
        yield put({
          type: 'data/get/failed',
          payload: err
        })
      }
    },
    *['data/put'] ({ payload }, { put, call, select }) {
      try {
        const { query } = yield select(state => state.finance)

        const { data } = yield call(
          axios,
          {
            method: 'put',
            url: process.env.API_HOST + '/dmv2',
            params: query,
            data: { config: payload }
          }
        )

        yield put({
          type: 'data/put/succeed',
          payload: data
        })

        yield put({
          type: 'nodeList/data/get',
          payload: data
        })
      } catch (err) {
        yield put({
          type: 'data/put/failed',
          payload: err
        })
      }
    }
  },
  reducers: {
    ['data/get/succeed'] (state, { payload }) {
      return {
        ...state,
        systemData: payload
      }
    },
    ['data/put/succeed'] (state, { payload }) {
      return {
        ...state,
        manualData: payload
      }
    },
    ['query/update'] (state, { payload }) {
      return {
        ...state,
        query: payload
      }
    }
  }
}
