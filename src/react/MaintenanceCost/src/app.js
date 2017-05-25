// @flow
import React from 'react'
import ReactDOM from 'react-dom'
import dva from 'dva'
import { useRouterHistory } from 'dva/router'
import createHashHistory from 'history/lib/createHashHistory'
import { AppContainer } from 'react-hot-loader'
import './app.css'

import router from '#/router'

import filters from '#/models/filters'
import depts from '#/models/depts'
import assetTypes from '#/models/assetTypes'
import groups from '#/models/groups'
import assets from '#/models/assets'
import overview from '#/models/overview'

const historyEngine = useRouterHistory(createHashHistory)({
  queryKey: false,
  basename: '/'
})
const app = dva({
  history: historyEngine,
  onError: e => {
    if (process.env.NODE_ENV === 'production') return
    console.log(e)
  }
})

app.router(router)
app.model(filters)
app.model(depts)
app.model(assetTypes)
app.model(groups)
app.model(assets)
app.model(overview)

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

window.store = app._store

if (module.hot) {
  // $FlowFixMe
  module.hot.accept('#/router', () => { render(router) })
}
