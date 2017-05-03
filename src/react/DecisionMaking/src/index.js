import React from 'react'
import ReactDOM from 'react-dom'
import { AppContainer } from 'react-hot-loader'
import dva from 'dva'
import { browserHistory, HashHistory, useRouterHistory } from 'dva/router'
import { createHistory } from 'history'

import './styles/index.scss'

import router from '#/router'
import financial from '#/models/financial'
import nodeList from '#/models/nodeList'
import config from '#/models/config'
import focus from '#/models/focus'

const historyEngine = useRouterHistory(createHistory)({
  basename: document.location.pathname.split('.xhtml')[0] + '.xhtml'
})

const app = dva({
  history: process.env.NODE_ENV === 'development' ? browserHistory : historyEngine
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
