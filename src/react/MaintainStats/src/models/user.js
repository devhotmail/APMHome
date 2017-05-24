/* @flow */
export default {
  namespace: 'user',
  state: {
    data: {}
  },
  reducers: {
    ['info/set'] (state, { payload }) {
      return {
        ...state,
        data: payload
      }
    }
  }
}
