import axios from 'axios'
import { API_HOST } from '#/constants'

const isHeadEl = document.querySelector('#user-context #isHead')
const isHead = isHeadEl ? JSON.parse(isHeadEl.value) : false

const orgIdEl = document.querySelector('#user-context #orgId')
const orgId = isHead ? undefined : parseInt(orgIdEl.value)

export default {
  namespace: 'depts',
  state: isHead ? [] : null,
  subscriptions: {
    setup({history, dispatch}) {
      dispatch({
        type: 'get'
      })
    }
  },
  effects: {
    *['get']({ payload }, { put, call }) {
      try {
        const { data } = yield call(axios, API_HOST + '/org/all')
        yield put({
          type: 'get/succeeded',
          payload: data.orgInfos
        })
      } catch(err) {
        console.error(err)
      }
    }
  },
  reducers: {
    ['get/succeeded'](state, { payload }) {
      return payload
    }
  }
}
