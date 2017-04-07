import React from 'react'
import ReactDOM from 'react-dom'
import axios from 'axios'

import webpackLogo from '#/assets/webpack.svg'
import Chart from '#/components/chart'

import './app.css'

export default class Main extends React.Component {
  render() {
    return <div>
      <h2>Hello World</h2>
      <img width="100" src={webpackLogo} />
      <Chart />
    </div>
  }
}
