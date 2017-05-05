import React from 'react'
import ReactDOM from 'react-dom'
import dva from 'dva'
import { browserHistory, HashHistory, useRouterHistory } from 'dva/router'
import createHashHistory from 'history/lib/createHashHistory'

import './polyfill'

import router from '#/router'
import models from '#/models'

import './styles/index.scss'

const historyEngine = useRouterHistory(createHashHistory)({
  queryKey: false,
  basename: '/'
})

const app = dva({
  history: historyEngine
})

models.forEach(model => app.model(model))

app.router(router)

app.start('#root')
