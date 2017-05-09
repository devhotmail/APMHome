// @flow
import React from 'react'
import ReactDOM from 'react-dom'
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

  onMouseEnter = (clientRect, data) => {
    const style = {
      pointerEvents: 'none',
      position: 'fixed',
      minWidth: 100,
      background: '#d8d8d8',
      border: '1px solid #5d87d4',
      borderRadius: 3,
      padding: '12px 7px',
      // transform: `translate(${clientRect.width + 10}px, ${clientRect.height / 2}px)`
    }
    if (clientRect.top > window.innerHeight * 0.6) {
      style.bottom = window.innerHeight - clientRect.bottom + clientRect.height / 2 + 'px'
    } else {
      style.top = clientRect.top + clientRect.height / 2 + 'px'
    }

    if (clientRect.left > window.innerWidth * 0.6) {
      style.right = window.innerWidth - clientRect.right + clientRect.width + 10 + 'px'
    } else {
      style.left = clientRect.left + clientRect.width + 10 + 'px'
    }
    this.el = this.el || ReactDOM.render(
      <div style={style}>
        <span>{data.getIn(['data', 'name'])}</span>
        <span>{data.getIn(['data', 'count'])}</span>
      </div>,
      document.createElement('div')
    )
    document.body.appendChild(this.el)
  }

  onMouseLeave = e => {
    document.body.removeChild(this.el)
    this.el = null
  }

  render() {
    const { cx, cy, maxRadius, minRadius, parts, partScans, filters } = this.props
    const countSums = partScans.valueSeq()
    const [ minCount, maxCount ] = getRange(countSums)
    const groups = partScans.entrySeq().map(([key, count]) => Immutable.fromJS({
      id: key,
      text: parts.getIn([key, 'name']),
      opacity: filters.find(filter => filter.get('key') === 'part') ? filters.find(filter => filter.get('key') === 'part' && filter.get('value') === key) ? 1 : 0.4 : 1,
      annuluses: [{
        width: valueToCoordinate(count, [minCount, maxCount], [minRadius, maxRadius]),
        color: parts.getIn([key, 'color'])
      }],
      data: {
        name: parts.getIn([key, 'name']),
        count
      }
    }))

    return (
      <AnnulusRing
        cx={cx}
        cy={cy}
        innerRadius={minRadius}
        outerRadius={maxRadius}
        startAngle={-10 / 180 * Math.PI}
        endAngle={-170 / 180 * Math.PI}
        groups={groups}
        onMouseEnter={this.onMouseEnter}
        onMouseLeave={this.onMouseLeave}
        onGroupClick={group => this.props.dispatch({
          type: 'scans/filters/toggle',
          payload: {
            key: 'part',
            value: group.get('id')
          }
        })}
      />
    )
  }
}
