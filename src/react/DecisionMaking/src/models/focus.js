import axios from 'axios'

export default {
  namespace: 'focus',
  state: {
    loading: false,
    data: undefined,
    cursor: undefined
  },
  subscriptions: {
    setup({ dispatch }) {
    }
  },
  effects: {
    *['data/set'] ({ payload }, { put, call, select }) {
      const [ id, depth ] = payload
      try {
        const nodeList = yield select(state => state.nodeList.data)
        const target = nodeList.find(n => n.data.id === id && n.depth === depth)
        if (target) {
          yield put({
            type: 'data/set/succeed',
            payload: {
              data: target,
              cursor: [ id, depth ]
            }
          })
        }
      } catch(err) {

      }
    }
  },
  reducers: {
    ['data/set/succeed'] (state, { payload }) {
      return {
        ...state,
        ...payload
      }
    }    
  }
}
