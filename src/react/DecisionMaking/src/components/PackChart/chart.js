import React, { Component } from 'react'
import { Button } from 'antd'

import { margin } from '#/constants'

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
    const { height, width, view } = this.props

    if (!width || !height || !view.length) return null

    const [ x, y, r ] = view

    const viewBox = [
      x - r - margin / 2,
      y - r - margin / 2,
      r * 2 + margin,
      r * 2 + margin
    ]

    return (
      <div className={styles.chart}>
        <div className={styles.btns}>
          <Button onClick={this.props.handleBackUpper}>返回上一层</Button>
          <Button onClick={this.props.handleBackRoot}>返回顶层</Button>
        </div>        
        <svg width={width} height={height} viewBox={viewBox.join(' ')}>
          <TreeNodes {...this.props} />
          {/*<TextNodes {...this.props} />*/}
          {/*{ nodeList ? this.renderTexts(nodeList) : null }*/}
        </svg>
      </div>
    )
  }

  renderTexts = (nodeList: Array<NodeT>) => {
    const { focus } = this.props
    return <g>
      {
        nodeList.map((node, index) => {
          return (
            <g
              key={`text-${index}`}
              transform={`translate(${node.x}, ${node.y})`}>
              <text dy="0.3em">{node.data.name.substring(0, 5)}</text>             
            </g>
          )
        })
      }
    </g>
  }
}
