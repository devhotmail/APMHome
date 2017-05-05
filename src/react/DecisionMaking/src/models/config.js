/* @flow */
import { round, isFocusNode } from '#/utils'

export default {
  namespace: 'config',
  state: {
    loading: false,
    data: undefined,
    changes: {}
  },
  subscriptions: {
    setup({ dispatch }) {
    }
  },
  effects: {
    *['data/set'] ({ payload }, { put, call, select }) {
      try {
        yield put({
          type: 'data/set/succeed',
          payload: payload
        })
      } catch (err) {

      }
    },
    *['change'] ({ payload }, { put, call, select }) {
      try {
        yield put({
          type: 'change/succeed',
          payload
        })

      } catch (err) {

      }
    }
  },
  reducers: {
    ['change/succeed'] (state, { payload }) {
      const { cursor: [ id ], ...changedProps } = payload

      return {
        ...state,
        changes: {
          ...state.changes,
          [id]: {...changedProps}
        }
      }
    },
    ['data/set/succeed'] (state, { payload }) {
      return {
        ...state,
        data: payload
      }
    }      
  }
}

function getConfig (nodes) {
  const root = nodes[0]
  const { depth, height } = root
  return nodes
  .filter(n => !~[depth, height].indexOf(n.depth))
}

function pickUpFields (obj) {
  return {
    depth: obj.depth,
    height: obj.height,
    id: obj.data.id,
    uid: obj.data.uid,
    name: obj.data.name,      
    // for the display usage # 1 hard code OMG !!!!!
    change: round(obj.data.usage_predict * 100, 1),
    usage_min: obj.data.usage_threshold ? obj.data.usage_threshold[0] * 100 : '',
    usage_max: obj.data.usage_threshold ? obj.data.usage_threshold[1] * 100 : ''  
  }
}
