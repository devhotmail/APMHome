// @flow
import React from 'react'
import ReactDOM from 'react-dom'
import dva from 'dva/mobile'
import { AppContainer } from 'react-hot-loader'
import './app.css'

import router from '#/router'

import assets from '#/models/assets'
import briefs from '#/models/briefs'
import depts from '#/models/depts'
import filters from '#/models/filters'
import steps from '#/models/steps'


const app = dva()

app.model(assets)
app.model(briefs)
app.model(depts)
app.model(filters)
app.model(steps)

app.router(router)

function render(router) {
  const App = router ? app._getProvider(router) : app.start();
  ReactDOM.render((
    <AppContainer>
      <App />
    </AppContainer>
  ), document.getElementById('root'));
}

app._plugin.apply('onHmr')(render);

render()

if (module.hot) {
  // $FlowFixMe
  module.hot.accept('#/router', () => { render(router) })
}
