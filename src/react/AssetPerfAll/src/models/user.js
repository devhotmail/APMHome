/* @flow */

const isHeadEl = document.querySelector('#user-context #isHead')
const isHead = isHeadEl ? JSON.parse(isHeadEl.value) : false

const orgIdEl = document.querySelector('#user-context #orgId')
const orgId = isHead ? undefined : parseInt(orgIdEl.value)

export default {
  namespace: 'user',
  state: {
    info: {
      isHead,
      orgId
    }
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
