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

const margin = 20

export default class Chart extends Component<*, ChartProps, *> {
  render() {
    const { height, width, diameter, nodeList, view, focus } = this.props

    if (!width || !height) return null

    const [ x, y, r ] = view
    // const { x, y, r } = focus
    const viewBox = [
      x - r - margin / 2,
      y - r - margin / 2,
      r * 2 + margin,
      r * 2 + margin
    ]

    // console.log(view)
    console.log(2)
    return (
      <div className={styles.chart}>
        <div className={styles.btns}>
          <Button onClick={this.props.handleBackUpper}>返回上一层</Button>
          <Button onClick={this.props.handleBackRoot}>返回顶层</Button>
        </div>        
        <svg width={width} height={height} viewBox={viewBox.join(' ')}>
          <TreeNodes {...this.props} />
          {/*<TextNodes {...this.props} />*/}
          {/*{ nodeList ? this.renderNodes(nodeList) : null }*/}
        </svg>
      </div>
    )
  }

  renderNodes = (nodeList: Array<NodeT>) => {
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

  changeFocus = node => {
    this.setState({

    })
  }
}
