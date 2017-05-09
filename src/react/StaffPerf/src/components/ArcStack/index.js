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
        { this.renderText() }
      </g>
    )
  }

  renderText2 = () => {
    const { stackes, innerRadius, startAngle, endAngle } = this.props

    const textR = innerRadius + 10
    const textAngle = (startAngle + endAngle) / 2
    const textX = textR * Math.sin(textAngle)
    const textY = -textR * Math.cos(textAngle)
    const textRotate = textAngle * 180 / Math.PI + (textAngle > 0 ? -1 : 1) * 90

    const pathId = uuid()
    return <g>
      <ArcBar
        id={`group-${pathId}`}
        innerRadius={innerRadius}
        outerRadius={innerRadius}
        startAngle={endAngle}
        endAngle={startAngle} />
        <text>
          <textPath xlinkHref={`#group-${pathId}`}>hello world</textPath>
        </text>
    </g>
  }

  renderText = () => {
    const { innerRadius, startAngle, endAngle } = this.props

    const pathId = uuid()

    const middleAngle = (startAngle + endAngle) / 2

    const bool = getAngle(middleAngle) < 90

    const arcAngleProps = {
      startAngle: bool ? startAngle : endAngle,
      endAngle: bool ? endAngle : startAngle
    }

    const dy = bool ? 15 : -15

    return <g>
      <ArcBar
        id={`group-${pathId}`}
        innerRadius={innerRadius}
        outerRadius={innerRadius}
        {...arcAngleProps} />
        <text dy={dy}>
          <textPath xlinkHref={`#group-${pathId}`}>hello world</textPath>
        </text>
    </g>
  }
}
