/* @flow */
import React, { Component } from 'react'
import { arc } from 'd3-shape'

const mockData = Array(10).fill(7)
console.log(mockData)

class Chart extends Component {
  render () {
    return (
      <div>
        Hello AnnulusChart
        <svg>
          {
            mockData.map((n, i) => {
              const path = arc()
              .innerRadius(50)
              .outerRadius(100)
              .startAngle(i * 2)
              .endAngle(i * 3)
              return <path d={path()} fill="#000" />
            })
          }
        </svg>
      </div>
    )
  }
}

export default Chart
