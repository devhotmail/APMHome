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
    *['field/set']({ payload }, { put }) {
      if (payload.key === 'groupBy') yield put({
        type: 'cursor/toggle',
        payload: undefined,
        level: 0
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
    },
    *['cursor/toggle']({ payload, level}, { put }) {
      if (level === 1) {
        yield put({
          type: 'assets/page/reset',
        })
        yield put({
          type: 'assets/data/get'
        })
      }
      yield put({
        type: 'overview/data/get'
      })
    }
  },
  reducers: {
    ['cursor/toggle'](state, { payload, level }) {
      const { cursor } = state
      if (payload === cursor[level - 1]) {
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
              to: ranges.currentYear.end.format('YYYY-MM-DD')
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
