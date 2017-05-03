import React from 'react'
import ReactDOM from 'react-dom'
import dva from 'dva/mobile'
import { AppContainer } from 'react-hot-loader'

import './styles/index.scss'

import router from '#/router'
import financial from '#/models/financial'
import nodeList from '#/models/nodeList'
import config from '#/models/config'
import focus from '#/models/focus'

const app = dva()

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
