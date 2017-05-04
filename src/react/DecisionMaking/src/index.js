import React from 'react'
import ReactDOM from 'react-dom'
import { AppContainer } from 'react-hot-loader'
import dva from 'dva'
import { browserHistory, HashHistory, useRouterHistory } from 'dva/router'
import createHashHistory from 'history/lib/createHashHistory'

import './polyfill'

import router from '#/router'
import financial from '#/models/financial'
import nodeList from '#/models/nodeList'
import config from '#/models/config'
import focus from '#/models/focus'

import './styles/index.scss'

const historyEngine = useRouterHistory(createHashHistory)({
  queryKey: false,
  basename: '/'
})

const app = dva({
  history: historyEngine
})

app.model(financial)
app.model(nodeList)
app.model(config)
app.model(focus)

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
  module.hot.accept('#/router', () => { render(router) })
}
