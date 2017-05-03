/* @flow */
import axios from 'axios'
import uuid from 'uuid/v4'
import * as d3 from 'd3'

import { currentYear } from '#/constants'
import { round } from '#/utils'
import data from '#/mock/data'

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
        dispatch({
          type: 'data/get',
          payload: query || {}
        })
      })
    }
  },
  effects: {
    *['data/get'] ({ payload }, { put, call, select }) {
      const params = {
        year: currentYear,
        groupby: 'type',
        ...payload
      }

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
