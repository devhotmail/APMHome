import React, { Component } from 'react'
import Slider from 'rc-slider'
import { last } from 'lodash-es'
import Tooltip from 'rc-tooltip'
import { HumanizeDurationLabel } from 'utils/helpers'

const Range = Slider.Range
const Handle = Slider.Handle

const handleWrapper = max => (
   function handle(props) {
    let { value, index, dragging, ...restProps } = props //eslint-disable-line
    value = HumanizeDurationLabel(max - value)
    return (
      <Tooltip
        prefixCls="rc-slider-tooltip"
        overlay={value}
        placement="right"
        key={index}
        visible
      >
        <Handle value={value} {...restProps} />
      </Tooltip>
    )
  }
)

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
      handle={handleWrapper(max)}
      {...restProps}
    />)
  }
}