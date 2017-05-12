// @flow
import React from 'react'
import ReactDOM from 'react-dom'
import dva from 'dva'
import { useRouterHistory } from 'dva/router'
import createHashHistory from 'history/lib/createHashHistory'
import { AppContainer } from 'react-hot-loader'
import './app.css'

import router from '#/router'

import profit from '#/models/profit'
import config from '#/models/config'
import user from '#/models/user'
import filters from '#/models/filters'
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


app.model(profit)
app.model(config)
app.model(user)
app.model(filters)
app.model(overview)

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

window.store = app._store

if (module.hot) {
  // $FlowFixMe
  module.hot.accept('#/router', () => { render(router) })
}
