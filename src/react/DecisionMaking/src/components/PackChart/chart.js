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
    const { height, width, diameter, nodeList, view, focus } = this.props

    if (!width || !height) return null

    const [ x, y, r ] = view
    // const { x, y, r } = focus
    const viewBox = [
      x - r,
      y - r,
      r * 2,
      r * 2
    ]

    console.log(view)

    return (
      <div>
        <svg width={width} height={height} viewBox={viewBox.join(' ')}>
          {/*{ nodeList ? this.renderNodes(nodeList) : null}*/}
          <TreeNodes {...this.props} />
        </svg>
      </div>
    )
  }

  changeFocus = node => {
    this.setState({

    })
  }
}
