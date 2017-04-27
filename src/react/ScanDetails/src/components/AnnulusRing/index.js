// @flow
import React from 'react'
import * as Immutable from 'immutable'
import { range } from 'lodash'
import type { Map, IndexedIterable, List } from 'immutable'
import { path } from 'd3'
import uuid from 'uuid/v4'
import AnnulusSectorGroup from '#/components/AnnulusSectorGroup'
import ImmutableComponent from '#/components/ImmutableComponent'

type Props = {
  cx: number,
  cy: number,
  innerRadius: number,
  startAngle: number,
  endAngle: number,
  groups: List<*>
}

type Position = {
  startAngle: number,
  endAngle: number
}

const MAX_GROUP_ANGLE = 15 * Math.PI / 180

function distribute(startAngle: number, endAngle: number, count: number): Position[]  {
  const span = endAngle - startAngle
  if (Math.abs(span) / count <= MAX_GROUP_ANGLE) {
    return range(count).map((_, index) => ({
      startAngle: startAngle + index * span / count,
      endAngle: startAngle + (index + 1) * span / count
    }))
  } else {
    const sign = span > 0 ? 1 : -1
    const padding = (Math.abs(span) - count * MAX_GROUP_ANGLE) / (count + 1)
    return range(count).map((_, index) => ({
      startAngle: startAngle + ((index + 1) * padding + index * MAX_GROUP_ANGLE) * sign,
      endAngle: startAngle + (index + 1) * (padding + MAX_GROUP_ANGLE) * sign
    }))
  }
}

export default
class AnnulusRing extends ImmutableComponent<*, Props, *> {
  render() {
    const { cx, cy, innerRadius, startAngle, endAngle, groups } = this.props
    const angles = distribute(startAngle, endAngle, groups.size)
    const id = uuid()
    // console.log(path().arc(0, 0, 10000, startAngle, endAngle));
    console.log(startAngle, endAngle);
    const sign = endAngle - startAngle > 0 ? 1 : 0
    const d = `M0,0L${500 * Math.sin(startAngle)},${-500 * Math.cos(startAngle)}A500,500,0,${sign},${sign},${500 * Math.sin(endAngle)},${-500 * Math.cos(endAngle)}`

    return (
      <g transform={`translate(${cx}, ${cy})`}>
        <defs>
          <clipPath id={id}>
            <path d={d} />
          </clipPath>
        </defs>
        <g clipPath={`url(#${id})`}>
          {groups.map((group, index) => (
            <AnnulusSectorGroup
              key={group.get('id', index)}
              innerRadius={innerRadius}
              text={group.get('text')}
              {...angles[index]}
              annuluses={group.get('annuluses')}
            />
          ))}
        </g>
      </g>
    )
  }
}
