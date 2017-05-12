import { call, put, take, takeEvery, takeLatest, fork, all } from 'redux-saga/effects'
import Services from 'services'
import cache from 'utils/cache'
import { error } from 'utils/logger'
import EventBus from 'eventbusjs'
const API = Services.api

function* fetchMeta() {
  try {
    let [ depts, types ] = yield [call(API.getDepartments), call(API.getAssetTypes)]
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
    let briefs = yield call(API.getBriefs, type)
    briefs.type = type
    EventBus.dispatch('brief-data', briefs)
  } catch (e) {
    error(e)
  }
}

function* fetchReasons(action) {
  try {
    let reasons = yield call(API.getReasons, action.type)
    EventBus.dispatch('reason-data', reasons)
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
  
}

function* reasonSaga() {
  yield takeLatest('update/reasons', fetchReasons)
}

export default function* sagas() {
  yield all([
    metaSaga(), 
    briefSagaLeft(),
    briefSagaRight(),
    reasonSaga()
  ])
}