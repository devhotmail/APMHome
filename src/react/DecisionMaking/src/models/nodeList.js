import * as d3 from 'd3'

export default {
  namespace: 'nodeList',
  state: {
    loading: false,
    data: [],
    coefficient: {
      diameter: undefined,
      margin: undefined
    }
  },
  subscriptions: {
    setup({ dispatch }) {
      // dispatch({
      //   type: 'config/set'
      // })
    }
  },
  effects: {
    *['coefficient/set'] ({ payload }, { put }) {
      const { diameter, margin } = payload
      yield put({
        type: 'coefficient/set/succeed',
        payload: payload
      })
      yield put({
        type: 'data/get'
      })
    },
    *['data/get'] ({ payload }, { put, select, call, take }) {
      try {
        let { data } = yield select(state => state.finance)
        if (!data) {
          const action = yield take('finance/data/get/succeed')
          data = action.payload
        }

        let { coefficient } = yield select(state => state.nodeList)

        if (!coefficient.diameter) {
          const action = yield take('coefficient/set/succeed')
          coefficient = action.payload
        }

        const { diameter, margin } = coefficient

        const nodes = getNodes(data, diameter, margin)

        yield put({
          type: 'data/get/succeed',
          payload: nodes
        })

        yield put({
          type: 'config/data/set',
          payload: nodes
        })

        const { depth, data: { id }} = nodes[0]
        yield put({
          type: 'focus/cursor/set',
          payload: [ id, depth ]
        })
      } catch (err) {

      }
    }
  },
  reducers: {
    ['coefficient/set/succeed'] (state, { payload }) {
      return {
        ...state,
        coefficient: {
          ...state.coefficient,
          ...payload
        }
      }
    },
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
  
  return nodes
}
