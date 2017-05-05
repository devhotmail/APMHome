// @flow
import React from 'react'
import ReactDOM from 'react-dom'
import { connect } from 'dva'
import type { IndexedIterable, KeyedIterable, Map, List } from 'immutable'
import * as Immutable from 'immutable'
import AnnulusRing from '#/components/AnnulusRing'
import ImmutableComponent from '#/components/ImmutableComponent'
import AnnulusSector from '#/components/AnnulusSector'
import { getRange, valueToCoordinate } from '#/utils'
import { COLORS } from '#/constants'


type Props = {
  cx: number,
  cy: number,
  maxRadius: number,
  minRadius: number,
  briefs: List<*>,
  filters: List<*>
}

export default
@connect()
class Types extends ImmutableComponent<void, Props, void> {
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
        <div>{data.getIn(['data', 'type', 'name'])}</div>
        {data.getIn(['data','items', 'data']).map(datum => (
          <div key={datum.get('id')}>
            <span>{this.props.parts.getIn([datum.get('id'), 'name'])}</span>
            <span>{datum.get('count')}</span>
          </div>
        ))}
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
    const { cx, cy, maxRadius, minRadius, briefs, parts, filters } = this.props
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
        color: parts.getIn([datum.get('id'), 'color'])
      })),
      data: brief
    }))

    return (
      <AnnulusRing
        cx={cx}
        cy={cy}
        innerRadius={minRadius}
        startAngle={-15 / 180 * Math.PI}
        endAngle={-165 / 180 * Math.PI}
        groups={groups}
        onMouseEnter={this.onMouseEnter}
        onMouseLeave={this.onMouseLeave}
        onGroupClick={group => this.props.dispatch({
          type: 'scans/filters/toggle',
          payload: {
            key: 'type',
            value: group.get('id')
          }
        })}
      />
    )

  }
}
