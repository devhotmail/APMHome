import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import PieChart from 'react-minimal-pie-chart'
import classnames from 'classnames'


const MAGIC_NUMBER = 2.1546

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

  onClickProxy = evt => {
    this.props.onClick(evt)
  }

  render() {
    let { title, rows, radius, fontColor, className, onClick, ...restPorps } = this.props
    return (
      <div 
        style={wrapperStyle(radius)} 
        className={classnames('donut-chart', className)} 
        onClick={onClick && this.onClickProxy}
        {...restPorps}
      >
        <PieChart
          lineWidth='10'
          data={[
            { value: 10, key: 1, color: '#E38627' },
            { value: 15, key: 2, color: '#C13C37' },
            { value: 20, key: 3, color: '#6A2135' },
          ]}
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
  onClick: PropTypes.func
}

DonutChart.defaultProps = {
  radius: 150 / MAGIC_NUMBER,
  title: '到场时间',
  rows: [
    { label: '平均', value: '3小时'},
    { label: 'P75', value: '2小时'},
    { label: 'P95', value: '2.5小时'},
  ]

}