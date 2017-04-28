// @flow
import React from 'react'
import { connect } from 'dva'
import type { IndexedIterable, KeyedIterable, IndexedSeq, Map, List } from 'immutable'
import * as Immutable from 'immutable'
import ImmutableComponent from '#/components/ImmutableComponent'
import AnnulusSector from '#/components/AnnulusSector'
import AnnulusRing from '#/components/AnnulusRing'
import { getRange, valueToCoordinate } from '#/utils'
import { COLORS } from '#/constants'

// type PartProps = {
//   id: string|number,
//   count: number,
//   minRadius: number,
//   maxRadius: number,
//   minCount: number,
//   maxCount: number,
//   startAngle: number,
//   endAngle: number,
//   partColors: KeyedIterable<string|number, string>,
//   parts: KeyedIterable<string|number, string>,
// }

// class Part extends ImmutableComponent<void, PartProps, void> {
//   countToWidth(count, minCount, maxCount, minRadius, maxRadius) {
//     return (count) / (maxCount - minCount) * (maxRadius - minRadius)
//   }
//
//   render() {
//     const { id, count, minRadius, maxRadius, minCount, maxCount, startAngle, endAngle, partColors, parts } = this.props
//
//     const width = this.countToWidth(count, minCount, maxCount, minRadius, maxRadius)
//
//     const textR = minRadius + width / 2
//     const textAngle = (startAngle + endAngle) / 2
//     const textX = textR * Math.sin(textAngle)
//     const textY = -textR * Math.cos(textAngle)
//     const textRotate = textAngle * 180 / Math.PI + 90
//
//     return (
//       <g>
//         <AnnulusSector
//           innerRadius={minRadius}
//           outerRadius={minRadius + width}
//           startAngle={startAngle}
//           endAngle={endAngle}
//           color={partColors.get(id)}
//         />
//         <text
//           textAnchor="middle"
//           fill="white"
//           x={textX}
//           y={textY}
//           dy="0.35em"
//           transform={`rotate(${textRotate} ${textX} ${textY})`}
//         >
//           {parts.get(id)}
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
  parts: Map<string|number, *>,
  partScans: Map<string|number, number>,
  partColors: KeyedIterable<string|number, string>,
  filters: List<*>
}


export default
@connect()
class Parts extends ImmutableComponent<void, Props, void> {
  static anglePaddingScale = 0.1
  getStartAndEndAngle(index: number, count: number) {
    const theta = Math.PI / count
    return {
      startAngle: -(index + Parts.anglePaddingScale) * theta,
      endAngle: -(index + 1 - Parts.anglePaddingScale) * theta
    }
  }

  render() {
    const { cx, cy, maxRadius, minRadius, parts, partColors, partScans, filters } = this.props
    const countSums = partScans.valueSeq()
    const [ minCount, maxCount ] = getRange(countSums)
    const groups = partScans.entrySeq().map(([key, count]) => Immutable.fromJS({
      id: key,
      text: parts.get(key),
      opacity: filters.find(filter => filter.get('key') === 'part') ? filters.find(filter => filter.get('key') === 'part' && filter.get('value') === key) ? 1 : 0.6 : 1,
      annuluses: [{
        width: valueToCoordinate(count, [minCount, maxCount], [minRadius, maxRadius]),
        color: COLORS[key]
      }]
    }))

    return (
      <AnnulusRing
        cx={cx}
        cy={cy}
        innerRadius={minRadius}
        startAngle={-10 / 180 * Math.PI}
        endAngle={-170 / 180 * Math.PI}
        groups={groups}
        onGroupClick={group => this.props.dispatch({
          type: 'scans/filters/toggle',
          payload: {
            key: 'part',
            value: group.get('id')
          }
        })}
      />
    )

    // return (
    //   <g transform={`translate(${cx}, ${cy})`}>
    //     {partScans.entrySeq().map(([key, count], index, seq) => (
    //       <Part
    //         key={key}
    //         id={key}
    //         count={count}
    //         minRadius={minRadius}
    //         maxRadius={maxRadius}
    //         minCount={minCount}
    //         maxCount={maxCount}
    //         {...this.getStartAndEndAngle(index, seq.size || 0)}
    //         partColors={partColors}
    //         parts={parts}
    //       />
    //     ))}
    //   </g>
    // )
  }
}
