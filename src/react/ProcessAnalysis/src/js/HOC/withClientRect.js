// @flow
import React from 'react'
import ReactDOM from 'react-dom'

export type ClientRect = {
  width: number,
  height: number,
  left: number,
  right: number,
  top: number,
  bottom: number
}

export default
(ComposedComponent: *) => class withClientRect extends React.Component<*, *, ClientRect> {
  state = {
    width: 0,
    height: 0,
    left: 0,
    right: 0,
    top: 0,
    bottom: 0
  }
  calcRect = () => {
    const el = ReactDOM.findDOMNode(this)
    if (el && el instanceof Element) {
      const rect = el.getBoundingClientRect()
      this.setState({
        width: rect.width,
        height: rect.height,
        left: rect.left,
        right: rect.right,
        top: rect.top,
        bottom: rect.bottom
      })
    }
  }

  componentDidMount() {
    this.calcRect()
    window.addEventListener('resize', this.calcRect)
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.calcRect)
  }

  render() {
    return <ComposedComponent {...this.props} clientRect={{...this.state}}/>
  }
}
