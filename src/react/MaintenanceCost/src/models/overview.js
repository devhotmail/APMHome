import moment from 'moment'
import axios from 'axios'
import { API_HOST, PAGE_SIZE } from '#/constants'

export default {
  namespace: 'overview',
  state: {
    data: {},
    pastData: {}
  },
  subscriptions: {
    setup({ history, dispatch }) {
      dispatch({
        type: 'data/get'
      })
    }
  },
  effects: {
    *['data/get']({ payload }, { put, call, select }) {
      const { from, to, groupby, dept, type, supplier, rltgrp, start, limit, cursor } = yield select(state => {
        const { filters } = state
        const groupby = filters.groupBy
        const { dept, assetType: type, supplier, target: rltgrp, cursor, groupBy } = filters
        const { from, to } = filters.range
        const start = state.groups.index * PAGE_SIZE
        const limit = PAGE_SIZE
        const res = { from, to, groupby: groupBy, dept, type, supplier, rltgrp, start, limit, cursor}
        if (res[groupBy] === undefined) res[groupBy] = cursor[0]
        return res
      })
      if (cursor[1]) {
        try {
          const data = yield Promise.all([
            axios(API_HOST + '/ma/asset/' + cursor[1], {params: {from, to, rltgrp: 'acyman'}}),
            axios(API_HOST + '/ma/asset/' + cursor[1], {params: {from, to, rltgrp: 'mtpm'}}),
            axios(API_HOST + '/ma/asset/' + cursor[1], {params: {from: moment(from).subtract(1, 'year').format('YYYY-MM-DD'), to: moment(to).subtract(1, 'year').format('YYYY-MM-DD'), rltgrp: 'acyman'}}),
            axios(API_HOST + '/ma/asset/' + cursor[1], {params: {from: moment(from).subtract(1, 'year').format('YYYY-MM-DD'), to: moment(to).subtract(1, 'year').format('YYYY-MM-DD'), rltgrp: 'mtpm'}})
          ])
          yield put({
            type: 'data/get/succeeded',
            payload: data.map(({ data }) => data[0])
          })
        } catch(err) {
          console.error(err)
        }
      } else {
        try {
          const data = yield Promise.all([
            axios(API_HOST + '/ma', {params: {from, to, groupby, dept, type, supplier, rltgrp: 'acyman'}}),
            axios(API_HOST + '/ma', {params: {from, to, groupby, dept, type, supplier, rltgrp: 'mtpm'}}),
            axios(API_HOST + '/ma', {params: {from: moment(from).subtract(1, 'year').format('YYYY-MM-DD'), to: moment(to).subtract(1, 'year').format('YYYY-MM-DD'), groupby, dept, type, supplier, rltgrp: 'acyman'}}),
            axios(API_HOST + '/ma', {params: {from: moment(from).subtract(1, 'year').format('YYYY-MM-DD'), to: moment(to).subtract(1, 'year').format('YYYY-MM-DD'), groupby, dept, type, supplier, rltgrp: 'mtpm'}})
          ])
          yield put({
            type: 'data/get/succeeded',
            payload: data.map(({ data }) => data.root)
          })
        } catch(err) {
          console.error(err)
        }
      }
    }
  },
  reducers: {
    ['data/get/succeeded'](state, { payload }) {
      return {
        ...state,
        data: {
          ...payload[0],
          ...payload[1]
        },
        pastData: {
          ...payload[2],
          ...payload[3]
        }
      }
    }
  }
}
