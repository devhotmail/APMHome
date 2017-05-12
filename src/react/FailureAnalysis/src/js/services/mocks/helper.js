// @flow
import axios from 'axios'
import MockAdapter from 'axios-mock-adapter'

export function load(routeFile: string) {
  let mock = new MockAdapter(axios, { delayResponse: 500 })
  
  require(`${routeFile}`).default(mock) // use string template to suppress warning

}