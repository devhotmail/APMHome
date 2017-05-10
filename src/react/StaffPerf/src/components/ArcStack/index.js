/* @flow */
import React, { PureComponent } from 'react'
import { arc } from 'd3-shape'
import uuid from 'uuid/v4'

import { getSum, getAngle } from '#/utils'

import ArcBar from '../ArcBar'

import styles from './styles.scss'

type PropsT = {
  innerRadius: number,
  startAngle: number,
  endAngle: number,
  stackes: Array<number>
}

const colorSet = [
  'rgb(106,180,166)',
  'rgb(123,190,178)',
  'rgb(135,203,190)',
  'rgb(154,201,192)'
]

let textPathId = 0

export default class ArcStack extends PureComponent<*, PropsT, *> {
  render() {
    const { stackes, innerRadius, ...restProps } = this.props
    return (
      <g>
        {
          stackes.map((n, i, arr) => {
            const innerR = innerRadius + getSum(arr.slice(0, i))
            const outerR = innerR + n
            return <ArcBar
              key={i}
              color={colorSet[i]}
              innerRadius={innerR}
              outerRadius={outerR}
              {...restProps} />
          })
        }
        { this.renderText('hello world') }
      </g>
    )
  }

  renderText = (text) => {
    const { stackes, innerRadius, startAngle, endAngle } = this.props

    const textR = innerRadius + 10
    const textAngle = (startAngle + endAngle) / 2
    const textX = textR * Math.sin(textAngle)
    const textY = -textR * Math.cos(textAngle)

    const angle = getAngle(textAngle)
    const direction = angle > 0 && angle < 180
    const textRotate = angle + (direction ? -90 : 90)

    return <text
      className={`${styles.text} ${direction ? styles.anchorStart : styles.anchorEnd}`}
      x={textX}
      y={textY}
      dy="0.35em"
      transform={`rotate(${textRotate} ${textX} ${textY})`}>
      {text}
    </text>
  }

  renderText1 = () => {
    const { innerRadius, startAngle, endAngle } = this.props

    const pathId = uuid()

    const middleAngle = (startAngle + endAngle) / 2

    const bool = getAngle(middleAngle) < 90

    const arcAngleProps = {
      startAngle: bool ? startAngle : endAngle,
      endAngle: bool ? endAngle : startAngle
    }

    const dy = bool ? 15 : -7

    return <g>
      <ArcBar
        id={`group-${pathId}`}
        innerRadius={innerRadius}
        outerRadius={innerRadius}
        {...arcAngleProps} />
        <text dy={dy}>
          <textPath xlinkHref={`#group-${pathId}`}>{"hello world".substring(0, 5)}</textPath>
        </text>
    </g>
  }
}
