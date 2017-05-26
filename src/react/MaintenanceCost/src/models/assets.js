import axios from 'axios'
import {pickBy} from 'lodash'
import moment from 'moment'
import {API_HOST} from '#/constants'

export const PAGE_SIZE = 20
export default {
  namespace : 'assets',
  subscriptions : {
    setup({history, dispatch}) {
      dispatch({type: 'data/get'})
    }
  },
  state : {
    loading: true,
    data: [],
    index: 0,
    total: 0
  },
  effects : {
    *['page/change'](_, {put}) {
      yield put({type: 'filters/cursor/reset', level: 2})
      yield put({type: 'data/get'})
    },
    *['data/get']({payload}, {put, select, call}) {
      const query = yield select(state => {
        const {filters} = state
        const {
          dept,
          assetType: type,
          supplier,
          target: rltgrp,
          cursor,
          groupBy
        } = filters
        const {from, to} = filters.range
        const start = state.assets.index * PAGE_SIZE
        const limit = PAGE_SIZE
        const res = {
          from,
          to,
          dept,
          type,
          supplier,
          rltgrp,
          start,
          limit
        }
        if (res[groupBy] === undefined)
          res[groupBy] = cursor[0]
        return res
      })
      const isPast = yield select(state => state.filters.type === 'history')
      const thresholds = yield select(state => state.thresholds)
      try {
        const { data } = yield call(axios, {
          method: isPast ? 'GET' : 'PUT',
          url: API_HOST + '/ma' + (isPast ? '' : '/forecast'),
          params: pickBy(query, v => v !== undefined),
          data: {
            threshold: thresholds.reduce((prev, cur, index) => (prev['condition' + (index + 1)] = cur, prev), {}),
            items: []
          }
        })
        yield put({type: 'data/get/succeeded', payload: data.items, total: data.root.total})
      } catch (err) {
        console.error(err)
      }
    }
  },
  reducers : {
    ['page/change'](state, {payload}) {
      return {
        ...state,
        index: payload
      }
    },
    ['page/reset'](state, {payload}) {
      return {
        ...state,
        index: 0
      }
    },
    ['data/get'](state, {payload}) {
      return {
        ...state,
        loading: true
      }
    },
    ['data/get/succeeded'](state, {payload, total}) {
      return {
        ...state,
        loading: false,
        data: payload,
        total
      }
    },
    ['pastData/get'](state, {payload}) {
      return {
        ...state
      }
    },
    ['pastData/get/succeeded'](state, {payload}) {
      return {
        ...state,
        pastData: payload
      }
    }
  }
}
