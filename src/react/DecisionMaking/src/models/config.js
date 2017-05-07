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
    *['changes'] ({ payload }, { put, call, select }) {
      try {
        yield put({
          type: 'changes/succeed',
          payload
        })

      } catch (err) {

      }
    },
    *['changes/submit'] ({ payload, resolve, reject }, { put, call, select }) {
      try {
        /**
         * todo:
         * Is it possible that API only need the changed part instead of threshold
         */
        const { changes, data: configList } = yield select(state => state.config)

        const datum = Object.keys(changes).reduce((prev, cur) => {
          const item = changes[cur]
          const { cursor, increase, max, min } = item
          const configTarget = configList.find(n => isFocusNode(n, cursor))
          const { children } = configTarget

          const changedOne = {
            id: cur,
            children: Array.isArray(children) ? children.map(n => n.data.id) : []
          }

          if (increase) changedOne.change = increase

          if (max || min) {
            changedOne.threshold =  [
              min || configTarget.data.usage_threshold[0],
              max || configTarget.data.usage_threshold[1]
            ]
          }

          prev.push(changedOne)
          return prev
        }, [])
        .filter(n => Array.isArray(n.children))
        .map(n => {
          const { id, children, ...otherProps } = n
          return children.map(id => ({
            id,
            ...otherProps
          }))
        })

        const body = [].concat.apply([], datum)

        yield put({
          type: 'finance/data/put',
          payload: body
        })

        resolve && resolve()
      } catch (err) {
        reject && reject()
        console.log(err)
      }
    }    
  },
  reducers: {
    ['changes/succeed'] (state, { payload }) {
      const { cursor: [ id ] } = payload

      return {
        ...state,
        changes: {
          ...state.changes,
          [id]: {
            ...(state.changes[id] || {}),
            ...payload
          }
        }
      }
    },
    ['changes/reset'] (state, { payload }) {
      return {
        ...state,
        changes: {}
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
