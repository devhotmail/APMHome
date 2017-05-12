export default {
  namespace: 'filters',
  state: {
    type: 'future',
    from: '2017-01-01',
    to: '2017-12-31',
    groupBy: 'dept',
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
    }
  },
  reducers: {
    ['data/change'](state, { payload, level }) {
      const data = state.data
      data[level] = payload
      return {
        ...state,
        data: data.slice(0, level + 1)
      }
    }
  }
}
