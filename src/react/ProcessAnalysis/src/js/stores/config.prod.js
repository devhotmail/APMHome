import { createStore, applyMiddleware, compose, combineReducers } from 'redux'
import createSagaMiddleware from 'redux-saga'
import promiseMiddleware from 'redux-promise'

import rootReducer from 'reducers/root'
import sagas from 'sagas'

const sagaMW = createSagaMiddleware()
const middlewares = [sagaMW, promiseMiddleware, require('redux-immutable-state-invariant')()]


const enhancer = (...otherMiddleware) => compose(
  applyMiddleware(...middlewares, ...otherMiddleware),
)

export default function configureStore(initialState, ...middleware) {
  let reducers = combineReducers(rootReducer)
  const store = createStore(reducers, initialState, enhancer(...middleware))
  sagaMW.run(sagas)
  return store
}
