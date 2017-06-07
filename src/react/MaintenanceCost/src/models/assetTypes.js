import axios from 'axios'
import { API_HOST } from '#/constants'
export default {
  namespace: 'assetTypes',
  state: [],
  subscriptions: {
    setup({history, dispatch}) {
      dispatch({
        type: 'get'
      })
    }
  },
  effects: {
    *['get']({ payload } , { put, call }) {
      try {
        const { data } = yield call(axios, API_HOST + '/msg', { params: {type: 'assetGroup'}})
        yield put({
          type: 'get/succeeded',
          payload: data
        })
      } catch(err) {
        console.error(err)
      }
    }
  },
  reducers: {
    ['get/succeeded'](state, { payload }) {
      return Object.entries(payload).map(([id, name]) => ({id: Number(id), name}))
    }
  }
}
