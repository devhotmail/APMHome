/* @flow */
import React from 'react'
import { Button } from 'antd'

import webpackLogo from '#/assets/webpack.svg'
import Chart from '#/components/chart'

import styles from './styles.scss'
import * as utils from '#/utils'

import './app.css'

export default class Main extends React.Component {
  render () {
    const myName = utils.rename({
      name: 'GE apm',
      id: 1
    })

    return <div>
      <Button type="primary">Primary</Button>
      <h2 className={styles.hello}>Hello World {myName}</h2>
      <img width="100" src={webpackLogo} />
      <Chart />
      Current API_HOST is <b>{process.env.API_HOST}</b>
    </div>
  }
}
