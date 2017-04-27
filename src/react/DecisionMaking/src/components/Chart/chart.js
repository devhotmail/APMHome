import React, { Component } from 'react'
import { Button } from 'antd'

import * as d3 from 'd3'

import styles from './styles.scss'

const margin = 20
const labelKey = 'name'
const percentKey = 'usage_predict'

const fontSize = 20
const textLength = 10
const circleColor = 'rgba(255, 255, 255, 0)'

const waveColor = [
  'rgb(235, 220, 81)', // yellow
  'rgb(178, 178, 178)', // gray
  'rgb(277, 110, 21)' // orange
]

const textColor = 'rgb(111, 111, 111)' // Text color
const textTopPercent = 0.85 // Percentage of height to show text top in the asset circle

type ChartProps = {
  width: number,
  height: number,
  diameter: number,
  setFocus: Function
}

type NodeT = {
  depth: number,
  height: number,
  r: number,
  value: number,
  x: number,
  y: number,
  data: Object,
  parent?: NodeT
}

function getStokeColor (n: NodeT): number {
  n.data.usage_threshold = [0.3, 1]
  const { usage_predict, usage_threshold } = n.data

  let colorIndex = 1
  if (usage_predict > usage_threshold[1]) colorIndex =  2
  else if (usage_predict < usage_threshold[0]) colorIndex = 0
  
  return waveColor[colorIndex]
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
            { nodeList.length ? this.renderNodes(nodeList) : null }
          </g>
        </svg>
      </div>
    )
  }

  renderNodes = (nodeList: Array<NodeT>) => {
    const { diameter, view, focus } = this.props
    const k = diameter / view[2]

    return <g>
      {
        nodeList.map((node, index) => {
          const translate = 'translate(' + (node.x - view[0]) * k + ',' + (node.y - view[1]) * k + ')'
          const scale = `scale(${k})`

          const waveColor = getStokeColor(node)
          const circleProps = {
            fill: this.getCircleFill(node, focus),
            strokeWidth: 1,
            stroke: waveColor,
            onClick: e => this.props.setFocus(node)
          }

          const groupStyle = {
            opacity: this.getOpacity(node, focus)
          }

          return (
            <g transform={translate} style={groupStyle} key={`${node.data.id}-${index}`}>
              <circle r={node.r * k} {...circleProps} />
              <g transform={scale}>
                {
                  node.data[percentKey] >= 1
                    ? <circle pointerEvents="none" r={node.r} fill={waveColor} />
                    : <path
                        pointerEvents="none"
                        fill={waveColor}
                        transform={`translate(${-node.r}, 0)`}
                        d={this.getWavePath(node.r, node.data[percentKey])} />
                }
              </g>
            </g>
          )
        })
      }   
    </g>
  }

  getCircleFill = (node, focus) => {
    if (node === focus) return circleColor
    // focus children
    if (node.parent && node.parent === focus) return circleColor
    // focus siblings
    if (node.children && ~node.children.indexOf(focus)) return circleColor
    // focus parent
    if (node.parent && node.parent.children && ~node.parent.children.indexOf(focus)) return circleColor
    return 'none'
  }

  getOpacity = (node, focus) => {
    if (node === focus) {
      if (node.children) return 0.6
      return 0.9
    } else {
      if (node.parent === focus) return 0.9
      return 0.1
    }
  }

  getWavePath = (radius: number, percent: number): string => {
    if (percent > 1) {
      return 'M' + radius + ' 0'
        + 'm'+ (-radius) + ', 0'
        + 'a' + radius + ',' + radius + ' 0 1,0 ' + (radius * 2) + ',0'
        + 'a' + radius + ',' + radius + ' 0 1,0 ' + (radius * -2) + ',0'
    } else {
      var part = 1 - percent * 2
      var h = part * radius
      var w = Math.sqrt(Math.pow(radius, 2) - Math.pow(h, 2))

      var defaultWaveHeight = 0.1 // The wave height as a percentage of wave circle's radius

      var waveHeight = defaultWaveHeight
      if (percent + defaultWaveHeight > 1) {
        var waveScaleY = d3.scaleLinear().range([0.035, 0.03]).domain([1 - defaultWaveHeight, 1])
        waveHeight = waveScaleY(percent)
      }
      if (percent < defaultWaveHeight) {
        var waveScaleY = d3.scaleLinear().range([0.035, 0.03]).domain([defaultWaveHeight, 0])
        waveHeight = waveScaleY(percent)
      }

      if (percent > 0.99 || percent < 0.01) {
        return 'M' + (radius - w) + ' ' + h
        + 'A' + radius + ',' + radius + ' 1' + (percent > 0.5 ? ' 1,0 ' : ' 0,0 ') + (radius + w) + ',' + h
        + 'z'
      } else {
        return 'M' + (radius - w) + ' ' + h
        + 'A' + radius + ',' + radius + ' 1' + (percent > 0.5 ? ' 1,0 ' : ' 0,0 ') + (radius + w) + ',' + h
        + 'q' + -w / 2 + ' ' + radius * waveHeight + ' ' + -w + ' 0'
        + 't' + -w + ' ' +  0
        + 'z'
      }
    }
  }
}
