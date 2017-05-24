import React, { Component } from 'react'
import Slider from 'rc-slider'
import { last } from 'lodash-es'
import moment from 'moment'
import 'rc-slider/assets/index.css'

const Range = Slider.createSliderWithTooltip(Slider.Range)

function periodFormatter(value) {
  if (value === 0) {
    return '0'
  }
  return moment.duration(value * 1000).humanize() // fixme: momentjs abandoned residue
}

export default class ReversedRange extends Component {

  onChangeProxy(value) {
    let max = last(value)
    let reversedValue = value.map(v => max - v).reverse()
    // make first/last immutable
    reversedValue[0] = 0
    reversedValue[value.length - 1] = max
    this.props.onChange(reversedValue)
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
      tipFormatter={val => periodFormatter(max - val)} 
      {...restProps}
    />)
  }
}