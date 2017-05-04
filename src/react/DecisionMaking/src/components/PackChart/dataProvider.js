/* @flow */
import React, { Component } from 'react'
import { connect } from 'dva'
import uuid from 'uuid/v4'
import * as d3 from 'd3'
import raf from 'raf'

import { margin } from '#/constants'
import data from '#/mock/data'

const sizeKey = 'size'
const childKey = 'items'

type State = {
  focusId: string,
  nodeList: Array<Object>,
  visibleNodes: Array<Object>
}

export default WrappedComponent => 
@connect(state => ({
  nodeList: state.nodeList.data,
  focus: state.focus
}))
class extends Component {
  state = {
    view: []
  }

  componentDidMount () {
    const { diameter, focus, nodeList, dispatch } = this.props

    if (diameter) {
      dispatch({
        type: 'nodeList/coefficient/set',
        payload: {
          diameter,
          margin
        }
      })
    }

    if (focus.cursor) {
      const [ id, depth ] = focus.cursor
      const target = nodeList.find(n => n.data.id === id && n.depth === depth)

      if (target) {
        const view = this.getView(target)
        this.setState({ view })
      }
    }
  }

  componentWillReceiveProps(nextProps) {
    const { diameter, focus, nodeList, dispatch } = nextProps
    if (diameter && diameter !== this.props.diameter) {
      dispatch({
        type: 'nodeList/coefficient/set',
        payload: {
          diameter,
          margin
        }
      })
    }

    if (focus.cursor) {
      const [ id, depth ] = focus.cursor
      const target = nodeList.find(n => n.data.id === id && n.depth === depth)

      if (target && focus !== this.props.focus) {
        if (!this.state.view.length) {
          const view = this.getView(target)
          this.setState({ view })  
        } else {
          this.setView(target)
        }
      }
    }
  }
  
  render() {
    const { nodeList, focus } = this.props
    const { view } = this.state

    if (!nodeList) return <div>data loading...</div>
    return <WrappedComponent
      {...this.props}
      nodeList={nodeList}
      view={view}
      focus={focus}
      setFocus={this.setFocus}
      handleBackUpper={this.handleBackUpper}
      handleBackRoot={this.handleBackRoot} />
  }

  handleBackUpper = (e?: Event) => {
    e && e.preventDefault()
    const { focus } = this.props
    if (focus.parent) this.setFocus(focus.parent)
  }

  handleBackRoot = (e?: Event) => {
    e && e.preventDefault()
    const { nodeList } = this.props
    if (nodeList[0]) this.setFocus(nodeList[0])
  }

  setFocus = (focus: Object) => {
    if (focus === this.props.focus) return

    const { depth, data: { id }} = focus

    this.props.dispatch({
      type: 'focus/data/set',
      payload: [ id, depth ]
    })

    this.setView(focus)
  }

  setView = (focus: Object) => {
    const nextView = this.getView(focus)
    const i = d3.interpolateZoom(this.state.view, nextView)

    d3.transition()
    .duration(750)
    .tween('zoom', () => t => {
      this.setState({
        view: i(t)
      })
    })
  }

  setFocus1 = (focus: Object) => {
    const nextView = this.getView(focus)
    const i = d3.interpolateZoom(this.state.view, nextView)

    if (focus === this.state.focus) return

    this.setState((state, props) => ({
      ...state,
      focus: focus
    }))

    const start = Date.now()
    const loop = (now = Date.now())  => {
      raf(() => {
        const now = Date.now()
        const t = (now - start) / 1000 / 0.75
        if (t > 1) return
        this.setState((state, props) => {
          return {
            ...state,
            view: i(t)
          }
        })

        loop(now)
      })
    }

    raf(loop)
  }

  getView = (node: Object) => {
    return [node.x, node.y, node.r]
  }

  getNodes = (data, diameter) => {
    const pack = d3.pack()
    .size([diameter - margin, diameter - margin])
    .padding(2)

    const root = d3.hierarchy(data, d => d.items)
    .sum(d => d[sizeKey])
    .sort((a, b) => b.value - a.value)

    const nodes = pack(root).descendants()

    return nodes
  }
}
