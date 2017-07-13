import { call, put, takeEvery, takeLatest, all, select } from 'redux-saga/effects'
import Services from 'services'
import cache from 'utils/cache'
import { error } from 'utils/logger'
import EventBus from 'eventbusjs'
import { DateFormat, GroupByMap, DataTypeMap } from 'converters'
const API = Services.api

/** Do not temper the original object !! */
// TODO: extract common params
function mapParamsBrief(params) {
  let { assettype, dept } = params.filterBy
  return {
    from: params.period.from.format(DateFormat),
    to: params.period.to.format(DateFormat),
    start: params.pagination.left.skip,
    limit: params.pagination.left.top,
    groupby: GroupByMap[params.display],
    type: assettype === 'all_assettype' ? undefined : assettype ,
    dept: dept === 'all_dept' ? undefined : dept,
    orderby: DataTypeMap[params.dataType]
  }
}

function mapParamsDetail(params) {
  let { assettype, dept } = params.filterBy
  return {
    from: params.period.from.format(DateFormat),
    to: params.period.to.format(DateFormat),
    start: params.pagination.right.skip,
    limit: params.pagination.right.top,
    type: assettype === 'all_assettype' ? undefined : assettype ,
    dept: dept === 'all_dept' ? undefined : dept,
    orderby: DataTypeMap[params.dataType]
  }
}

function mapParamsGross(params) {
  let { assettype, dept } = params.filterBy
  return {
    from: params.period.from.format(DateFormat),
    to: params.period.to.format(DateFormat),
    typeId: assettype === 'all_assettype' ? undefined : assettype ,
    deptId: dept === 'all_dept' ? undefined : dept
  }
}

function mapParamsPhase(params, phase) {
  let { assettype, dept } = params.filterBy
  let { distributionEttr, distributionResponse, distributionArrival, dataType } = params
  dataType = phase || dataType
  let distribution
    if (dataType === 'ettr') {
      distribution = distributionEttr
    } else if (dataType === 'arrival_time') {
      distribution = distributionArrival
    } else { // response_time
      distribution = distributionResponse
    }
  return {
    from: params.period.from.format(DateFormat),
    to: params.period.to.format(DateFormat),
    typeId: assettype === 'all_assettype' ? undefined : assettype ,
    deptId: dept === 'all_dept' ? undefined : dept,
    t1: distribution[1],
    t2: distribution[2],
    tmax: distribution[3],
    phase: DataTypeMap[dataType]
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
      params.start = params.limit * (action.data.value - 1)
    }
    let briefs = yield call(API.getBriefs, params)
    // sync pagination
    let value = {
      total: briefs.page.total,
      skip: briefs.page.start,
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
      params.start = params.limit * (action.data.value - 1)
    }

    let details = yield call(API.getDetails, params)
    // sync pagination
    let value = {
      total: details.page.total,
      skip: details.page.start,
    }
    yield put({ type: 'update/param/pagination/sync', data: { type: 'right', value } })
    EventBus.dispatch('detail-data', this, details)
  } catch (e) {
    error(e)
  }
}

function* fetchGross() {
  try {
    let store = yield select(state => state.parameters)
    let params = mapParamsGross(store)
    // if (action.type === 'update/param') {
    //   // do general reload when param changes
    // }
    let gross = yield call(API.getGross, params)
    EventBus.dispatch('gross-data', this, gross)
  } catch (e) {
    error(e)
  }
}

function* fetchPhase(action) {
  try {
    let store = yield select(state => state.parameters)
    let params = mapParamsPhase(store, action.data)
    let phase = yield call(API.getPhase, params)
    phase.phase = params.phase
    EventBus.dispatch('phase-data', this, phase)
  } catch (e) {
    error(e)
  }
}

function* fetchAll(action) {
  try {
    // distribution changes only effect phase data
    if (action.type.startsWith('update/param/distribution')) {
      yield call(fetchPhase, {})
      return
    }
    yield all([
      call(fetchGross, action),
      call(fetchDetails, action),
      call(fetchBriefs, action),
      call(fetchPhase, { data: 'ettr' }),
      call(fetchPhase, { data: 'arrival_time' }),
      call(fetchPhase, { data: 'response_time' }),
    ])
  } catch (e) {
    error(e)
  }
}

function* pageChange(action) {
  if (action.data.type === 'brief') {
    yield call(fetchBriefs, action)
  } else {
    yield call(fetchDetails, action)
  }
}

function* metaSaga() {
  yield takeLatest('update/meta/', fetchMeta)
}

function* paramChangeSaga() {
  yield takeLatest(act => act.type.startsWith('update/param/') && !act.type.endsWith('sync')
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

function* getGrossSaga() {
  yield takeLatest('get/gross', fetchGross)
}

function* getPhase() {
  yield takeEvery('get/phase', fetchPhase)
}

export default function* sagas() {
  yield all([
    metaSaga(),
    paramChangeSaga(),
    pageChangeSaga(),
    getBriefsSaga(),
    getDetailsSaga(),
    getGrossSaga(),
    getPhase()
  ])
}