import { cloneDeep } from 'lodash-es'
import cache from 'utils/cache'
import { ACT_UPDATE_META } from 'actions'

const initParameter = {
  departments: cache.get('departments'),
  assetTypes: cache.get('assettypes'),
}

const reducers = {
  ['update/meta/departments'](next, data) {
    next.departments = data
    return next
  },

  ['update/meta/assettypes'](next, data) {
    next.assetTypes = data
    return next
  }
}


export default (state = initParameter, action) => {

  if (!action.type.startsWith(ACT_UPDATE_META)) {
    return state
  }
  let reducer = reducers[action.type]
  if (reducer) {
    let meta = cloneDeep(state)
    reducer(meta, action.data)
    return meta
  } else {
    return state
  }
}