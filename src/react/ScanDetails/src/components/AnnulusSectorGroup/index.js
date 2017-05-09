// @flow
import React from 'react'
import ReactDOM from 'react-dom'
import * as Immutable from 'immutable'
import type { Map, IndexedIterable, List } from 'immutable'
import AnnulusSector from '#/components/AnnulusSector'
import ImmutableComponent from '#/components/ImmutableComponent'
import { trimString } from '#/utils'

type Annulus = Map<string, any>

export type Props = {
  onMouseEnter?: Function,
  onMouseLeave?: Function,
  extraText?: string,
  text: string,
  innerRadius: number,
  outerRadius: number,
  startAngle: number,
  endAngle: number,
  annuluses: IndexedIterable<Annulus>,
  opacity: number,
  onClick?: (any) => void
}

export default
class AnnulusSectorGroup extends ImmutableComponent<*, Props, *> {
  render() {
    const { onMouseEnter, onMouseLeave, extraText, text, innerRadius, outerRadius, startAngle, endAngle, annuluses, onClick, opacity } = this.props
    const accumulated = annuluses.reduce((prev: List<Annulus>, cur: Annulus): List<Annulus> => {
      return prev.push(
        cur.set(
          'innerRadius',
          prev.getIn([-1, 'innerRadius'], innerRadius) + prev.getIn([-1, 'width'], 0)
        )
      )
    }, Immutable.List())

    const textR = innerRadius + 10
    const textAngle = (startAngle + endAngle) / 2
    const textX = textR * Math.sin(textAngle)
    const textY = -textR * Math.cos(textAngle)
    const textRotate = textAngle * 180 / Math.PI + (textAngle > 0 ? -1 : 1) * 90
    const extraTextR = outerRadius + 10
    const extraTextX = extraTextR * Math.sin(textAngle)
    const extraTextY = -extraTextR * Math.cos(textAngle)
    return (
      <g
        style={{
          cursor: 'pointer'
        }}
        onClick={onClick || (() => {})}
        onMouseEnter={e => onMouseEnter && onMouseEnter(e.target.getBoundingClientRect())}
        onMouseLeave={onMouseLeave || (() => {})}
        opacity={opacity}
      >
        {accumulated.map((annulus, index) => (
          <AnnulusSector
            key={annulus.get('id', index)}
            innerRadius={annulus.get('innerRadius')}
            width={annulus.get('width')}
            startAngle={startAngle}
            endAngle={endAngle}
            color={annulus.get('color')}
          />
        ))}
        <text
          textAnchor={textAngle > 0 ? "start" : "end"}
          fill="#8c8c8c"
          x={textX}
          y={textY}
          dy="0.35em"
          transform={`rotate(${textRotate} ${textX} ${textY})`}
        >
          {trimString(text, Math.floor((outerRadius - innerRadius) / 5))}
        </text>
        {
          extraText
          ?
          <text
            opacity="0.1"
            textAnchor={textAngle > 0 ? "start" : "end"}
            fill="#555555"
            x={extraTextX}
            y={extraTextY}
            dy="0.35em"
            transform={`rotate(${textRotate} ${extraTextX} ${extraTextY})`}
          >
            {extraText}
          </text>
          : null
        }
      </g>
    )
  }
}
