// dependencies
import React from 'react'
import { render } from 'react-dom'
import createHistory from 'history/createBrowserHistory'
import { routerMiddleware } from 'react-router-redux'
import { AppContainer } from 'react-hot-loader'
import Redbox from 'redbox-react'
// polyfills
require('es6-object-assign').polyfill()
require('proxy-polyfill/proxy.min.js')

// tree shaking doesn't work when use strip plugin
const dummy = () => null
const Perf = process.env.NODE_ENV === 'production' ? {
  start: dummy,
  stop: dummy,
  printInclusive: dummy,
  getLastMeasurements: dummy
} : require('react-addons-perf')

// components & stores
import Root from 'containers/Root'
import configureStore from 'stores/config'

// stylesheets, caveat: no alias
import '../styles/glob.sass'

const history = createHistory()
const routerMW = routerMiddleware(history)
const store = configureStore(
  window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__(),
  routerMW
)

const rootElement = document.getElementById('app')

Perf.start()
render(
  <AppContainer errorReporter={Redbox}>
    <Root store={store} history={history} />
  </AppContainer>,
  rootElement
)
Perf.stop()
Perf.printInclusive(Perf.getLastMeasurements())

if (module.hot) {
  /**
   * Warning from React Router, caused by react-hot-loader.
   * The warning can be safely ignored, so filter it from the console.
   * Otherwise you'll see it every time something changes.
   * See https://github.com/gaearon/react-hot-loader/issues/298
   */
   const orgError: string => void = console.error // eslint-disable-line no-console
   console.error = (message) => { // eslint-disable-line no-console
     if (typeof message !== 'string' || message.indexOf('You cannot change <Router routes>') > -1) {
       // Log the error as normally
       orgError.apply(console, [message])
     }
   }

  module.hot.accept('./containers/Root', () => {

    render(
      <AppContainer errorReporter={Redbox}>
        <Root store={store} history={history} />
      </AppContainer>,
      rootElement
    )
  })
}