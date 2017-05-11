/* @flow */
export default {
  namespace: 'focus',
  state: {
    loading: false,
    data: undefined
  },
  effects: {   
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
