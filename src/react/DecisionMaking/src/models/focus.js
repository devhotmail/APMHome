import { isSameCursor, isFocusNode } from '#/utils'

export default {
  namespace: 'focus',
  state: {
    loading: false,
    node: undefined,
    cursor: []
  },
  subscriptions: {
    setup({ dispatch }) {
    }
  },
  effects: {
    *['cursor/set'] ({ payload }, { put, call, select }) {
      // should we validate the cursor is correct ?
      yield put({
        type: 'cursor/set/succeed',
        payload
      })

      yield put({ type: 'node/set' })
    },
    *['node/set'] ({ payload }, { take }) {
      try {
        let { cursor } = yield select(state => state.focus)
        if (!cursor) {
          const action = yield take('cursor/set/succeed')
          cursor = action.payload
        }

        const { id, depth } = cursor

        const target = nodeList.find(n => n.data.id === id && n.depth === depth)
        if (target) {
          yield put({
            type: 'node/set/succeed',
            payload: target
          })
        }
      } catch(err) {

      }
    }
  },
  reducers: {
    ['cursor/set/succeed'] (state, { payload }) {
      if (isSameCursor(payload, state.cursor)) return state
      return {
        ...state,
        cursor: payload
      }
    },
    ['node/set/succeed'] (state, { payload }) {
      if (isFocusNode(payload, state.cursor)) return state
      return {
        ...state,
        node: payload
      }
    }    
  }
}
