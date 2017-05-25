import axios from 'axios'
import {pickBy} from 'lodash'
import moment from 'moment'
import {API_HOST} from '#/constants'

export const PAGE_SIZE = 10

export default {
  namespace : 'groups',
  state : {
    loading: true,
    index: 0,
    total: 0,
    data: [],
    pastData: []
  },
  subscriptions : {
    setup({history, dispatch}) {
      dispatch({type: 'data/get'})
    }
  },
  effects : {
    *['page/change'](_, {put}) {
      yield put({type: 'filters/cursor/reset', level: 1})
      yield put({type: 'data/get'})
    },
    *['data/get']({payload}, {put, call, select}) {
      const query = yield select(state => {
        const {filters} = state
        const groupby = filters.groupBy
        const {dept, assetType: type, supplier, target: rltgrp} = filters
        const {from, to} = filters.range
        const start = state.groups.index * PAGE_SIZE
        const limit = PAGE_SIZE
        return {
          from,
          to,
          groupby,
          dept,
          type,
          supplier,
          rltgrp,
          start,
          limit
        }
      })
      try {
        const data = yield Promise.all([
          axios(API_HOST + '/ma', {
            params: pickBy(query, v => v !== undefined)
          }),
          axios(API_HOST + '/ma', {
            params: pickBy({
              ...query,
              from: moment(query.from).subtract(1, 'year').format('YYYY-MM-DD'),
              to: moment(query.to).subtract(1, 'year').format('YYYY-MM-DD')
            }, v => v !== undefined)
          })
        ])
        yield put({type: 'data/get/succeeded', payload: data})
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
    ['data/get/succeeded'](state, {payload}) {
      return {
        ...state,
        loading: false,
        data: payload[0].data.items,
        pastData: payload[1].data.items,
        total: payload[0].data.root.total
      }
    }
  }
}
