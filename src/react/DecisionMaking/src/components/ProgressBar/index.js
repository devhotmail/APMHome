/* @flow */
import React, { Component } from 'react'
import { Progress } from 'antd'

type PropsT = {
  title: string
}

export default class ProgressBar extends Component<*, PropsT, *> {
  render () {
    const { title = '2016年5月' } = this.props
    return (
      <div>
        <div>{title}</div>
        <Progress percent={50} showInfo={false} />
      </div>
    )
  }
}
