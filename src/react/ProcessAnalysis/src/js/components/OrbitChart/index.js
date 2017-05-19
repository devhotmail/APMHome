import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import classnames from 'classnames'
import { spring, Motion } from 'react-motion'
import catNames from 'cat-names'
import { Polar2Cartesian } from 'utils/math'
import { memoization } from 'javascript-decorators'
import { AnnulusSector } from 'utils/draw'

import './styles.scss'

const RESERVED_ANGEL = 5

function renderBall(ball, radius, cx, cy, angle, ballRadius) {
  let [x, y] = Polar2Cartesian(radius, angle, { baseX: cx, baseY: cy })

  return (
    <g className="orbit-chart-ball" key={ball.key} >
      <circle cx={x} cy={y} r={ballRadius} fill="white" stroke="gray"/>
      <text x={x} y={y} textAnchor="middle" dy=".3em">{ball.label}</text>
    </g>
  )
}

function renderLane(balls, radius, cx, cy, color) {
  let ball = balls.find(_ => _.connectPrevious)
  if (ball) {
    return (
      <g className="orbit-chart-lane">
        <path d={AnnulusSector({
          startAngle: 90 - ball.distance,
          endAngle: 90,
          outerRadius: radius + 5,
          innerRadius: radius - 5,
          cx: cx,
          cy: cy,
        })} fill={color} stroke="none" />
      </g>
    )
  } else {
    return null
  }
}

export default class OrbitChart extends PureComponent {

  @memoization()
  getAvailiableAngle(ballCount) {
    return 360 - RESERVED_ANGEL * ballCount
  }

  render() {

    let { id, className, radius, ballRadius, balls, laneColor } = this.props
    let totalRadius = radius + ballRadius
    let chartSize = (radius + ballRadius) * 2
    let cx, cy; cx = cy = totalRadius
    
    return (
      <div id={id} className={classnames('orbit-chart', className)}>
        <svg width={chartSize} height={chartSize}>
          <circle className="indicator-circle" cx={cx} cy={cy} r={totalRadius} fill="none" stroke="gray"/>
          { renderLane(balls, totalRadius, cx, cy, laneColor) }
          { balls.map(ball => {
            let angle = ball.distance - 90
            return renderBall(ball, totalRadius, cx, cy, angle, ballRadius)
          })}
        </svg>
      </div> 
    )
  }
}

OrbitChart.defaultProps = {
  startAngle: 0,
  endAngle: 360,
  clockwise: true,
  maxBallAngle: 360,
}


OrbitChart.propTypes = {
  radius: PropTypes.number.isRequired,
  startAngle: PropTypes.number,
  endAngle: PropTypes.number,
  clockwise: PropTypes.bool,
  ballRadius: PropTypes.number.isRequired,
  maxBallAngle: PropTypes.number,
  balls: PropTypes.arrayOf(PropTypes.object),
  laneColor: PropTypes.string.isRequired
}