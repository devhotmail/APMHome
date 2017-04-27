/* @flow */
import React, { Component } from 'react'
import uuid from 'uuid/v4'
import * as d3 from 'd3'
import raf from 'raf'

import data from '#/mock/data'

const sizeKey = 'size'
const childKey = 'items'
const margin = 20

type State = {
  focusId: string,
  nodeList: Array<Object>,
  visibleNodes: Array<Object>
}

function DataProvider(WrappedComponent: Class<Component<*, *, *>>): Class<Component<*, *, *>> {
  return class extends Component {
    state = {
      focus: undefined,
      view: undefined,
      nodeList: []
    }

    componentDidMount () {
      const { diameter } = this.props
      if (diameter) {
        this.setState((state, props) => {
          const nodeList = this.getNodes(data, diameter)
          const focus = state.focus ? state.focus : nodeList[0]
          const view = this.getView(focus)

          return {
            ...state,
            focus,
            view,
            nodeList
          }
        })
      }
    }

    componentWillReceiveProps(nextProps) {
      const { diameter } = nextProps
      if (diameter) {
        this.setState((state, props) => {
          const nodeList = this.getNodes(data, diameter)
          const focus = state.focus ? state.focus : nodeList[0]
          const view = this.getView(focus)

          return {
            ...state,
            focus,
            view,
            nodeList
          }
        })
      }
    }
    
    render() {
      const { focus, nodeList, view } = this.state

      if (!nodeList) return <div>data loading...</div>
      return <WrappedComponent
        nodeList={nodeList}
        view={view}
        focus={focus}
        setFocus={this.setFocus}
        handleBackUpper={this.handleBackUpper}
        handleBackRoot={this.handleBackRoot}
        {...this.props} />
    }


    handleBackUpper = (e?: Event) => {
      e && e.preventDefault()
      const { focus } = this.state
      if (focus.parent) this.setFocus(focus.parent)
    }

    handleBackRoot = (e?: Event) => {
      e && e.preventDefault()
      const { nodeList } = this.state
      if (nodeList[0]) this.setFocus(nodeList[0])
    }

    setFocus = (focus: Object) => {
      const nextView = this.getView(focus)
      const i = d3.interpolateZoom(this.state.view, nextView)

      this.setState((state, props) => ({
        ...state,
        focus: focus
      }))

      d3.transition()
      .duration(750)
      .tween('zoom', () => t => {
        this.setState((state, props) => {
          return {
            ...state,
            view: i(t)
          }
        })
      })
    }

    setFocus1 = (focus: Object) => {
      const nextView = this.getView(focus)
      const i = d3.interpolateZoom(this.state.view, nextView)

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
      return [node.x, node.y, node.r * 2 + margin]
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
}

export default DataProvider
