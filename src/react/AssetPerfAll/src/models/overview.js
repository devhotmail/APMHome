import axios from 'axios'
import { API_HOST, dateFormat } from '#/constants'
import moment from 'moment'

export default {
  namespace: 'overview',
  state: {
    data: [],
  },
  subscriptions: {
    setup({dispatch}) {
      dispatch({
        type: 'data/get'
      })
    }
  },
  effects: {
    *['data/get']({ payload }, { put, select, call }) {
      const now = moment()
      const yesterday = now.clone().subtract(1, 'day')
      try {
        const res = yield Promise.all([
          axios({
            method: 'get',
            url: API_HOST + '/profit',
            params: {
              from: now.clone().subtract(2, 'year').startOf('year').format(dateFormat),
              to:  now.clone().subtract(2, 'year').endOf('year').format(dateFormat)
            }
          }),
          axios({
            method: 'get',
            url: API_HOST + '/profit',
            params: {
              from: now.clone().subtract(1, 'year').startOf('year').format(dateFormat),
              to:  now.clone().subtract(1, 'year').endOf('year').format(dateFormat)
            }
          }),
          axios({
            method: 'get',
            url: API_HOST + '/profit',
            params: {
              from: yesterday.clone().startOf('year').format(dateFormat),
              to: yesterday.clone().format(dateFormat)
            }
          }),
          axios({
            method: 'get',
            url: API_HOST + '/profit/forecast',
            params: {
              from: now.clone().startOf('year').format(dateFormat),
              to: now.clone().endOf('year').format(dateFormat)
            }
          }),
          axios({
            method: 'get',
            url: API_HOST + '/profit/forecastrate',
            params: {
              from: now.clone().startOf('year').format(dateFormat),
              to: now.clone().endOf('year').format(dateFormat),
              groupby: 'type'
            }
          })
        ])
        const data = res.map(item => item.data.root)
        yield put({
          type: 'data/get/succeeded',
          payload: data
        })
      } catch (err) {
        console.log(err);
      }
    }
  },
  reducers: {
    ['data/get/succeeded'](state, { payload }) {
      return {
        ...state,
        data: payload
      }
    }
  }
}
