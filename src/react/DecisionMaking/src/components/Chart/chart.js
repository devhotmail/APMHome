import React, { Component } from 'react'
import axios from 'axios'

import * as d3 from 'd3'

import styles from './styles.scss'

const margin = 20

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
        <svg width={width} height={height}>
          <g transform={`translate(${width/2},${height/2})`}>
            { nodeList.length ? this.renderNodes(nodeList) : null }
          </g>
        </svg>
      </div>
    )
  }

  renderNodes = nodeList => {
    const { diameter, view } = this.props
    const k = diameter / view[2]

    return <g>
      {
        nodeList.map((node, index) => {

          const translate = 'translate(' + (node.x - view[0]) * k + ',' + (node.y - view[1]) * k + ')'
          const scale = `scale(${k})`

          const circleProps = {
            fill: '#fff',
            strokeWidth: 1,
            stroke: '#000',
            onClick: e => this.props.setFocus(node)
          }

          return (
            <g transform={translate} key={`${node.data.id}-${index}`}>
              <g transform={scale}>
                <circle r={node.r} {...circleProps} />
                {
                  node.data.usage >= 1
                    ? <circle r={node.r} fill="rgb(178, 178, 178)" />
                    : <path
                        pointerEvents="none"
                        fill="rgb(178, 178, 178)"
                        transform={`translate(${-node.r}, 0)`}
                        d={this.getWavePath(node.r, node.data.usage)} />
                }
              </g>
            </g>
          )
        })
      }   
    </g>
  }

  getWavePath = (radius, percent) => {
    var part = 1 - percent * 2
    var h = part * radius 
    var w = Math.sqrt(Math.pow(radius, 2) - Math.pow(h, 2))

    var defaultWaveHeight = 0.1 // The wave height as a percentage of wave circle's radius
    var waveScaleY = d3.scaleLinear().range([0.035, 0.03]).domain([1 - defaultWaveHeight, 1])
    var waveHeight = (percent + defaultWaveHeight > 1) ? waveScaleY(percent) : defaultWaveHeight

    return 'M' + (radius - w) + ' ' + h
    + 'A' + radius + ',' + radius + ' 1' + (percent > 0.5 ? ' 1,0 ' : ' 0,0 ') + (radius + w) + ',' + h
    + 'q' + -w / 2 + ' ' + radius * waveHeight + ' ' + -w + ' 0'
    + 't' + -w + ' ' +  0
    + 'z'
  }
}
