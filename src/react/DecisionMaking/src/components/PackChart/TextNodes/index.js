/* @flow */
import React, { PureComponent } from 'react'

import type { NodeListT } from '#/types'

import { isFocusNode, isSameCursor } from '#/utils'

import TextDesc from './TextDesc'

import styles from './styles.scss'

const labelKey = 'name'
const percentKey = 'usage_predict'

const constants = {
  maxFontSize: 20,
  maxTextLen: 10,
  circleColor: 'rgba(255, 255, 255, 0)'
}

const textTopPercent = 0.85 // Percentage of height to show text top in the asset circle

class TextCenter extends PureComponent {
  render () {
    const { node } = this.props

    const textProps = {
      label: node.data[labelKey],
      percent: node.data[percentKey],
      overload: Array.isArray(node.data.usage_sum) && node.data.usage_sum[1]
        ? node.data.usage_sum[1]
        : undefined
    }

    return (
      <g
        className={styles.textCenter}
        transform={`translate(${node.x}, ${node.y})`}>
        <TextDesc {...textProps} />
      </g>
    )
  }
}

class TextTop extends PureComponent {
  render () {
    const { node } = this.props

    const textProps = {
      label: node.data[labelKey],
      percent: node.data[percentKey],
      overload: Array.isArray(node.data.usage_sum) && node.data.usage_sum[1]
        ? node.data.usage_sum[1]
        : undefined
    }

    return (
      <g
        className={styles.textCenter}
        transform={`translate(${node.x}, ${node.y - textTopPercent * node.r})`}>
        <TextDesc {...textProps} />
      </g>
    )
  }
}

export default class TextNodes extends PureComponent {
  render () {
    const { nodeList, cursor } = this.props

    const topTextNode = nodeList.find(n => {
      return isFocusNode(n, cursor)
    })
    // nodeList filter to find centerTextNodes
    const centerTextNodes = nodeList.filter(n => {
      return (n.parent && isFocusNode(n.parent, cursor))
    })

    const TopTextComp = centerTextNodes.length !== 0 ? TextTop : TextCenter

    return (
      <g>
        <g className={styles.texts}>
          <TopTextComp node={topTextNode} />
          {
            centerTextNodes.map((node, index) => <TextCenter key={index} node={node} />)
          }
        </g>
      </g>
    )
  }
}
