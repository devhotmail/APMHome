import _ from 'lodash'
import moment from 'moment'
import { ACT_UPDATE_PARAM } from 'actions'

const Current = moment()
const YearStart = moment(new Date().getFullYear(), 'YYYY')

const initParameter = {

  filterBy: {
    ['assettype']: 'all_asset_type',
    ['dept']: 'all_dept'
  },
  orderBy: 'operation_rate',
  period: {
    from: YearStart,
    to: Current
  },
  dataType: 'operation_rate',
  showLastYear: false,
  display: 'display_asset_type', // type | brand | name
  pagination: {
    left: {
      skip: 0,
      top: 6,
      total: 0,
    },

    right: {
      skip: 0,
      top: 16,
      total: 0
    }
  }
}

const reducers = {
  ['update/param/filter'](next, data) {
    next.filterBy[data.type] = data.data.key
    return next
  },

  ['update/param/order'](next, data) {
    next.orderBy = data.key
    return next
  },

  ['update/param/period'](next, data) {
    next.period = {
      from: data[0],
      to: data[1]
    }
    return next
  },

  ['update/param/datatype'](next, data) {
    next.dataType = data
    return next
  },

  ['update/param/showlastyear'](next, data) {
    next.showLastYear = data
    return next
  },

  ['update/param/display'](next, data) {
    next.display = data
    return next
  },

  ['update/param/pagination'](next, data) {
    let pag = next.pagination[data.type]
    Object.assign(pag, data.value)
    return next
  }

}


export default (state = initParameter, action) => {

  if (!action.type.startsWith(ACT_UPDATE_PARAM)) {
    return state
  }
  return reducers[action.type](_.cloneDeep(state), action.data)

}