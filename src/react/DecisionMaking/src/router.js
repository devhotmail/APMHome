import React from 'react'
import { Router, Route } from 'dva/router'

import Root from '#/containers/Root'

export default (({ history }) => 
  <Router history={history}>
    <Route path="/" component={Root} />
  </Router>
)
