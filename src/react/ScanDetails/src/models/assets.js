import axios from 'axios'
import { pickBy } from 'lodash'
import { API_HOST } from '#/constants'

export const PAGE_SIZE = 15

export default {
  namespace: 'assets',
  state: {
    data: [],
    loading: true,
    index: 0,
    animationDirection: 0
  },
  subscriptions: {
    setup({dispatch}) {
      dispatch({
        type: 'data/get'
      })
    }
  },
  effects: {
    *['page/change'](_, { select, put, call }) {
      yield put({type: 'data/get'})
    },
    *['data/get']({ payload }, { select, put, call }) {
      const query = yield select(state => {
        const { filters, assets } = state
        const { range, dept, cursor } = filters
        const { from, to } = range
        const { type, part } = cursor
        const { index } = state.assets
        return {
          from,
          to,
          dept,
          type,
          part,
          start: index * PAGE_SIZE,
          limit: PAGE_SIZE + 1,
          groupby: 'asset'
        }
      })
      const { data } = yield axios({
        method: 'GET',
        url: API_HOST + '/scan/detail',
        params: pickBy(query, item => item !== undefined && item !== null)
      })
      yield put({
        type: 'data/get/succeeded',
        payload: data.detail
      })
    }
  },
  reducers: {
    ['page/reset'](state) {
      return {
        ...state,
        index: 0,
        animationDirection: 0
      }
    },
    ['page/change'](state, { payload }) {
      let animationDirection
      if (payload > 0) {
        animationDirection = 1
      } else if (payload < 0) {
        animationDirection = -1
      } else {
        animationDirection = 0
      }
      return {
        ...state,
        index: state.index + payload,
        animationDirection
      }
    },
    ['data/get'](state) {
      return {
        ...state,
        loading: true
      }
    },
    ['data/get/succeeded'](state, { payload }) {
      return {
        ...state,
        loading: false,
        data: payload
      }
    }
  }
}
