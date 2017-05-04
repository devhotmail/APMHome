/* @flow */
import React, { Component } from 'react'

import type { ConfigT } from '../interface'

class ConfigDept extends Component<*, ConfigT, *> {
  state = {
    dept: undefined
  }

  render () {
    const { config, focus, depths, setFocus } = this.props
    const { dept } = this.state
    const configListOne = config.filter(n => n.depth === depths[0])
    const configListTwo = config.filter(n => n.depth === depths[1] )
    // console.log(configListTwo)
    return (
      <div>
        <ul>
          {
            configListOne.length ? configListOne.map((n, index) => {
              const cls = this.isSameNode(n, focus) || (focus.parent ? this.isSameNode(n, focus.parent) : false)
                ? 'active'
                : ''
              const onClick = e => {
                const { depth, data: { id }} = n
                setFocus([id, depth])
                this.setState({ dept: n })
              }
              return <li key={index} className={cls} onClick={onClick}>{n.data.name}</li>
            }) : null
          }
        </ul>
        <ul>
          {
            dept && Array.isArray(dept.children) ? dept.children.map((n, index) => {
              const cls = this.isSameNode(n, focus) || (focus.parent ? this.isSameNode(n, focus.parent) : false)
                ? 'active'
                : ''
              const onClick = e => {
                const { depth, data: { id }} = n
                setFocus([id, depth])
                this.setState({ dept: n })
              }
              return <li key={index} className={cls} onClick={onClick}>{n.data.name}</li>
            }) : null
          }
        </ul>
      </div>
    )
  }

  isSameNode = (node, focus) => {
    return node.id === focus.data.id && node.depth === focus.depth
  }
}

export default ConfigDept