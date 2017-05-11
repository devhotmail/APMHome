/* @flow */
import React, { Component } from 'react'
import { connect } from 'dva'

@connect()
export default WrappedComponent => 
class extends Component {
  state = {
    type: 'hours'
  }

  render() {
    const { data, width, height, diameter } = this.props 

    if (!data) return <div>暂无数据</div>

    const { type } = this.state

    const nodeList = data.map(n => ({
      id: n.id,
      text: n.name,
      stackes: n[type],
      info: n.data
    }))

    return <WrappedComponent
      width={width}
      height={height}
      diameter={diameter}
      nodeList={nodeList}
      setFocus={this.setFocus} />
  }

  setFocus = (node: cursorT) => {
    this.props.dispatch({
      type: 'list/focus/set',
      payload: node
    })
  }
}
