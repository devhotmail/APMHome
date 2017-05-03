/* @flow */
import axios from 'axios'
import uuid from 'uuid/v4'
import * as d3 from 'd3'

import { round } from '#/utils'
import data from '#/mock/data'

function formatData (data) {
  return {
    children: data.items,
    ...data.root
  }
}

const childKey = 'items'

function flatten (data: Object): Array<Object> {
  const result = []
  const stack = [data]

  while (stack.length) {
    const item = stack.pop()
    const uid = uuid()
    const children = item[childKey]

    if (Array.isArray(children) && children.length) {
      Array.prototype.push.apply(stack, children.map(n => ({
        ...n,
        parent: uid
      })))
    }

    // remove original childKey && sizeKey
    delete item[childKey]

    result.push({
      ...item,
      uid // give each item an unique id attribute
    })
  }
  return result
}

function getConfigFromData (data) {
  const root = d3.hierarchy(data, d => d.items)
  const tree = d3.tree()
  const nodes = tree(root).descendants()

  return nodes.map(n => ({
    ...n,
    data: {
      ...n.data,
      uid: uuid()
    }
  })).map(n => {
    return {
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
      increase: round(n.data.revenue_increase_sug * 100, 1)
    }
  })
}

export default {
  namespace: 'financial',
  state: {
    loading: false,
    data: data,
    config: undefined
  },
  subscriptions: {
    setup({ dispatch }) {
      dispatch({
        type: 'config/set'
      })
    }
  },
  effects: {
    *['data/get'] (action, { put, call, select }) {
      const params = {
        year: 2016,
        groupby: 'type',
        start: 0,
        limit: 10
      }

      try {
        const { data } = yield call(
          axios.get,
          process.env.API_HOST + '/profit',
          { params }
        )

        yield put({
          type: 'data/get/succeed',
          payload: data
        })
      } catch(err) {
        yield put({
          type: 'data/get/failed',
          payload: err
        })
      }
    },
    *['config/set'] (action, { put, select, call }) {
      const data = yield select(state => state.financial.data)
      const config = yield call(getConfigFromData, data)
      yield put({
        type: 'config/set/succeed',
        payload: config
      })
    }
  },
  reducers: {
    ['data/get/succeed'] (state, { payload }) {
      return {
        ...state,
        data: formatData(payload)
      }
    },
    ['config/set/succeed'] (state, { payload }) {
      return {
        ...state,
        config: payload
      }
    }    
  }
}
