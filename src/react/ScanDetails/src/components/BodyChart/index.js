// @flow
import React from 'react'
import type { Map } from 'immutable'
import { Motion, spring } from 'react-motion'
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
  getImage() {
    const { scans, clientRect } = this.props
    const filters = scans.get('filters')
    const { width, height } = clientRect
    const partFilter = filters.find(filter => filter.get('key') === 'part')
    const partId = partFilter ? partFilter.get('value') : null
    const mapping = {
      1: {
        translate: [0, 1.05 * width],
        scale: 6,
        clipRadius: width * 0.15
      },
      2: {
        translate: [0, 0.7 * width],
        scale: 5,
        clipRadius: width * 0.15
      },
      3: {
        translate: [0, 0.38 * width],
        scale: 3.5,
        clipRadius: width * 0.15
      },
      4: {
        translate: [0, 0.2 * width],
        scale: 3.6,
        clipRadius: width * 0.15
      },
      5: {
        translate: [0, 0.12 * width],
        scale: 4,
        clipRadius: width * 0.15
      },
      6: {
        translate: [0, 0.2 * width],
        scale: 2,
        clipRadius: width * 0.15
      },
      7: {
        translate: [-0.12 * width, 0.1 * width],
        scale: 2,
        clipRadius: width * 0.15
      },
      8: {
        translate: [0, -0.15 * width],
        scale: 1.5,
        clipRadius: width * 0.15
      },
      9: {
        translate: [0, 0],
        scale: 1,
        clipRadius: width
      }
    }
    return (
      <Motion
        defaultStyle={{
          clipRadius: partId === null ? 0.3 * width : mapping[partId].clipRadius,
          translateX: partId === null ? 0 : mapping[partId].translate[0],
          translateY: partId === null ? 0 : mapping[partId].translate[1],
          scale: partId === null ? 1 : mapping[partId].scale
        }}
        style={{
          clipRadius: spring(partId === null ? 0.3 * width : mapping[partId].clipRadius),
          translateX: spring(partId === null ? 0 : mapping[partId].translate[0]),
          translateY: spring(partId === null ? 0 : mapping[partId].translate[1]),
          scale: spring(partId === null ? 1 : mapping[partId].scale)
        }}
      >
        {
          style => (
            <g
              transform={`translate(${width / 2}, ${height / 2})`}
            >
              <defs>
                <clipPath id="image-clip">
                  <circle cx="0" cy="0" r={style.clipRadius} />
                </clipPath>
              </defs>
              <g
                clipPath="url(#image-clip)"
              >
                <image
                  transform={`translate(${style.translateX}, ${style.translateY}) scale(${style.scale})`}
                  width={0.2 * width}
                  height={0.4 * width}
                  x={-0.1 * width}
                  y={-0.2 * width}
                  xlinkHref={body}
                />
              </g>
            </g>
          )
        }
      </Motion>
    )
  }
  render() {
    const { scans, clientRect, className } = this.props
    const { width, height } = clientRect
    const briefs = scans.get('briefs')
    const details = scans.get('details')
    const parts = scans.get('parts')
    const detailsCurPage = scans.get('detailsCurPage')
    const detailsByStep = scans.get('detailsByStep')
    const detailsByStepCurPage = scans.get('detailsByStepCurPage')
    const pageSize = scans.get('pageSize')
    const filters = scans.get('filters')
    return (
      <svg
        className={`${styles['body-chart']} ${className}`}
        viewBox={`0 0 ${width} ${height}`}
        >
        {this.getImage()}
        <Brief parts={parts} briefs={briefs} clientRect={clientRect} filters={filters}/>
        <Detail
          parts={parts}
          details={details}
          detailsCurPage={detailsCurPage}
          detailsByStep={detailsByStep}
          detailsByStepCurPage={detailsByStepCurPage}
          onDetailsPageChange={number => this.props.dispatch({
            type: 'scans/detail/page/change',
            payload: number
          })}
          onDetailsBySequencePageChange={number => this.props.dispatch({
            type: 'scans/detailByStep/page/change',
            payload: number
          })}
          pageSize={pageSize}
          clientRect={clientRect}
          filters={filters}
        />
      </svg>
    )
  }
}

export default
withClientRect(BodyChart)
