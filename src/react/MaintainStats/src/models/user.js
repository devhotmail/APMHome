/* @flow */
export default {
  namespace: 'user',
  state: {
    info: {}
  },
  reducers: {
    ['info/set'] (state, { payload }) {
      return {
        ...state,
        info: payload
      }
    }
  }
}
