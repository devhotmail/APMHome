import moment from 'moment'
import { times } from 'lodash'
import axios from 'axios'
import { API_HOST, currentYear } from '#/constants'

export default {
  namespace: 'forecastOverview',
  state: {
    loading: true,
    data: [],
    ranges: []
  },
  effects: {
    // *['filters/field/set']({ payload }, { put }) {
    //   debugger
    //   if (payload.key !== 'range') return
    //   yield put({
    //     type: 'ranges/calc'
    //   })
    // },
    *['ranges/calc']({ payload }, { put, select, call }) {
      const { range, type } = yield select(state => state.filters)
      if (type === 'history') return

      const ranges = times(4, index => {
        const { from, to } = range
        const toYear = moment(to).year()
        if (currentYear > toYear) throw('currentYear larger than range')
        if (toYear - index >= currentYear) return [moment(from).subtract(index, 'year'), moment(to).subtract(index, 'year')]
        if (toYear - index === currentYear - 1) return [moment(from).subtract(index - 1, 'year').startOf('year'), moment()]
        else return [moment(from).subtract(index - 1, 'year'), moment(to).subtract(index - 1, 'year')]
      }).reverse().map(([from, to]) => [from.format('YYYY-MM-DD'), to.format('YYYY-MM-DD')])
      yield put({
        type: 'ranges/set',
        payload: ranges
      })
    },
    *['data/get']({ payload }, { put, select, call }) {
      const { type, cursor } = yield select(state => state.filters)
      const { ranges } = yield select(state => state.forecastOverview)
      if(type === 'history' || cursor.length < 2) return
      try {
        const res = yield Promise.all(ranges.map(([from, to]) => {
          if (to <= moment().format('YYYY-MM-DD')) {
            return Promise.all(['acyman', 'mtpm'].map(key => axios(API_HOST + '/ma/asset/' + cursor[1], {params: {from, to, rltgrp: key}})))
          } else {
            return Promise.all(['acyman', 'mtpm'].map(key => axios({
              method: 'PUT',
              url: API_HOST + '/ma/forecast/asset/' + cursor[1],
              params: {from, to, rltgrp: key},
              data: {
                threshold: {},
                items: []
              }
            })))
          }
        }))
        yield put({
          type: 'data/get/succeeded',
          payload: res.map(item => ({...item[0].data[0], ...item[1].data[0]}))
        })
      } catch(err) {
        console.error(err)
      }
    }
  },
  reducers: {
    ['data/get/succeeded'](state, { payload }) {
      return {
        ...state,
        data: payload
      }
    },
    ['ranges/set'](state, { payload }) {
      return {
        ...state,
        ranges: payload
      }
    }
  }
}
