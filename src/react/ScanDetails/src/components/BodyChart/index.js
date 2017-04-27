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
    const filters = scans.get('filters')
    return (
      <svg
        className={`${styles['body-chart']} ${className}`}
        viewBox={`0 0 ${width} ${height}`}
        >
        <Brief briefs={briefs} clientRect={clientRect} filters={filters}/>
        <Detail details={details} clientRect={clientRect} filters={filters}/>
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
