export default {
  namespace: 'confirm',
  state: {},
  effects: {
    *['clicked'](_, { put }) {
      yield put({
        type: 'assets/data/get'
      })
      yield put({
        type: 'forecastOverview/data/get'
      })
      yield put({
        type: 'groups/data/get'
      })
      yield put({
        type: 'overview/data/get'
      })
      yield put({
        type: 'suggestions/data/get'
      })
    }
  }
}
