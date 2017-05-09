/* @flow */
import React, { PureComponent } from 'react'
import { arc } from 'd3-shape'

import styles from './styles.scss'

type PropsT = {
  innerRadius: number,
  outerRadius?: number,
  startAngle: number,
  endAngle: number,
  width?: number,
  color?: string
}

export default class AnnulusSector extends PureComponent<void, PropsT, void> {
  render() {
    const {
      innerRadius, outerRadius,
      startAngle, endAngle,
      width, color
    } = this.props

    const d = arc()
      .innerRadius(innerRadius)
      .outerRadius(outerRadius || innerRadius + width)
      .startAngle(startAngle)
      .endAngle(endAngle)()

    return (
      <path className={styles.path} fill={color} d={d}/>
    )
  }
}
