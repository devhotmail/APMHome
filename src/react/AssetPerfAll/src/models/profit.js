import axios from 'axios'
import { pickBy } from 'lodash'
import { API_HOST, PAGE_SIZE } from '#/constants'

const isHeadEl = document.querySelector('#user-context #isHead')
const isHead = isHeadEl ? JSON.parse(isHeadEl.value) : false

const orgIdEl = document.querySelector('#user-context #orgId')
const orgId = isHead ? undefined : parseInt(orgIdEl.value)

export default {
  namespace: 'profit',
  state: {
    data: []
  },
  subscriptions: {
    setup({ dispatch, location }) {
      dispatch({
        type: 'data/get',
        start: 0,
        level: 0
      })
    }
  },
  effects: {
    *['data/get']({ start = 0, level }, { put, select }) {
      const filters = yield select(state => state.filters)
      const { type, groupBy, from, to, data } = filters
      const filter = data[level]
      const config = yield select(state => state.config.changes)
      try {
        const { data } = yield axios({
          method: type === 'history' ? 'get' : 'put',
          url: API_HOST + (type ==='history' ? '/profit' : '/profit/forecast'),
          params: pickBy({
            groupby: filter ? null : groupBy,
            from,
            to,
            dept: orgId,
            ...filter,
            start,
            limit: PAGE_SIZE
          }),
          data: {config}
        })

        data.items = data.items.map(item => ({root: item}))

        yield put({
          type: 'data/get/succeeded',
          payload: data,
          level
        })
      } catch(err) {
        console.log(err)
      }
    }
  },
  reducers: {
    ['data/get/succeeded'](state, { payload, level }) {
      const data = [...state.data]
      data[level] = payload
      return {
        ...state,
        data: data.slice(0, level + 1)
      }
    }
  }
}
