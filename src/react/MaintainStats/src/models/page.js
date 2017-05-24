/* @flow */
export default {
  namespace: 'page',
  state: {
    loading: false
  },
  reducers: {
    ['loading/on'] (state, { payload }) {
      return {
        ...state,
        data: payload
      }
    }
  }
}
