import { call, put, take, takeEvery, takeLatest, fork, all, select } from 'redux-saga/effects'
import Services from 'services'
import _ from 'lodash'
import cache from 'utils/cache'
import { error } from 'utils/logger'
import EventBus from 'eventbusjs'
const API = Services.api

const DateFormat = 'YYYY-MM-DD'
const GroupByMapping = {
  'display_assettype': 'type',
  'display_brand': 'supplier',
  'display_dept': 'dept'
}
/** Do not temper the original object !! */
function mapParamsBrief(parameters) {
  return {
    from: parameters.period.from.format(DateFormat),
    to: parameters.period.to.format(DateFormat),
    limit: parameters.pagination.left.top,
    groupby: GroupByMapping[parameters.display],
  }
}

function mapParamsDetail(paramters) {
  return {
    limit: paramters.pagination.right.top
  }
}

function* fetchMeta() {
  try {
    let [ depts, types ] = yield all([call(API.getDepartments), call(API.getAssetTypes)])
    cache.set('departments', depts)
    cache.set('assettypes', types)
    yield put({ type: 'update/meta/departments', data: depts })
    yield put({ type: 'update/meta/assettypes', data: types })
  } catch(e) {
    error(e)
  }
}

function* fetchBriefs(action) {
  try {
    let store = yield select(state => state.parameters)
    let params = mapParamsBrief(store)
    if (action.type === 'page/change') {
      // do page change thing
    }
    if (action.type === 'update/param') {
      // do general reload when param changes
    }
    let briefs = yield call(API.getBriefs, params)
    
    // sync pagination
    let value = {
      total: briefs.page.total,
      skip: briefs.page.skip,
    }
    yield put({ type: 'update/param/pagination/sync', data: { type: 'left', value } })
    EventBus.dispatch('brief-data', this, briefs)
  } catch (e) {
    error(e)
  }
}

function* fetchDetails(action) {
  try {
    let store = yield select(state => state.parameters)
    let params = mapParamsDetail(store)
    if (action.type === 'page/change') {
      // do page change thing
    }
    if (action.type === 'update/param') {
      // do general reload when param changes
    }

    let details = yield call(API.getDetails, params)
    // sync pagination
    let value = {
      total: details.page.total,
      skip: details.page.skip,
    }
    yield put({ type: 'update/param/pagination/sync', data: { type: 'right', value } })
    EventBus.dispatch('detail-data', this, details)
  } catch (e) {
    error(e)
  }
}

function* fetchGross() {
  let result = yield {}
  EventBus.dispatch('gross-data', this, result)
}

function* fetchAll(action) {
  try {
    // do nothing for paginator sync action
    if (action.type === 'update/param/pagination/sync') {
      return
    }
    // distribution changes only effect gross data
    if (action.type === 'update/param/distribution') {
      yield call(fetchGross)
      return
    }
    let params = yield select((state) => state.parameters)
    yield all([
      // call(fetchGross, { params }),
      call(fetchDetails, { params }), 
      call(fetchBriefs, { params })])
  } catch (e) {
    error(e)
  }
}

function* pageChange(action) {
  if (action.data.type === 'brief') {
    yield call(fetchBriefs, {})
  } else {
    yield call(fetchDetails, {})
  }
}

function* metaSaga() {
  yield takeLatest('update/meta/', fetchMeta)
}

function* paramChangeSaga() {
  yield takeLatest(act => act.type.startsWith('update/param/')
  , fetchAll)
}

function* pageChangeSaga() {
  yield takeLatest('page/change', pageChange)
}

function* getBriefsSaga() {
  yield takeLatest('get/briefs', fetchBriefs)
}

function* getDetailsSaga() {
  yield takeLatest('get/details', fetchDetails)
}
export default function* sagas() {
  yield all([
    metaSaga(), 
    paramChangeSaga(),
    pageChangeSaga(),
    getBriefsSaga(),
    getDetailsSaga()
  ])
}