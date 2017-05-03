/* @flow */
import { round } from '#/utils'

export default {
  namespace: 'config',
  state: {
    loading: false,
    data: undefined,
  },
  subscriptions: {
    setup({ dispatch }) {
    }
  },
  effects: {
    *['data/set'] ({ payload }, { put, call, select }) {
      try {
        const config = getConfig(payload)
        yield put({
          type: 'data/set/succeed',
          payload: config
        })
      } catch (err) {

      }
    }
  },
  reducers: {
    ['data/set/succeed'] (state, { payload }) {
      return {
        ...state,
        data: payload
      }
    }      
  }
}

function getConfig (nodes) {
  return nodes.map(n => ({
    parent: n.parent ? {
      id: n.parent.data.id,
      uid: n.parent.data.uid
    }: null,
    children: n.children ? n.children.map(child => ({
      uid: child.data.uid,
      id: child.data.id
    })) : null,
    depth: n.depth,
    height: n.height,
    id: n.data.id,
    uid: n.data.uid,
    name: n.data.name,
    // for the display usage # 1
    change: round(n.data.revenue_increase_sug * 100, 1)
  }))
}
