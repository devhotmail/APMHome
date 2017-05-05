// @flow
import React from 'react'
import type { List } from 'immutable'
import * as Immutable from 'immutable'
import ImmutableComponent from '#/components/ImmutableComponent'
import type { ClientRect } from '#/HOC/withClientRect'
import Types from './Types'
import Parts from './Parts'

const COLORS = [
  '#6ab4a6',
  '#d5c165',
  '#ba82b7',
  '#83b2d1',
  '#99ba93',
  '#ba9cbb'
]

type Props = {
  filters: List<*>,
  briefs: List<*>,
  limit: number,
  clientRect: ClientRect
}

export default
class Brief extends ImmutableComponent<*, Props, *> {

  render() {
    const { parts, briefs, clientRect, limit, filters } = this.props
    const { width, height } = clientRect
    const cx = width / 2
    const cy = height / 2
    const maxRadius = Math.min.call(null, height * 0.7 / 2, width * 0.6 / 2)

    // const parts = briefs
    //   .flatMap(brief => brief.getIn(['items', 'desc']))
    //   .reduce((prev, cur) => prev.set(cur.get('id'), cur.get('name')), Immutable.Map())

    const partColors = parts.mapEntries(([k, v]) => [k, v.get('color')])

    const partScans = briefs
      .filter(brief => {
        const typeFilter = filters.find(filter => filter.get('key') === 'type')
        if (!typeFilter) return true
        return brief.getIn(['type', 'id']) === typeFilter.get('value')
      })
      .flatMap(brief => brief.getIn(['items', 'data']))
      .reduce((prev, cur) => {
        const count = prev.get(cur.get('id'), 0)
        return prev.set(cur.get('id'), count + cur.get('count'))
      }, Immutable.Map())

    return (
      <g>
        <Types
          cx={cx - 0.1 * width}
          cy={cy}
          maxRadius={maxRadius}
          minRadius={maxRadius * 0.8}
          briefs={briefs}
          parts={parts}
          filters={filters}
        />
        <Parts
          cx={cx - 0.1 * width}
          cy={cy}
          maxRadius={maxRadius * 0.65}
          minRadius={maxRadius * 0.55}
          parts={parts}
          partScans={partScans}
          filters={filters}
        />
      </g>
    )
  }
}
