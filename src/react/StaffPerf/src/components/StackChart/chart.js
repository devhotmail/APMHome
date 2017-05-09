/* @flow */
import React, { PureComponent } from 'react'
import { scaleLinear } from 'd3-scale'

import { margin, stackHeight, pageSize } from '#/constants'
import { getRadian } from '#/utils'

import ArcStack from '../ArcStack'

const averageSpace = 360 / pageSize

const barAngle = averageSpace * 0.75

const mockData = Array(15).fill([0.2, 0.2, 0.2, 0.2])

const scaleFn = (length: number) => {
  if (length === 1) {
    return () => 0
  } else if (length < 8) {
    const space = length / 2 * averageSpace
    return scaleLinear().range([-space, space]).domain([0, length - 1])
  } else {
    return scaleLinear().range([-90, 270]).domain([0, length])
  }
}

type PropsT = {
  width: number,
  height: number,
  diameter: number
}

export default class Chart extends PureComponent<*, PropsT, *> {
  render () {
    const {
      height, width, diameter
    } = this.props

    if (!diameter) return null

    const radius = (diameter - margin) / 2

    const getAngle = scaleFn(mockData.length)
    return (
      <div>
        <svg width={width} height={height}>
          <g transform={`translate(${width / 2}, ${height / 2})`}>
            {
              mockData.map((n, i) => {
                const middleAngle = getAngle(i)
                return <ArcStack
                  key={i}
                  innerRadius={radius - stackHeight}
                  startAngle={getRadian(middleAngle - barAngle / 2)}
                  endAngle={getRadian(middleAngle + barAngle / 2)}
                  stackes={n.map(a => a * stackHeight)} />
              })
            }
          </g>
        </svg>
      </div>
    )
  }
}
