// @flow
import React from 'react'
import * as Immutable from 'immutable'
import ImmutableComponent from '#/components/ImmutableComponent'
import { arc } from 'd3'

type Props = {
  innerRadius: number,
  outerRadius?: number,
  width?: number,
  startAngle: number,
  endAngle: number,
  color?: string
}

export default
class AnnulusSector extends ImmutableComponent<void, Props, void> {
  render() {
    const { innerRadius, outerRadius, width, startAngle, endAngle, color } = this.props
    const d = arc()
      .innerRadius(innerRadius)
      .outerRadius(outerRadius || innerRadius + width)
      .startAngle(startAngle)
      .endAngle(endAngle)()

    return (
      <path strokeWidth={0} stroke="none" fill={color} d={d}/>
    )
  }
}
