// @flow

import React from 'react'
import PropTypes from 'prop-types'
import { ConnectedRouter } from 'react-router-redux'
import { Provider } from 'react-redux'
import { I18nextProvider } from 'react-i18next'
import i18n from 'js/i18n'
import routes from 'routes/routes'

const Root = ({ store, history }) => {

  let ComponentEl = (
    <Provider store={store}>
      <I18nextProvider i18n={i18n}>
        <ConnectedRouter history={history} children={routes} />
      </I18nextProvider>
    </Provider>
  )

/*
  if (process.env.NODE_ENV !== 'production') {
    const DevTools = require('./DevTools').default

    ComponentEl = (
      <Provider store={store}>
        <div>
          <Router history={history} routes={routes} />
          {!window.devToolsExtension ? <DevTools /> : null}
        </div>
      </Provider>
    )
  }*/

  return ComponentEl
}

Root.propTypes = {
  history: PropTypes.object.isRequired,
  store: PropTypes.object.isRequired
}

export default Root
