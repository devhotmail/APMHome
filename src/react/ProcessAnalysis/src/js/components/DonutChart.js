import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import PieChart from 'react-minimal-pie-chart'
import classnames from 'classnames'
import { memoization } from 'javascript-decorators'
import colorUtil from 'color'


const MAGIC_NUMBER = 2.1546
const DARKEN_LIMIT = .8

const SampleData = [
  { value: 10, key: 1 },
  { value: 15, key: 2 },
  { value: 20, key: 3 },
  { value: 20, key: 4 },
]

let wrapperStyle = radius => {
  return {
    width: radius * 2,
    height: radius * 2,
    position: 'absolute',
  }
}

let centered = (radius, fontColor) => ({
  fontSize: radius / 6,
  fontColor: fontColor || '',
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  textAlign: 'center'
})

let titleStyle = {
  display: 'inline-block',
  paddingBottom: 3,
  fontWeight: 'bold'
}

function rowHelper(rowData) {
  return (
    <div>
      <span>{rowData.label}: {rowData.value}</span>
    </div>
  )
}


export default class DonutChart extends PureComponent {

  @memoization()
  static getColorGradation(baseColor, number) {
    let result = [ baseColor ]
    if (number > 1) {
      let color = colorUtil(baseColor)
      let step = DARKEN_LIMIT / number
      for (let i = 1; i < number; i++) {
        result.push(color.darken(i * step).hexString())
      }
    }
    return result
  }
  onClickProxy = evt => {
    this.props.onClick(evt)
  }

  render() {
    let { title, rows, radius, data, baseColor, fontColor, className, onClick, ...restPorps } = this.props
    let colorGradation = DonutChart.getColorGradation(baseColor, data.length)

    return (
      <div 
        style={wrapperStyle(radius)} 
        className={classnames('donut-chart', className)} 
        onClick={onClick && this.onClickProxy}
        {...restPorps}
      >
        <PieChart
          lineWidth='10'
          data={SampleData.map((d, i) => { d.color = colorGradation[i]; return d })}
        />
        <div style={centered(radius, fontColor)}>
          <span style={titleStyle}>{title}</span>
          { rows.map(rowHelper) }
        </div>
      </div>
    )
  }
}

DonutChart.propTypes = {
  radius: PropTypes.number.isRequired,
  title: PropTypes.string,
  rows: PropTypes.arrayOf(PropTypes.object),
  fontColor: PropTypes.string,
  data: PropTypes.arrayOf(PropTypes.object),
  onClick: PropTypes.func,
  baseColor: PropTypes.string.isRequired
}

DonutChart.defaultProps = {
  radius: 150 / MAGIC_NUMBER,
  data: SampleData,
  title: '到场时间',
  rows: [
    { label: '平均', value: '3小时'},
    { label: 'P75', value: '2小时'},
    { label: 'P95', value: '2.5小时'},
  ]

}