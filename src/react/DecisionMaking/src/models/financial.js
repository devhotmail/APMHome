/* @flow */
import axios from 'axios'
import uuid from 'uuid/v4'
import * as d3 from 'd3'

import { round } from '#/utils'
import data from '#/mock/data'

function formatData (data) {
  return {
    children: data.items,
    ...data.root
  }
}

const childKey = 'items'

export default {
  namespace: 'financial',
  state: {
    loading: false,
    data: data
  },
  subscriptions: {
    setup({ dispatch }) {
      dispatch({
        type: 'config/set'
      })

      // dispatch({
      //   type: 'data/get'
      // })
    }
  },
  effects: {
    *['data/get'] (action, { put, call, select }) {
      console.log(data)

      // const params = {
      //   year: 2016,
      //   groupby: 'type',
      //   start: 0,
      //   limit: 10
      // }

      // try {
      //   const { data } = yield call(
      //     axios.get,
      //     process.env.API_HOST + '/profit',
      //     { params }
      //   )

      //   yield put({
      //     type: 'data/get/succeed',
      //     payload: data
      //   })
      // } catch(err) {
      //   yield put({
      //     type: 'data/get/failed',
      //     payload: err
      //   })
      // }
    }
  },
  reducers: {
    ['data/get/succeed'] (state, { payload }) {
      return {
        ...state,
        data: formatData(payload)
      }
    }        
  }
}
