import React, { Component } from 'react'

import ProgressBar from '#/components/ProgressBar'

export default class SystemPanel extends Component {
  render () {
    const { focus } = this.props
    return (
      <div>
        <div className="lead">当前位置: {focus.data.name}</div>
        <ProgressBar />
      </div>
    )
  }
}
