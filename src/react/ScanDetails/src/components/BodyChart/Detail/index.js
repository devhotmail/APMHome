// @flow
import React from 'react'
import type { IndexedIterable, KeyedIterable, Map, List } from 'immutable'
import { Motion, spring } from 'react-motion'
import type { ClientRect } from '#/HOC/withClientRect'
import * as Immutable from 'immutable'
import AnnulusRing from '#/components/AnnulusRing'
import ImmutableComponent from '#/components/ImmutableComponent'
import AnnulusSector from '#/components/AnnulusSector'
import { valueToCoordinate, getRange } from '#/utils'
import { COLORS } from '#/constants'


// class Item extends ImmutableComponent<*, *, *> {
//   countToWidth(count, minCount, maxCount, minRadius, maxRadius) {
//     return (count) / (maxCount - minCount) * (maxRadius - minRadius)
//   }
//
//   render() {
//     const { detail, minRadius, maxRadius, minCount, maxCount, startAngle, endAngle, partColors } = this.props
//     const annulusSectors = detail.getIn(['items', 'data'], []).reduce((prev, cur) => {
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
//     const textR = annulusSectors.get(0).props.innerRadius + 5
//     const textAngle = (startAngle + endAngle) / 2
//     const textX = textR * Math.sin(textAngle)
//     const textY = -textR * Math.cos(textAngle)
//     const textRotate = textAngle * 180 / Math.PI - 90
//     return (
//       <g>
//         {annulusSectors}
//         <text
//           fill="white"
//           x={textX}
//           y={textY}
//           dy="0.35em"
//           transform={`rotate(${textRotate} ${textX} ${textY})`}
//         >
//           {detail.getIn(['asset', 'name'])}
//         </text>
//       </g>
//     )
//   }
// }

type Props = {
  filters: List<*>,
  details: List<*>,
  detailsCurPage?: number,
  detailsBySequence: List<*>,
  detailsBySequenceCurPage?: number,
  pageSize?: number,
  onDetailsPageChange?: number => void,
  onDetailsBySequencePageChange?: number => void,
  clientRect: ClientRect
}

// function getRange(arr: number[] | IndexedIterable<number>):[number, number] {
//   return arr.reduce((prev, cur) => {
//     if (cur > prev[1]) prev[1] = cur
//     if (cur < prev[0]) prev[0] = cur
//     return prev
//   }, [+Infinity, -Infinity])
// }

export default
class Detail extends ImmutableComponent<void, Props, void> {
  static anglePaddingScale = 0

  render() {
    const {
      details,
      detailsCurPage,
      detailsBySequence,
      detailsBySequenceCurPage,
      pageSize,
      clientRect,
      filters,
      onDetailsPageChange,
      onDetailsBySequencePageChange
    } = this.props
    const { width, height } = clientRect
    const cx = width / 2
    const cy = height / 2
    const maxRadius = Math.min.call(null, height * 0.7 / 2, width * 0.6 / 2)
    const countSums = details
      .map(detail => detail
        .getIn(['items', 'data'])
        .reduce((prev, cur) => prev + cur.get('count'), 0)
      )
    const [ minCount, maxCount ] = getRange(countSums)


    const parts = details
      .flatMap(detail => detail.getIn(['items', 'desc']))
      .reduce((prev, cur) => prev.set(cur.get('id'), cur.get('name')), Immutable.Map())

    const partColors = parts.mapEntries(([key, _], index) => [key, COLORS[index]])

    const groups = details
      .filter(detail => {
        const typeFilter = filters.find(filter => filter.get('key') === 'type')
        const partFilter = filters.find(filter => filter.get('key') === 'part')
        let res = true
        if (typeFilter) res = res && detail.getIn(['type', 'id']) === typeFilter.get('value')
        if (partFilter) res = res && detail.getIn(['part', 'id']) === partFilter.get('value')

        return res
      })
      .map((detail, index) => {
        const data = detail.getIn(['items', 'data'])
        return Immutable.fromJS({
          id: detail.getIn(['asset', 'id']),
          text: detail.getIn(['asset', 'name']),
          annuluses: data.map(datum => Immutable.fromJS(({
            width: valueToCoordinate(datum.get('count'), [minCount, maxCount], [0.8 * maxRadius, maxRadius]),
            color: COLORS[datum.get('id')]
          })))
        })
      })
      .slice(detailsCurPage * pageSize, (detailsCurPage + 1) * pageSize)

    const sequenceCountRange = getRange(detailsBySequence.map(detail => detail.getIn(['items', 'data', 0,  'count'])))

    const sequenceGroups = detailsBySequence
      .filter(detail => {
        const typeFilter = filters.find(filter => filter.get('key') === 'type')
        const partFilter = filters.find(filter => filter.get('key') === 'part')
        let res = true
        if (typeFilter) res = res && detail.getIn(['type', 'id']) === typeFilter.get('value')
        if (partFilter) res = res && detail.getIn(['part', 'id']) === partFilter.get('value')

        return res
      })
      .map((detail, index) => {
        const data = detail.getIn(['items', 'data'])
        return Immutable.fromJS({
          id: detail.getIn(['sequence', 'id']),
          text: detail.getIn(['sequence', 'name']),
          annuluses: data.map(datum => Immutable.fromJS({
            width: valueToCoordinate(datum.get('count'), sequenceCountRange, [0.8 * maxRadius, maxRadius]),
            color: COLORS[detail.getIn(['part', 'id'])]
          }))
        })
      })
      .slice(detailsBySequenceCurPage * pageSize, (detailsBySequenceCurPage + 1) * pageSize)

    return (
        <Motion
          defaultStyle={{
            opacity: filters.size ? 1 : 0
          }}
          style={{
            opacity: spring(filters.size ? 1 : 0)
          }}
        >
          {
            ({opacity}) => (
              <g>
                <g opacity={1 - opacity} display={opacity === 1 ? 'none' : ''}>
                  <AnnulusRing
                    cx={cx + 0.1 * width}
                    cy={cy}
                    innerRadius={0.8 * maxRadius}
                    startAngle={0}
                    endAngle={Math.PI}
                    groups={groups}
                    curPage={detailsCurPage}
                    pageSize={pageSize}
                    onPageChange={diff => {
                      if (groups.size < pageSize && diff === 1) return
                      if (detailsCurPage === 0 && diff === -1) return
                      this.props.onDetailsPageChange(diff)
                    }}
                  />
                </g>
                <g opacity={opacity} display={opacity === 0 ? 'none' : ''}>
                  <AnnulusRing
                    cx={cx + 0.1 * width}
                    cy={cy}
                    innerRadius={0.8 * maxRadius}
                    startAngle={0}
                    endAngle={Math.PI}
                    groups={sequenceGroups}
                    curPage={detailsBySequenceCurPage}
                    pageSize={pageSize}
                    onPageChange={diff => {
                      if (sequenceGroups.size < pageSize && diff === 1) return
                      if (detailsCurPage === 0 && diff === -1) return
                      this.props.onDetailsBySequencePageChange(diff)
                    }}
                  />
                </g>
              </g>
            )
          }
        </Motion>
    )

    // return (
    //   <g transform={`translate(${cx + 0.1 * width}, ${cy})`}>
    //     {details.filter(detail => {
    //       const typeFilter = filters.find(filter => filter.get('key') === 'type')
    //       if (!typeFilter) return true
    //       return detail.getIn(['type', 'id']) === typeFilter.get('value')
    //     }).map((detail, index) => (
    //       <Item
    //         key={detail.getIn(['asset', 'id'])}
    //         detail={detail}
    //         maxRadius={maxRadius}
    //         minRadius={0.8 * maxRadius}
    //         minCount={minCount}
    //         maxCount={maxCount}
    //         {...this.getStartAndEndAngle(index, details.size)}
    //         partColors={partColors}
    //       />
    //     ))}
    //   </g>
    // )
  }
}
