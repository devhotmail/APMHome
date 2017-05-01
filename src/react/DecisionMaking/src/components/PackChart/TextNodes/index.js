import React, { Component } from 'react'

import styles from './styles.scss'

function TextDesc (props) {
  const { label, percent, overload } = props
  return <g>
    <text fontSize="1em">{label}</text>
    <text y="1.1em" fontSize="0.8em">{percent}</text>
    {
      overload
        ? <text y="2.1em" fontSize="0.8em">{overload}</text>
        : null
    }
  </g>
}

const labelKey = 'name'
const percentKey = 'usage_predict'

const constants = {
  maxFontSize: 20,
  maxTextLen: 10,
  circleColor: 'rgba(255, 255, 255, 0)'
}

const textColor = 'rgb(111, 111, 111)' // Text color
const textTopPercent = 0.85 // Percentage of height to show text top in the asset circle

export default class TextNodes extends Component {
  render () {
    const { nodeList } = this.props
    return (
      <g>
        { nodeList.length ? this.renderTexts(nodeList) : null }
      </g>
    )
  }

  // <text dy="0.3em">{node.data.name.substring(0, node.r / 3)}</text>

  renderTexts = (nodeList: Array<NodeT>) => {
    const { diameter, view, focus } = this.props
    const k = diameter / view[2]

    return <g className={styles.texts}>
      {
        nodeList.map((node, index) => {
          const translate = 'translate(' + (node.x - view[0]) * k + ',' + (node.y - view[1]) * k + ')'
          const scale = `scale(${k})`

          const fontSize = this.getFontSize(node.r * k)

          const textProps = {
            label: node.data[labelKey],
            percent: node.data[percentKey],
            overload: Array.isArray(node.data.usage_sum) && node.data.usage_sum[1] ? node.data.usage_sum[1]: undefined
          }

          const Text = <TextDesc {...textProps} />
 
          return (
            <g transform={translate} fontSize={fontSize} key={`${node.data.id}-${index}`}>
              {this.renderTopText(Text, node)}
              {this.renderCenterText(Text, node)}
            </g>
          )
        })
      }   
    </g>
  }

  renderTopText = (content, node) => {
    const textTopStyle = {
      display: this.getDisplayTop(node)
    }

    return <g style={textTopStyle}>{content}</g>
  }

  renderCenterText = (content, node) => {
    const textCenterStyle = {
      display: this.getDisplayCenter(node)
    }

    return <g style={textCenterStyle}>{content}</g>
  }


  getDisplayTop = (node) => {
    return node === this.props.focus ? 'inline' : 'none'
  }

  getDisplayCenter = (node) => {
    const { focus } = this.props
    if (node.parent === focus) return 'inline'
    if (node === focus && !node.children) return 'inline'
    return 'none'
  }

  getFontSize = (radius: number): number => {
    const { maxFontSize, maxTextLen } = constants

    const val = radius / maxTextLen
    return val > maxFontSize ? maxFontSize :  val
  }
}
