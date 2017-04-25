import React, { Component } from 'react'

import data from '#/mock/data'

function ChartWrapper(WrappedComponent) {
  return class extends Component {
    render() {
      return <WrappedComponent data={data} {...this.props} />
    }
  }
}

export default ChartWrapper
