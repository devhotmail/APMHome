/* @flow */
import React, { Component } from 'react'
import uuid from 'uuid/v4'
import * as d3 from 'd3'

import data from '#/mock/data'

const valueKey = 'revenue'
const childKey = 'items'
const margin = 20

type State = {
  focusId: string,
  nodeList: Array<Object>,
  visibleNodes: Array<Object>
}

function DataProvider(WrappedComponent: Class<Component<*, *, *>>): Class<Component<*, *, State>> {
  return class extends Component {
    state = {
      focusId: '',
      nodeList: [],
      visibleNodes: []
    }

    componentDidMount () {
      this.setState((state, props) => {
        const nodeList = this.getNodes(data)

        let { focusId } = state
        if (!focusId) focusId = nodeList[0].uid
        const visibleNodes = this.getVisibleNodes(nodeList, focusId)

        return {
          ...state,
          focusId,
          nodeList,
          visibleNodes
        }
      }, () => {
        // setTimeout(() => {
        //   const { uid } = this.state.nodeList[10]
        //   this.setFocus(uid)
        // }, 3000)    
      })
    }

    render() {
      const { focusId, visibleNodes } = this.state
      const rootData = this.list2Tree(visibleNodes)
      if (!rootData) return <div>data loading...</div>

      return <WrappedComponent rootData={rootData} setFocus={this.setFocus} {...this.props} />
    }

    setFocus = (focusId: string) => {
      this.setState((state, props) => {
        const { nodeList } = state
        const visibleNodes = this.getVisibleNodes(nodeList, focusId)

        return {
          ...state,
          focusId,
          visibleNodes
        }
      })
    }

    list2Tree = (nodeList: Array<Object>): Object => {
      const treeList = []
      const lookup = {}

      const list = nodeList.map(n => ({
        ...n,
        children: []
      }))
      // mark all items
      list.forEach(n => lookup[n.uid] = n)

      list.forEach(n => {
        if (n.parent) lookup[n.parent].children.push(n)
        else treeList.push(n)
      })

      return treeList[0]
    }

    getNodes = (data: Object): Array<Object> => {
      const result = []
      const stack = [data]

      while (stack.length) {
        const item = stack.pop()
        const uid = uuid()
        const size = item[valueKey]
        const children = item[childKey]

        if (Array.isArray(children) && children.length) {
          Array.prototype.push.apply(stack, children.map(n => ({
            ...n,
            parent: uid
          })))
        }

        // remove original childKey && sizeKey
        delete item[childKey]
        delete item[valueKey]

        result.push({
          ...item,
          size,
          uid // give each item an unique id attribute
        })
      }
      return result
    }

    getVisibleNodes = (nodeList: Array<Object>, focusId: string): Array<Object> => {
      const root = nodeList[0]
      const focus = nodeList.find(n => n.uid === focusId) || root
      const parents = this.getParents(focus, nodeList)

      return nodeList.filter(n => !n.parent || ~parents.indexOf(n.parent))
    }

    getParents = (node: Object, nodeList: Array<Object>): Array<string> => {
      const stack = [node]
      const results = []

      while (stack.length) {
        const item = stack.pop()
        results.push(item.uid)
        if (item.parent) {
          const parent = nodeList.find(n => n.uid === item.parent)
          if (parent) stack.push(parent)
        }
      }

      return results
    }
  }
}

export default DataProvider
