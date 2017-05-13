import { call, put, take, takeEvery, takeLatest, fork, all, select } from 'redux-saga/effects'
import Services from 'services'
import cache from 'utils/cache'
import { error } from 'utils/logger'
import EventBus from 'eventbusjs'
const API = Services.api

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
  let type = action.type.replace('update/briefs/', '')
  try {
    let params = yield select(state => state.parameters)
    let briefs = yield call(API.getBriefs, type, params)
    briefs.type = type
    let value = {
      total: (briefs.total || 86), // todo!!!, ask ivar to provide
      skip: briefs.skip || 11,
    }
    yield put({ type: 'update/param/pagination/sync', data: { type, value } })
    EventBus.dispatch('brief-data', briefs)
  } catch (e) {
    error(e)
  }
}

function* fetchReasons(action) {
  let params = action.params
  try {
    let reasons = yield call(API.getReasons, params)
    EventBus.dispatch('reason-data', reasons)
  } catch (e) {
    error(e)
  }
}

function* fetchAll() {
  try {
    let params = yield select((state) => state.parameters)
    yield all([
      call(fetchReasons, {type: 'todo', params}), 
      call(fetchBriefs, {type: 'left', params}), 
      call(fetchBriefs, {type: 'right', params})])
  } catch (e) {
    error(e)
  }
}

function* metaSaga() {
  yield takeLatest('update/meta/', fetchMeta)
}

function* briefSagaLeft() {
  yield takeLatest('update/briefs/left', fetchBriefs)
}
function* briefSagaRight() {
  yield takeLatest('update/briefs/right', fetchBriefs)
}

function* paramChangeSaga() {
  yield takeLatest(act => act.type.startsWith('update/param/') && !act.type.endsWith('sync')
  , fetchAll)
}

function* reasonSaga() {
  yield takeLatest('update/reasons', fetchReasons)
}

export default function* sagas() {
  yield all([
    metaSaga(), 
    briefSagaLeft(),
    briefSagaRight(),
    reasonSaga(),
    paramChangeSaga()
  ])
}