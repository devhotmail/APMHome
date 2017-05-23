/* @flow */
import React, { PureComponent } from 'react'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'
import RingSectorLayout from 'ring-sector-layout'
import AnnulusSector from 'ring-sector-layout/dist/AnnulusSector'
import AnnulusSectorStack from 'ring-sector-layout/dist/AnnulusSectorStack'

import { formatData } from './helper'

const purple = '#b781b4'
const prasinous = '#6ab6a6'

type PropsT = {
  data: Array<Object>,
  switcher: string,
  animationDirection: number,
  onClick: Function,
  selectedGroupId: string
}

@connect()
export default class PartGroup extends PureComponent<*, PropsT, *> {
  render () {
    const { data, switcher, animationDirection, onClick, selectedGroupId } = this.props

    const chartData = formatData(data).map(n => {
      return {
        id: n.id,
        name: n.name,
        origin: n.origin,
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
            const opacity = selectedGroupId ? selectedGroupId === item.data.id ? 1 : 0.3 : 1
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
                onClick={onClick(item.data.id)}
                opacity={opacity} />
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
