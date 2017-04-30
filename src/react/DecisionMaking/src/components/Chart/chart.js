import React, { Component } from 'react'
import { Button } from 'antd'

import TextNodes from './TextNodes'
import TreeNodes from './TreeNodes'

import styles from './styles.scss'

type ChartProps = {
  width: number,
  height: number,
  diameter: number,
  setFocus: Function
}

export default class Chart extends Component<*, ChartProps, *> {
  render() {
    const { height, width, nodeList } = this.props

    if (!width || !height) return null

    return (
      <div>
        <div>
          <Button onClick={this.props.handleBackUpper}>返回上一层</Button>
          <Button onClick={this.props.handleBackRoot}>返回顶层</Button>
        </div>
        <svg width={width} height={height}>
          <g transform={`translate(${width/2},${height/2})`}>
            <TreeNodes {...this.props} />
            <TextNodes {...this.props} />
          </g>
        </svg>
      </div>
    )
  }
}
