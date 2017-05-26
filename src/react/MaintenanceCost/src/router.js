import React from 'react'
import { Router, Route } from 'dva/router'
import MaintenanceCost from '#/containers/MaintenanceCost'

export default (({ history }) =>
  <Router history={history}>
    <Route path="/" component={MaintenanceCost} />
  </Router>
)
