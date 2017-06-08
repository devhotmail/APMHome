import axios from 'axios'
import { groupBy, flatMap, uniqBy } from 'lodash'
import { API_HOST, COLORS } from '#/constants'

const PAGE_SIZE = 15

export default {
  namespace: 'briefs',
  state: {
    loading: true,
    data: [],
    parts: {},
    index: 0
  },
  subscriptions: {
    setup({dispatch}) {
      dispatch({
        type: 'data/get'
      })
    }
  },
  effects: {
    *['data/get']({ payload }, { put, select }) {
      const query = yield select(state => {
        const { filters, briefs } = state

        const { range, dept } = filters
        const { from, to } = range
        return {
          from,
          to,
          dept,
          start: briefs.index * PAGE_SIZE,
          limit: PAGE_SIZE
        }
      })
      const { data } = yield axios({
        method: 'GET',
        url: API_HOST + '/scan/brief',
        params: query
      })
      yield put({
        type: 'data/get/succeeded',
        payload: data.brief
      })
    }
  },
  reducers: {
    ['data/get'](state) {
      return {
        ...state,
        loading: true
      }
    },
    ['data/get/succeeded'](state, { payload }) {
      const parts = uniqBy(flatMap(payload, item => item.items.desc), item => item.id)
        .reduce((prev, cur, index) => (prev[cur.id] = {...cur, color: COLORS[index]}, prev), {})
      return {
        ...state,
        data: payload,
        parts,
        loading: false
      }
    }
  }
}
