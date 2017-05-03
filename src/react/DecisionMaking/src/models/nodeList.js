/* @flow */
import * as d3 from 'd3'
import uuid from 'uuid/v4'

export default {
  namespace: 'nodeList',
  state: {
    loading: false,
    data: []
  },
  subscriptions: {
    setup({ dispatch }) {
      // dispatch({
      //   type: 'config/set'
      // })
    }
  },
  effects: {
    *['data/get'] ({ payload }, { put, select, call, take }) {
      let data = yield select(state => state.financial.data)
      if (!data) {
        const action = yield take('financial/data/get/succeed')
        data = action.payload
      }

      const { diameter, margin } = payload
      const nodes = getNodes(data, diameter, margin)

      yield put({
        type: 'data/get/succeed',
        payload: nodes
      })

      yield put({
        type: 'config/data/set',
        payload: nodes
      })

      yield put({
        type: 'focus/data/set',
        payload: nodes[0]
      })
    }
  },
  reducers: {
    ['data/get/succeed'] (state, { payload }) {
      return {
        ...state,
        data: payload
      }
    }        
  }
}

function getNodes (data: Object, diameter: number, margin: number) {
  const pack = d3.pack()
  .size([diameter - margin, diameter - margin])
  .padding(2)

  const root = d3.hierarchy(data, d => d.items)
  .sum(d => d.size) // sizeKey hard code here
  .sort((a, b) => b.value - a.value)

  const nodes = pack(root).descendants()
  
  return nodes.map(n => ({
    ...n,
    data: {
      ...n.data,
      uid: uuid()
    }
  }))
}
