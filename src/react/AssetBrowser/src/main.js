import React from 'react'
import ReactDOM from 'react-dom'
import axios from 'axios'

import Chart from '#/components/chart'

import './app.css'

export default class Main extends React.Component {
  render() {
    return <div>
      <Chart />
      <h2>Hello World</h2>
    </div>
  }
}
