//@flow
import React, { Component } from 'react'
import { translate } from 'react-i18next'
import _ from 'lodash'
import Radium from 'radium'
import DebounceInput from 'react-debounce-input'
import GearListChart from 'react-gear-list-chart'
import Tooltip from 'components/Tooltip'
import { GenerateTeethData } from 'utils/helpers'

type Props = {
  children: Array,
  t: any
}

let styles = {
  demo: {
    border: '1px solid red'
  },
  container: {
    width: 1000,
    height: 'auto',
    position: 'relative',
    marginTop: '5em'
  },
  clear: {
    clear: 'both'
  }
}

@Radium
export class Demo extends Component<void, Props, void> {

  state = {
    items: GenerateTeethData(8),
    startAngle: 0,
    endAngle: 360,
    outerRadius: 320,
    innerRadius: 250,
    amount: 8,
    margin: 3,
    tooltipX: -861112,
    tooltipY: -861112,
  }

  changeMode = evt => {
    let mode = evt.target.textContent.toLowerCase()
    this.setState({ items: GenerateTeethData(this.state.amount, mode.startsWith('Total') ? undefined : mode) })
  }

  changeValue = evt => {
    let prop = evt.target.getAttribute('data-prop')
    let value = +evt.target.value
    if (prop === 'amount') {
      this.setState({ items: GenerateTeethData(value)})
    } else {
      this.setState({ [prop]: value })
    }
  }

  render() {
    let { startAngle, endAngle, outerRadius, innerRadius, margin, amount, tooltipX, tooltipY, tooltip } = this.state
    return (
      <div className="app">
        <label>Start Angle</label>
        <DebounceInput
          data-prop="startAngle"
          value={startAngle} 
          debounceTimeout={800}
          onChange={this.changeValue} />
        <label>End Angle</label>
        <DebounceInput
          data-prop="endAngle"
          value={endAngle} 
          debounceTimeout={800}
          onChange={this.changeValue} />
        <br/>
        <label>Outer Radius</label>
        <DebounceInput
          data-prop="outerRadius"
          value={outerRadius} 
          debounceTimeout={800}
          onChange={this.changeValue} />
        <label>Inner Radius</label>
        <DebounceInput
          data-prop="innerRadius"
          value={innerRadius} 
          debounceTimeout={800}
          onChange={this.changeValue} />
        <br/>
        <label>Margin</label>
        <DebounceInput
          data-prop="margin"
          value={margin} 
          debounceTimeout={800}
          onChange={this.changeValue} />
        <label>Teeth amount</label>
        <DebounceInput
          data-prop="amount"
          value={amount} 
          debounceTimeout={800}
          onChange={this.changeValue} />
        <br/>

        <button onClick={this.changeMode}>Total Chaos!</button>
        <button onClick={this.changeMode}>Spokerib</button>
        <button onClick={this.changeMode}>Layer</button>
        <button onClick={this.changeMode}>Bar</button>
        <br/>

        <GearListChart 
          id="demo" 
          style={styles.demo}
          startAngle={startAngle} endAngle={endAngle} 
          outerRadius={outerRadius} innerRadius={innerRadius}
          margin={margin}
          items={this.state.items}
        />
        <div>
          <Tooltip mouseX={tooltipX} mouseY={tooltipY}>
            <div style={{color: tooltip && tooltip.color}}>{JSON.stringify(tooltip)}</div>
          </Tooltip> 
        </div>
      </div>
    )
  }
}

export default translate()(Demo)
