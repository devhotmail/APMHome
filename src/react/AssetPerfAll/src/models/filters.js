import moment from 'moment'
import { dateFormat } from '#/constants'

export default {
  namespace: 'filters',
  state: {
    type: 'history',
    from: moment().subtract(1, 'year').format(dateFormat),
    to: moment().format(dateFormat),
    groupBy: 'month',
    data: []
  },
  effects: {
    *['data/change']({ payload, level }, { put, select }){
      yield put({
        type: 'profit/data/get',
        level
      })
      yield put({
        type: 'config/data/get',
        level
      })
    },
    addWatcher: [ function* ({ takeLatest, put, call, select }) {
      const paload = yield takeLatest(
        [
          'filters/type/set',
          'filters/range/set',
          'filters/groupBy/set',
        ],
        function* (action) {
          yield put({
            type: 'filters/data/reset'
          })
          yield put({
            type: 'config/changes/reset'
          })
          yield put({
            type: 'config/data/get',
            level: 0
          })
          yield put({
            type: 'profit/data/get',
            level: 0
          })
        }
      )
    }, { type: 'watcher'}]
  },
  reducers: {
    ['data/reset'](state) {
      return {
        ...state,
        data: []
      }
    },
    ['data/change'](state, { payload, level }) {
      const data = state.data
      data[level] = payload
      return {
        ...state,
        data: data.slice(0, level + 1)
      }
    },
    ['type/set'](state, { payload }) {
      return {
        ...state,
        type: payload
      }
    },
    ['range/set'](state, { payload }) {
      return {
        ...state,
        ...payload
      }
    },
    ['groupBy/set'](state, { payload }) {
      return {
        ...state,
        groupBy: payload
      }
    }
  }
}
