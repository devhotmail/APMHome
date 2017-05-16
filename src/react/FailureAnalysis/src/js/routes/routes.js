import React from 'react'
import { Route, Switch } from 'react-router-dom'

import App from 'containers/App'
import Demo from 'containers/Demo'

export default (
  <Switch>
    {/*<Route path="/" component={App}/>*/}
    <Route path="/demo" component={Demo}/>
  </Switch>
)