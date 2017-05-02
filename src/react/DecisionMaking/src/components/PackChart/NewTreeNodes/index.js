import React, { Component } from 'react'
import * as d3 from 'd3'

import styles from './styles.scss'

const margin = 20
const percentKey = 'usage_predict'

const constants = {
  circleColor: 'rgba(255, 255, 255, 0)'
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

// todo: hard code field
function getCircleStrokeCls (n: NodeT): number {
  const waveColorCls = [
    styles.strokeYellow,
    styles.strokeGray,
    styles.strokeOrange
  ]

  const { usage_predict, usage_threshold = [0.3, 1] } = n.data

  let colorIndex = 1
  if (usage_predict > usage_threshold[1]) colorIndex =  2
  else if (usage_predict < usage_threshold[0]) colorIndex = 0
  
  return waveColorCls[colorIndex]
}

export default class TreeNodes extends Component<*, *, *> {
  render () {
    const { nodeList } = this.props
    return (
      <g>
        { nodeList.length ? this.renderNodes(nodeList) : null }
      </g>
    )
  }

  renderNodes = (nodeList: Array<NodeT>) => {
    const { focus } = this.props
    return <g>
      {
        nodeList.map((node, index) => {
          const groupCls = [
            this.getOpacityCls(node, focus),
            node.children ? 'node' : 'leaf node'
          ]

          const strokeCls = getCircleStrokeCls(node)

          const circleCls = [
            this.getCircleFillCls(node, focus),
            strokeCls
          ]

          const onClick = e => this.props.setFocus(node)

          return (
            <g
              key={`tree-${index}`}
              className={groupCls.join(' ')}
              transform={`translate(${node.x}, ${node.y})`}>
              <circle className={circleCls.join(' ')} r={node.r} onClick={onClick} />
              <g className={styles.noPointerEvent}>
                {
                  node.data[percentKey] >= 1
                    ? <circle className={strokeCls} r={node.r} />
                    : <path
                        className={strokeCls}
                        transform={`translate(${-node.r}, 0)`} // todo: remove transform here
                        d={this.getWavePath(node.r, node.data[percentKey])} />
                }
              </g>              
            </g>
          )
        })
      }
    </g>
  }

  getCircleFillCls = (node, focus) => {
    // transparent fill to occupy own place
    const { transparentFill, noFill } = styles

    if (node === focus) return transparentFill
    // focus children
    if (node.parent && node.parent === focus) return transparentFill
    // focus siblings
    if (node.children && ~node.children.indexOf(focus)) return transparentFill
    // focus parent
    if (node.parent && node.parent.children && ~node.parent.children.indexOf(focus)) return transparentFill
    return noFill
  }

  getOpacityCls = (node, focus) => {
    if (node === focus) {
      if (node.children) return styles.opacityPointSix
      return styles.opacityPointNine
    } else {
      if (node.parent === focus) return styles.opacityPointNine
      return styles.opacityPointOne
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
