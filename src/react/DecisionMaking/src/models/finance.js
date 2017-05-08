/* @flow */
import axios from 'axios'
import { routerRedux } from 'dva/router'

import { currentYear } from '#/constants'

export default {
  namespace: 'finance',
  state: {
    loading: false,
    systemData: undefined,
    manualData: undefined,
    query: {}
  },
  subscriptions: {
    setup ({ dispatch, history }) {
      return history.listen(({query}) => {
        const { groupby, year } = query

        if (!groupby) {
          return dispatch(routerRedux.push({
            pathname: '/',
            query: {
              ...query,
              groupby: 'type'
            }
          }))
        }

        if (!year) {
          return dispatch(routerRedux.push({
            pathname: '/',
            query: {
              ...query,
              year: currentYear
            }
          }))
        }

        dispatch({
          type: 'data/get',
          payload: query
        })
      })
    }
  },
  effects: {
    *['data/get'] ({ payload: params }, { put, call, select, take }) {
      try {
        yield put({ type: 'loading/on' })

        const { data } = yield call(
          axios,
          {
            url: process.env.API_HOST + '/dmv2',
            params
          }
        )

        yield put({
          type: 'query/update',
          payload: params
        })

        yield put({
          type: 'data/get/succeed',
          payload: data
        })

        yield put({
          type: 'focus/cursor/reset'
        })

        yield put({
          type: 'config/changes/reset'
        })

        yield put({
          type: 'nodeList/data/get',
          payload: data
        })

        yield put({ type: 'loading/off' })

        // init config
        const action = yield take('nodeList/data/get/succeed')
        yield put({
          type: 'config/data/set',
          payload: action.payload
        })
      } catch (err) {
        yield put({
          type: 'data/get/failed',
          payload: err
        })
      }
    },
    *['data/put'] ({ payload }, { put, call, select, take }) {
      try {
        const { query } = yield select(state => state.finance)

        const { data } = yield call(
          axios,
          {
            method: 'put',
            url: process.env.API_HOST + '/dmv2',
            params: query,
            data: { config: payload }
          }
        )

        yield put({
          type: 'data/put/succeed',
          payload: data
        })

        yield put({
          type: 'nodeList/data/get',
          payload: data
        })

        // set config
        const action = yield take('nodeList/data/get/succeed')
        yield put({
          type: 'config/data/set',
          payload: action.payload
        })        
      } catch (err) {
        yield put({
          type: 'data/put/failed',
          payload: err
        })
      }
    },
    *['data/toggle'] ({ payload = 'system' }, { put, select }) {
      try {
        const finance = yield select(state => state.finance)
        const dataKey = `${payload}Data`
        yield put({
          type: 'nodeList/data/get',
          payload: finance[dataKey]
        })
      } catch (err) {

      }
    }
  },
  reducers: {
    ['data/get/succeed'] (state, { payload }) {
      return {
        ...state,
        systemData: payload,
        manualData: payload
      }
    },
    ['data/put/succeed'] (state, { payload }) {
      return {
        ...state,
        manualData: payload
      }
    },
    ['query/update'] (state, { payload }) {
      return {
        ...state,
        query: payload
      }
    },
    ['loading/on'] (state, action) {
      return {
        ...state,
        loading: true
      }
    },
    ['loading/off'] (state, action) {
      return {
        ...state,
        loading: false
      }
    }    
  }
}
