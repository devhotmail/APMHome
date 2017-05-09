// @flow
import React from 'react'
import ReactDOM from 'react-dom'
import type { IndexedIterable, KeyedIterable, Map, List } from 'immutable'
import { Motion, spring } from 'react-motion'
import type { ClientRect } from '#/HOC/withClientRect'
import * as Immutable from 'immutable'
import AnnulusRing from '#/components/AnnulusRing'
import ImmutableComponent from '#/components/ImmutableComponent'
import AnnulusSector from '#/components/AnnulusSector'
import { valueToCoordinate, getRange } from '#/utils'
import { COLORS, PAGE_SIZE } from '#/constants'


type Props = {
  filters: List<*>,
  details: List<*>,
  detailsCurPage?: number,
  detailsByStep: List<*>,
  detailsByStepCurPage?: number,
  pageSize?: number,
  onDetailsPageChange?: number => void,
  onDetailsBySequencePageChange?: number => void,
  clientRect: ClientRect
}


export default
class Detail extends ImmutableComponent<void, Props, void> {
  static anglePaddingScale = 0

  onDetailMouseEnter = (clientRect, data) => {
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
    this.detailEl = ReactDOM.render(
      <div style={style}>
        <div>{data.getIn(['data', 'type', 'name'])}</div>
        {data.getIn(['data', 'items', 'data']).map(datum => (
          <div key={datum.get('id')}>
            <span>{this.props.parts.getIn([datum.get('id'), 'name'])}</span>
            <span>{datum.get('count')}</span>
          </div>
        ))}
      </div>,
      document.createElement('div')
    )
    document.body.appendChild(this.detailEl)
  }

  onDetailMouseLeave = e => {
    document.body.removeChild(this.detailEl)
    this.el = null
  }

  onStepMouseEnter = (clientRect, data) => {
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
    this.stepEl = ReactDOM.render(
      <div style={style}>
        <div>{data.getIn(['data', 'type', 'name'])}</div>
        <div>{data.getIn(['data', 'asset', 'name'])}</div>
        <div>{data.getIn(['data', 'part', 'name'])}</div>
        <div>{data.getIn(['data', 'step', 'name'])}</div>
        <div>{data.getIn(['data', 'step', 'count'])}</div>
      </div>,
      document.createElement('div')
    )
    document.body.appendChild(this.stepEl)
  }

  onStepMouseLeave = e => {
    document.body.removeChild(this.stepEl)
    this.el = null
  }

  render() {
    const {
      parts,
      details,
      detailsCurPage,
      detailsByStep,
      detailsByStepCurPage,
      pageSize,
      clientRect,
      filters,
      onDetailsPageChange,
      onDetailsBySequencePageChange
    } = this.props
    const { width, height } = clientRect
    const cx = width / 2
    const cy = height / 2
    const maxRadius = Math.min.call(null, height * 0.95 / 2, width * 0.8 / 2)
    const countSums = details
      .map(detail => detail
        .getIn(['items', 'data'])
        .reduce((prev, cur) => prev + cur.get('count'), 0)
      )
    const [ minCount, maxCount ] = getRange(countSums)

    const innerRadius = 0.6 * maxRadius
    const outerRadius = 0.9 * maxRadius

    const groups = details
      .map((detail, index) => {
        const data = detail.getIn(['items', 'data'])
        return Immutable.fromJS({
          id: detail.getIn(['asset', 'id']),
          text: detail.getIn(['asset', 'name']),
          annuluses: data.map(datum => Immutable.fromJS(({
            width: valueToCoordinate(datum.get('count'), [minCount, maxCount], [innerRadius, outerRadius]),
            color: parts.getIn([datum.get('id'), 'color'])
          }))),
          data: detail
        })
      })
      // .filter(detail => {
      //   const typeFilter = filters.find(filter => filter.get('key') === 'type')
      //   const partFilter = filters.find(filter => filter.get('key') === 'part')
      //   let res = true
      //   if (typeFilter) res = res && detail.getIn(['type', 'id']) === typeFilter.get('value')
      //   if (partFilter) res = res && detail.getIn(['part', 'id']) === partFilter.get('value')
      //
      //   return res
      // })

      // .slice(0, PAGE_SIZE)

    const sequenceCountRange = getRange(detailsByStep.map(detail => detail.getIn(['step', 'count'])))

    const stepGroups = detailsByStep
      .map((detail, index) => {
        const count = detail.getIn(['step', 'count'])
        return Immutable.fromJS({
          id: `${detail.getIn(['step', 'id'])}-${detail.getIn(['asset', 'id'])}-${detail.getIn(['part', 'id'])}-${detail.getIn(['type', 'id'])}`,
          text: detail.getIn(['step', 'name']),
          extraText: detail.getIn(['asset', 'name']),
          annuluses: [{
            width: valueToCoordinate(count, sequenceCountRange, [innerRadius, outerRadius]),
            color: parts.getIn([detail.getIn(['part', 'id']), 'color'])
          }],
          data: detail
        })
      })
      // .filter(detail => {
      //   const typeFilter = filters.find(filter => filter.get('key') === 'type')
      //   const partFilter = filters.find(filter => filter.get('key') === 'part')
      //   let res = true
      //   if (typeFilter) res = res && detail.getIn(['type', 'id']) === typeFilter.get('value')
      //   if (partFilter) res = res && detail.getIn(['part', 'id']) === partFilter.get('value')
      //
      //   return res
      // })

      // .slice(0, PAGE_SIZE)

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
                    innerRadius={innerRadius}
                    outerRadius={outerRadius}
                    startAngle={0}
                    endAngle={Math.PI}
                    groups={groups.slice(0, PAGE_SIZE)}
                    curPage={detailsCurPage}
                    pageSize={pageSize}
                    showPrev={detailsCurPage > 0}
                    showNext={groups.size > PAGE_SIZE}
                    onMouseEnter={this.onDetailMouseEnter}
                    onMouseLeave={this.onDetailMouseLeave}
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
                    innerRadius={innerRadius}
                    outerRadius={outerRadius}
                    startAngle={0}
                    endAngle={Math.PI}
                    groups={stepGroups.slice(0, PAGE_SIZE)}
                    curPage={detailsByStepCurPage}
                    pageSize={pageSize}
                    showPrev={detailsByStepCurPage > 0}
                    showNext={stepGroups.size > PAGE_SIZE}
                    onMouseEnter={this.onStepMouseEnter}
                    onMouseLeave={this.onStepMouseLeave}
                    onPageChange={diff => {
                      if (stepGroups.size < pageSize && diff === 1) return
                      if (detailsByStepCurPage === 0 && diff === -1) return
                      this.props.onDetailsBySequencePageChange(diff)
                    }}
                  />
                </g>
              </g>
            )
          }
        </Motion>
    )
  }
}
