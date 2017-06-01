import moment from 'moment'
import { ranges } from '#/constants'

export default {
  namespace: 'filters',
  state: {
    type: 'history',
    range: {
      from: ranges.oneYear.start.format('YYYY-MM-DD'),
      to: ranges.oneYear.end.format('YYYY-MM-DD')
    },
    dept: undefined,
    assetType: undefined,
    groupBy: 'type',
    MSA: undefined,
    target: 'acyman',
    cursor: []
  },
  effects: {
    *['field/set']({ payload }, { select, put }) {
      if (payload.key === 'groupBy') yield put({
        type: 'cursor/toggle',
        payload: undefined,
        level: 0
      })
      yield put({
        type: 'forecastOverview/ranges/calc'
      })
      yield put({
        type: 'groups/page/reset'
      })
      yield put({
        type: 'assets/page/reset'
      })
      yield put({
        type: 'groups/data/get'
      })
      yield put({
        type: 'assets/data/get'
      })
      yield put({
        type: 'overview/data/get'
      })
      const to = yield select(state => state.filters.range.to)
      if (moment(to) > moment()) {
        yield put({
          type: 'assets/rate/get'
        })
        const cursor = yield select(state => state.filters.cursor)
        if (cursor.length < 2) yield put({
          type: 'suggestions/data/get/all'
        })
      }
    },
    *['cursor/toggle']({ payload, level}, { put, select }) {
      if (level === 1) {
        yield put({
          type: 'assets/page/reset',
        })
        yield put({
          type: 'assets/data/get'
        })
        const to = yield select(state => state.filters.range.to)
        if (moment(to) > moment()) {
          yield put({
            type: 'assets/rate/get'
          })
          yield put({
            type: 'suggestions/data/get/all'
          })
        }
      }
      if (level === 2) {
        const type = yield select(state => state.filters.type)
        if (type === 'forecast') yield put({
          type: 'forecastOverview/data/get'
        })
      }
      yield put({
        type: 'overview/data/get'
      })
    }
  },
  reducers: {
    ['cursor/toggle'](state, { payload, level, noReset }) {
      const { cursor } = state
      if (payload === cursor[level - 1] && !noReset) {
        return {
          ...state,
          cursor: cursor.slice(0, level - 1)
        }
      } else {
        const newCursor = cursor.slice(0, level)
        newCursor[level - 1] = payload
        return {
          ...state,
          cursor: newCursor
        }
      }
    },
    ['cursor/reset'](state, { level }) {
      return {
        ...state,
        cursor: state.cursor.slice(0, level - 1)
      }
    },
    ['field/set'](state, { payload: { key, value } }) {
      if (key === 'type') {
        if (value === 'forecast') {
          return {
            ...state,
            [key]: value,
            range: {
              from: ranges.currentYear.start.format('YYYY-MM-DD'),
              to: ranges.currentYear.start.endOf('year').format('YYYY-MM-DD')
            }
          }
        } else {
          return {
            ...state,
            [key]: value,
            range: {
              from: ranges.oneYear.start.format('YYYY-MM-DD'),
              to: ranges.oneYear.end.format('YYYY-MM-DD')
            }
          }
        }

      }
      const intValue = parseInt(value)
      if (isNaN(intValue)) {
        return {
          ...state,
          [key]: value
        }
      } else {
        return {
          ...state,
          [key]: intValue
        }
      }
    }
  }
}
