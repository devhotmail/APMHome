import React from 'react'
import { Router, Route } from 'dva/router'
import AssetPerf from '#/containers/AssetPerf'

export default (({ history }) =>
  <Router history={history}>
    <Route path="/" component={AssetPerf} />
  </Router>
)
