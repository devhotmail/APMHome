import axios from 'axios'

export default {
  namespace: 'focus',
  state: {
    loading: false,
    data: undefined,
    info: undefined
  },
  subscriptions: {
    setup({ dispatch }) {
      // dispatch({
      //   type: 'data/get'
      // })
    }
  },
  effects: {
    *['set'] ({ payload }, { put, call, select }) {
      yield put({
        type: 'set/succeed',
        payload
      })
    },
    *['setByInfo'] ({ payload }, { put, call, select }) {
      yield put({
        type: 'setByInfo/succeed',
        payload
      })
    },    
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
    }
  },
  reducers: {
    ['set/succeed'] (state, { payload }) {
      return {
        ...state,
        data: payload
      }
    },
    ['setByInfo/succeed'] (state, { payload }) {
      return {
        ...state,
        info: payload
      }
    }    
  }
}
