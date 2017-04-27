// @flow
import React from 'react'
import * as Immutable from 'immutable'
import type { Map, IndexedIterable, List } from 'immutable'
import AnnulusSector from '#/components/AnnulusSector'
import ImmutableComponent from '#/components/ImmutableComponent'

type Annulus = Map<string, any>

export type Props = {
  text: string,
  innerRadius: number,
  startAngle: number,
  endAngle: number,
  annuluses: IndexedIterable<Annulus>
}

export default
class AnnulusSectorGroup extends ImmutableComponent<*, Props, *> {
  render() {
    const { text, innerRadius, startAngle, endAngle, annuluses } = this.props
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
    return (
      <g>
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
          fill="white"
          x={textX}
          y={textY}
          dy="0.35em"
          transform={`rotate(${textRotate} ${textX} ${textY})`}
        >
          {text}
        </text>
      </g>
    )
  }
}
