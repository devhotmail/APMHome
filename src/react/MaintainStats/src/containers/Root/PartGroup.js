/* @flow */
import React, { PureComponent } from 'react'
import RingSectorLayout from 'ring-sector-layout'
import AnnulusSector from 'ring-sector-layout/dist/AnnulusSector'
import AnnulusSectorStack from 'ring-sector-layout/dist/AnnulusSectorStack'

import { formatData } from './helper'

const purple = '#b781b4'
const prasinous = '#6ab6a6'

export default class PartGroup extends PureComponent {
  render () {
    const { data, switcher, animationDirection } = this.props

    const chartData = formatData(data).map(n => {
      return {
        id: n.id,
        name: n.name,
        children: n[switcher]
      }
    })

    const startAngle = 10 / 180 * Math.PI
    const endAngle = 170 / 180 * Math.PI
    const range = endAngle - startAngle

    return (
      <RingSectorLayout
        startAngle={-startAngle}
        endAngle={-endAngle}
        getCx={width => width}
        getCy={(width, height) => height / 2}
        items={chartData}
        animationDirection={animationDirection || 0}>
        {
          (item, index, innerRadius, outerRadius) => {
            const span = Math.min(range / 18, range / (chartData.length + 1))
            const { data } = item
            return (
              <AnnulusSectorStack
                key={item.key}
                startAngle={item.style.position - span / 2}
                endAngle={item.style.position + span / 2}
                innerRadius={innerRadius}
                sectors={data.children.map((item, i) => ({
                  width: (outerRadius - innerRadius) * item.value,
                  fill: item.color
                }))}
                text={{
                  content: data.name,
                  fill: '#6b6b6b',
                  fontSize: 16
                }}
                opacity={item.style.opacity} />
            )
          }
        }
      </RingSectorLayout>
    )
  }

  handleChange = (payload) => {
    const { key, value } = payload
    console.log(payload)
  }
}
