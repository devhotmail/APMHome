// @flow
import React from 'react'
import type { Map } from 'immutable'
import ImmutableComponent from '#/components/ImmutableComponent'
import withClientRect from '#/HOC/withClientRect'
import type { ClientRect } from '#/HOC/withClientRect'
import Brief from './Brief'
import Detail from './Detail'
import styles from './index.scss'
import body from '#/assets/body.png'

type Props = {
  className: string,
  scans: Map<string, any>,
  clientRect: ClientRect
}

class BodyChart extends ImmutableComponent<*, Props, *> {
  render() {
    const { scans, clientRect, className } = this.props
    const { width, height } = clientRect
    const briefs = scans.get('briefs')
    const details = scans.get('details')
    const detailsCurPage = scans.get('detailsCurPage')
    const detailsBySequence = scans.get('detailsBySequence')
    const detailsBySequenceCurPage = scans.get('detailsBySequenceCurPage')
    const pageSize = scans.get('pageSize')
    const filters = scans.get('filters')
    return (
      <svg
        className={`${styles['body-chart']} ${className}`}
        viewBox={`0 0 ${width} ${height}`}
        >
        <Brief briefs={briefs} clientRect={clientRect} filters={filters}/>
        <Detail
          details={details}
          detailsCurPage={detailsCurPage}
          detailsBySequence={detailsBySequence}
          detailsBySequenceCurPage={detailsBySequenceCurPage}
          onDetailsPageChange={number => this.props.dispatch({
            type: 'scans/detail/page/change',
            payload: number
          })}
          onDetailsBySequencePageChange={number => this.props.dispatch({
            type: 'scans/detailBySequence/page/change',
            payload: number
          })}
          pageSize={pageSize}
          clientRect={clientRect}
          filters={filters}
        />
        <g
          transform={`translate(${width / 2}, ${height / 2})`}>
          <image
            width={0.2 * width}
            height={0.4 * width}
            x={-0.1 * width}
            y={-0.2 * width}
            xlinkHref={body}
          />
        </g>
      </svg>
    )
  }
}

export default
withClientRect(BodyChart)
