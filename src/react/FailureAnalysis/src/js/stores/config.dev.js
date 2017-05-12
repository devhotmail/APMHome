import { createStore, applyMiddleware, compose, combineReducers } from 'redux'
import createSagaMiddleware from 'redux-saga'
import { persistState } from 'redux-devtools'
import promiseMiddleware from 'redux-promise'
import createLogger from 'redux-logger'

import rootReducer from 'reducers/root'
import sagas from 'sagas'

 /**
  * Redux logger
  */
const logger = createLogger()
const sagaMW = createSagaMiddleware()
const middlewares = [sagaMW, promiseMiddleware, logger, require('redux-immutable-state-invariant')()]

// By default we try to read the key from ?debug_session=<key> in the address bar
const getDebugSessionKey = function () {
  const matches = window.location.href.match(/[?&]debug_session=([^&]+)\b/)
  return (matches && matches.length) ? matches[1] : null
}

const enhancer = (...otherMiddleware) => compose(
  applyMiddleware(...middlewares, ...otherMiddleware),
  // window.devToolsExtension ? window.devToolsExtension() : DevTools.instrument(),
  // Optional. Lets you write ?debug_session=<key> in address bar to persist debug sessions
  persistState(getDebugSessionKey())
)

export default function configureStore(initialState, ...middleware) {
  let reducers = combineReducers(rootReducer)
  const store = createStore(reducers, initialState, enhancer(...middleware))
  sagaMW.run(sagas)
  // Enable hot module replacement for reducers (requires Webpack or Browserify HMR to be enabled)
  if (module.hot) {
    // module.hot.accept('../reducer', () => {
    //   const nextReducer = require('../reducer').default
    //   store.replaceReducer(nextReducer)
    // })
  }

  return store
}
