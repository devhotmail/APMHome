/* @flow */
export default {
  namespace: 'focus',
  state: {
    data: undefined
  },
  reducers: {
    ['set'] (state, { payload }) {
      return {
        ...state,
        data: payload
      }
    }
  }
}
