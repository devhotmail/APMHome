/* @flow */
import axios from 'axios'
import uuid from 'uuid/v4'
import { routerRedux } from 'dva/router'
import * as d3 from 'd3'

import { currentYear } from '#/constants'
import { round } from '#/utils'

import deptData from '#/mock/deptData'
import typeData from '#/mock/typeData'

const childKey = 'items'

export default {
  namespace: 'financial',
  state: {
    loading: false,
    data: undefined
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
    *['data/get'] ({ payload: params }, { put, call, select }) {
      try {
        const mockData = params.groupby === 'dept' ? deptData : typeData

        yield put({
          type: 'data/get/succeed',
          payload: mockData
        })

        yield put({
          type: 'nodeList/data/get'
        })
      } catch(err) {
        yield put({
          type: 'data/get/failed',
          payload: err
        })
      }
    },
    *['data/get1'] ({ payload: params }, { put, call, select }) {
      try {
        const { data } = yield call(
          axios.get,
          process.env.API_HOST + '/dmv2',
          { params }
        )

        yield put({
          type: 'data/get/succeed',
          payload: data
        })

        yield put({
          type: 'nodeList/data/get'
        })
      } catch(err) {
        yield put({
          type: 'data/get/failed',
          payload: err
        })
      }
    }
  },
  reducers: {
    ['data/get/succeed'] (state, { payload }) {
      return {
        ...state,
        data: payload
      }
    }        
  }
}
