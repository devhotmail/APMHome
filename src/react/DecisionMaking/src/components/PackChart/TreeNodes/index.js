/* @flow */
import React, { Component } from 'react'
import * as d3 from 'd3'

import type { cursorT, NodeT } from '#/types'
import { getCursor, isSameCursor } from '#/utils'

import styles from './styles.scss'

const percentKey = 'usage_predict'

const constants = {
  circleColor: 'rgba(255, 255, 255, 0)'
}

// todo: hard code field
function getCircleStrokeCls (n: NodeT): string {
  const circleStrokeCls = [
    styles.strokeYellow,
    styles.strokeGray,
    styles.strokeOrange
  ]

  const { usage_predict, usage_threshold = [0.3, 1] } = n.data

  let colorIndex = 1
  if (usage_predict > usage_threshold[1]) colorIndex =  2
  else if (usage_predict < usage_threshold[0]) colorIndex = 0
  
  return circleStrokeCls[colorIndex]
}

function getWaveFillCls (n: NodeT): string {
  const waveFillCls = [
    styles.fillYellow,
    styles.fillGray,
    styles.fillOrange
  ]

  const { usage_predict, usage_threshold = [0.3, 1] } = n.data

  let colorIndex = 1
  if (usage_predict > usage_threshold[1]) colorIndex =  2
  else if (usage_predict < usage_threshold[0]) colorIndex = 0
  
  return waveFillCls[colorIndex]
}

export default class TreeNodes extends Component<*, *, *> {  
  render () {
    const { nodeList, focusCursor } = this.props
    return (
      <g>
        { nodeList.length && focusCursor.length ? this.renderNodes(nodeList, focusCursor) : null }
      </g>
    )
  }

  renderNodes = (nodeList: Array<NodeT>, cursor: cursorT) => {
    return <g>
      {
        nodeList.map((node, index) => {
          const groupCls = [
            this.getOpacityCls(node, cursor),
            node.children ? 'node' : 'leaf node'
          ]

          const circleCls = [
            this.getCircleFillCls(node, cursor),
            getCircleStrokeCls(node)
          ]

          const waveCls = getWaveFillCls(node)

          const onClick = (e: Event) => {
            e.stopPropagation()
            this.props.setFocus(getCursor(node))
          }

          return (
            <g
              key={`tree-${index}`}
              className={groupCls.join(' ')}
              transform={`translate(${node.x}, ${node.y})`}>
              <circle className={circleCls.join(' ')} r={node.r} onClick={onClick} />
              <g className={styles.noPointerEvent}>
                {
                  node.data[percentKey] >= 1
                    ? <circle className={waveCls} r={node.r} />
                    : <path
                        className={waveCls}
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

  getCircleFillCls = (node: NodeT, cursor: cursorT): string => {
    // transparent fill to occupy own place
    const { transparentFill, noFill } = styles
 
    // focus self
    if (this.compareCursor(node, cursor)) return transparentFill
    // focus's parent
    if (node.parent && this.compareCursor(node.parent, cursor)) return transparentFill
    // focus's siblings
    if (
      node.parent
      && Array.isArray(node.parent.children)
      && node.parent.children.find(n => this.compareCursor(n, cursor))
    ) return transparentFill
    return noFill
  }

  getOpacityCls = (node: NodeT, cursor: cursorT): string => {
    if (this.compareCursor(node, cursor)) {
      if (node.children) return styles.opacityPointSix
      return styles.opacityPointNine
    } else {
      if (node.parent && this.compareCursor(node.parent, cursor)) return styles.opacityPointNine
      return styles.opacityPointOne
    }
  }
  
  compareCursor = (node: NodeT, cursor: cursorT): boolean => {
    return isSameCursor(getCursor(node), cursor)
  }

  findCursorTarget = (nodeList: Array<NodeT>, cursor: cursorT): NodeT => {
    const [ id, depth ] = cursor
    return nodeList.find(node => node.data.id === id && node.depth === depth)
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
