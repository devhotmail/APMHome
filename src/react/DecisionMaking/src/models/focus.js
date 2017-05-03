import axios from 'axios'

export default {
  namespace: 'focus',
  state: {
    loading: false,
    data: undefined
  },
  subscriptions: {
    setup({ dispatch }) {
    }
  },
  effects: {
    *['data/set'] ({ payload }, { put, call, select }) {
      yield put({
        type: 'data/set/succeed',
        payload
      })
    },
    *['data/setByInfo'] ({ payload }, { put, call, select }) {
      const { uid } = payload
      try {
        const nodeList = yield select(state => state.nodeList.data)
        const target = nodeList.find(n => n.data.uid === uid)
        yield put({
          type: 'setByInfo/succeed',
          payload: target
        })
      } catch (err) {

      }
    },
  },
  reducers: {
    ['data/set/succeed'] (state, { payload }) {
      return {
        ...state,
        data: payload
      }
    },
    ['setByInfo/succeed'] (state, { payload }) {
      return {
        ...state,
        data: payload
      }
    }    
  }
}
