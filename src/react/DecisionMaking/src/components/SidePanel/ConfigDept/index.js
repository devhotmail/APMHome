/* @flow */
import React, { Component } from 'react'

import { getCursor, isSameCursor, isFocusNode } from '#/utils'

import type { ConfigT, NodeT, cursorT } from '#/types'

class ConfigDept extends Component<*, ConfigT, *> {
  render () {
    const { config, focus: { cursor}, depths, setFocus } = this.props

    const configListOne = config.filter(n => n.depth === depths[0])
    if (!configListOne.length) return null

    const activeCursors = this.getParentCursors()

    const configListTwo = config.filter(n => n.depth === depths[1])

    return (
      <div>
        <ul>
          {
            configListOne.length ? configListOne.map((n, index) => {
              const cls = isFocusNode(n, activeCursors[0])
                ? 'active'
                : ''
              const onClick = e => setFocus(getCursor(n))
              return <li key={index} className={cls} onClick={onClick}>{n.data.name}</li>
            }) : null
          }
        </ul>
        { activeCursors[0] ? this.renderConfigTwo(activeCursors) : null}
      </div>
    )
  }

  renderConfigTwo = (activeCursors) => {
    const { config, focus: { cursor}, depths, setFocus } = this.props

    const configListTwo = config.filter(n => n.depth === depths[1])
    .filter(n => isFocusNode(n.parent, activeCursors[0]))

    return (
      <ul>
        {
          configListTwo ? configListTwo.map((n, index) => {
            const cls = isFocusNode(n, activeCursors[1])
              ? 'active'
              : ''
            const onClick = e => setFocus(getCursor(n))
            return <li key={index} className={cls} onClick={onClick}>{n.data.name}</li>
          }) : null
        }
      </ul>
    )
  }

  shouldActive = (node: NodeT, cursors: Array<cursorT>): boolean => {
    const cursor = getCursor(node)
    return cursors.find(n => isSameCursor(cursor, n))
  }

  getParentCursors = (): Array<cursorT>  => {
    const { config, focus: { cursor }} = this.props

    const target = config.find(n => isFocusNode(n, cursor))

    if (!target) return []

    return getCursors(target)

    function getCursors(node) {
      const nodes = [getCursor(node)]
      getParent(node)
      return nodes.reverse().slice(1) // remove root

      function getParent(node) {
        const parent = node.parent
        if (!parent) return
        nodes.push(getCursor(parent))
        getParent(parent)
      }
    }
  }
}

export default ConfigDept