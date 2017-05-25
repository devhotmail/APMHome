/* @flow */
import { round, isFocusNode } from '#/utils'
import axios from 'axios'
import { pickBy } from 'lodash'
import { API_HOST } from '#/constants'

const isHeadEl = document.querySelector('#user-context #isHead')
const isHead = isHeadEl ? JSON.parse(isHeadEl.value) : false

const orgIdEl = document.querySelector('#user-context #orgId')
const orgId = isHead ? undefined : parseInt(orgIdEl.value)

export default {
  namespace: 'config',
  state: {
    loading: false,
    data: [],
    changes: []
  },
  subscriptions: {
    setup ({ dispatch }) {
      dispatch({
        type: 'data/get',
        level: 0
      })
    }
  },
  effects: {
  },
  reducers: {
  }
}
