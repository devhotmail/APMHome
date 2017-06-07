import React, { Component } from 'react'
import Slider from 'rc-slider'
import { last } from 'lodash-es'
import { HumanizeDurationLabel } from 'utils/helpers'
// import 'rc-slider/assets/index.css'

const Range = Slider.createSliderWithTooltip(Slider.Range)

export default class ReversedRange extends Component {

  onChangeProxy(value) {
    let max = last(this.props.value)
    let reverseBack = value.map(v => max - v).reverse()
    // make first/last immutable
    if (reverseBack[0] === 0 && reverseBack[value.length - 1] === max) {
      this.props.onChange(reverseBack)
    }
  }

  render() {
    let { value, onChange, ...restProps } = this.props
    let max = last(value)
    let mirrored = value.map(v => max - v).reverse()

    return (<Range
      vertical
      min={0} 
      max={max} 
      value={mirrored}
      defaultValue={mirrored}
      onChange={onChange && this.onChangeProxy.bind(this)}
      tipFormatter={val => HumanizeDurationLabel(max - val)} 
      {...restProps}
    />)
  }
}