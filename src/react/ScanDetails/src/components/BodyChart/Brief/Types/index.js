// @flow
import React from 'react'
import { connect } from 'dva'
import type { IndexedIterable, KeyedIterable, Map, List } from 'immutable'
import * as Immutable from 'immutable'
import AnnulusRing from '#/components/AnnulusRing'
import ImmutableComponent from '#/components/ImmutableComponent'
import AnnulusSector from '#/components/AnnulusSector'
import { getRange, valueToCoordinate } from '#/utils'
import { COLORS } from '#/constants'

type TypeProps = {
  brief: Map<string, any>,
  minRadius: number,
  maxRadius: number,
  minCount: number,
  maxCount: number,
  startAngle: number,
  endAngle: number,
  partColors: KeyedIterable<string|number, string>,
  filters: List<*>,
}

// @connect()
// class Type extends ImmutableComponent<void, TypeProps, void> {
//   // $FlowFixMe
//   onClick = e => this.props.dispatch({
//     type: 'scans/filters/toggle',
//     payload: {
//       key: 'type',
//       value: this.props.brief.getIn(['type', 'id'])
//     }
//   })
//
//   countToWidth(count, minCount, maxCount, minRadius, maxRadius) {
//     return (count) / (maxCount - minCount) * (maxRadius - minRadius)
//   }
//
//   getOpacity(filters, brief) {
//     if (filters.size === 0) return 1
//     if (filters.find(filter => filter.get('key') === 'type' && filter.get('value') === brief.getIn(['type', 'id']))) return 1
//     return 0.3
//   }
//
//   render() {
//     const { brief, minRadius, maxRadius, minCount, maxCount, startAngle, endAngle, partColors, filters } = this.props
//     const annulusSectors = brief.getIn(['items', 'data'], []).reduce((prev, cur) => {
//       const width = this.countToWidth(cur.get('count'), minCount, maxCount, minRadius, maxRadius)
//       const prevRadius = prev.size ? prev.get(-1).props.outerRadius : minRadius
//       return prev.concat(
//         <AnnulusSector
//           key={cur.get('id')}
//           innerRadius={prevRadius}
//           outerRadius={prevRadius + width}
//           startAngle={startAngle}
//           endAngle={endAngle}
//           color={partColors.get(cur.get('id'))}
//         />
//       )
//     }, Immutable.List())
//
//     const textR = annulusSectors.get(0).props.innerRadius + 10
//     const textAngle = (startAngle + endAngle) / 2
//     const textX = textR * Math.sin(textAngle)
//     const textY = -textR * Math.cos(textAngle)
//     const textRotate = textAngle * 180 / Math.PI + 90
//     return (
//       <g onClick={this.onClick} opacity={this.getOpacity(filters, brief)}>
//         {annulusSectors}
//         <text
//           textAnchor="end"
//           fill="white"
//           x={textX}
//           y={textY}
//           dy="0.35em"
//           transform={`rotate(${textRotate} ${textX} ${textY})`}
//         >
//           {brief.getIn(['type', 'name'])}
//         </text>
//       </g>
//     )
//   }
// }

type Props = {
  cx: number,
  cy: number,
  maxRadius: number,
  minRadius: number,
  briefs: List<*>,
  partColors: KeyedIterable<string|number, string>,
  filters: List<*>
}

export default
@connect()
class Types extends ImmutableComponent<void, Props, void> {
  // static anglePaddingScale = 0.15
  // getStartAndEndAngle(index: number, count: number) {
  //   const theta = Math.PI / count
  //   return {
  //     startAngle: -(index + Types.anglePaddingScale) * theta,
  //     endAngle: -(index + 1 - Types.anglePaddingScale) * theta
  //   }
  // }

  render() {
    const { cx, cy, maxRadius, minRadius, briefs, partColors, filters } = this.props
    const countSums = briefs
      .map(brief => brief
        .getIn(['items', 'data'])
        .reduce((prev, cur) => prev + cur.get('count'), 0)
      )
    const [ minCount, maxCount ] = getRange(countSums)

    const groups = briefs.map(brief => Immutable.fromJS({
      id: brief.getIn(['type', 'id']),
      text: brief.getIn(['type', 'name']),
      opacity: filters.find(filter => filter.get('key') === 'type') ? filters.find(filter => filter.get('key') === 'type' && filter.get('value') === brief.getIn(['type', 'id'])) ? 1 : 0.4 : 1,
      annuluses: brief.getIn(['items', 'data']).map(datum => Immutable.fromJS({
        width: valueToCoordinate(datum.get('count'), [minCount, maxCount], [minRadius, maxRadius]),
        color: COLORS[datum.get('id')]
      }))
    }))

    return (
      <AnnulusRing
        cx={cx}
        cy={cy}
        innerRadius={minRadius}
        startAngle={-15 / 180 * Math.PI}
        endAngle={-165 / 180 * Math.PI}
        groups={groups}
        onGroupClick={group => this.props.dispatch({
          type: 'scans/filters/toggle',
          payload: {
            key: 'type',
            value: group.get('id')
          }
        })}
      />
    )

    // return (
    //   <g transform={`translate(${cx}, ${cy})`}>
    //     {briefs.map((brief, index) => (
    //       <Type
    //         key={brief.getIn(['type', 'id'])}
    //         brief={brief}
    //         minRadius={minRadius}
    //         maxRadius={maxRadius}
    //         minCount={minCount}
    //         maxCount={maxCount}
    //         {...this.getStartAndEndAngle(index, briefs.size)}
    //         partColors={partColors}
    //         filters={filters}
    //       />
    //     ))}
    //   </g>
    // )
  }
}
