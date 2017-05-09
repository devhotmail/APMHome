/* @flow */
import React, { PureComponent } from 'react'
import { arc } from 'd3-shape'

import AnnulusSector from '../AnnulusSector'

type PropsT = {
  innerRadius: number,
  outerRadius?: number,
  startAngle: number,
  endAngle: number,
  width?: number,
  color?: string
}

const colorSet = [
  'rgb(106,180,166)',
  'rgb(123,190,178)',
  'rgb(135,203,190)',
  'rgb(154,201,192)'
]

export default class ArcStack extends PureComponent<*, PropsT, *> {
  render() {
    const { stackes, innerRadius, outerRadius, ...restProps } = this.props
    const spaces = outerRadius - innerRadius
    return (
      <g>
        {
          stackes.map((n, i, arr) => {
            const a = innerRadius + spaces * (i > 0 ? arr[i - 1]: 0)
            const b = innerRadius + spaces * n
            return <AnnulusSector
              key={i}
              color={colorSet[i]}
              innerRadius={a}
              outerRadius={b}
              {...restProps} />
          })
        }
      </g>
    )
  }
}
