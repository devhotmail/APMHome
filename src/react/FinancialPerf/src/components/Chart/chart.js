/* @flow */
import React, { Component } from 'react'
import axios from 'axios'
import * as d3 from 'd3'

import styles from './styles.scss'

const margin = 20

const tree = (nodes: Array<NodeT>) => ({handleClick}: {handleClick: Function}): React$Element<any> => {
  return <g>
    {
      nodes.map((n, i) => {
        const [x, y] = project(n, n.y)
        return <circle
          key={i}
          id={`${n.label}`}
          cx={x}
          cy={y}
          r={50}
          onClick={handleClick(n)}></circle>
      })
    }
  </g>
}

function project(n: NodeT, radius: number): Array<number> {
  if (n.x === 0 && n.y === 0) return [0, 0]
  const angle = (n.x - 180 ) / 180 * Math.PI
  return [
    radius * Math.cos(angle),
    radius * Math.sin(angle)
  ]
}

function getLinkPath(n: NodeT, nodes: Array<NodeT>): string {
  const [x, y] = project(n, n.y)
  const parent = nodes.find(node => node.uid === n.parent) || nodes[0]
  const [px, py] = project(parent, n.y - parent.y)
  return `M ${x} ${y} L ${px} ${py}`
}

type NodeT = {
  uid: string,
  x: number,
  y: number,
  parent: string,
  size: number,
  label: string,
  waveFill: number,
  children?: Array<NodeT>
}

type ChartProps = {
  width: number,
  height: number,
  diameter: number,
  rootData: Object,
  setFocus: Function
}

export default class Chart extends Component<*, ChartProps, *> {
  render() {
    const { height, width, diameter, rootData } = this.props

    if (!width || !height) return null

    const r = 50

    const nodes = this.updateView(rootData, diameter)
    const Tree = tree(nodes)

    console.log(nodes)

    return (
      <div>
        <svg width={width} height={height}>
          <g transform={`translate(${width/2}, ${height - margin - r})`}>
            <g className="circle-group">
              <Tree handleClick={this.handleClick} />
            </g>
            <g className="line-group">
              {
                nodes.slice(1).map((n, i) => {
                  const d = getLinkPath(n, nodes)
                  return <path key={i} className={styles.link} d={d}></path>
                })
              }
            </g>
          </g>
        </svg>
      </div>
    )
  }

  /**
   * Todos:
   * keep order when change and change again
   * maybe use size key to keep the order?
   * or a mark to keep it
   */

  updateView = (rootData: Object, diameter: number): Array<Object> => {
    const treeArr = this.calcPos(this.patchHeight(this.flatTree(rootData)))
    return treeArr
  }

  sortByDepth = (arr: Array<Object>): Array<Object> => {
    return arr.sort((a, b) => a.depth - b.depth)
  }

  calcPos = (treeArr: Array<Object>): Array<Object> => {
    let { height } = treeArr.sort((a, b) => a.depth - b.depth)[0]
    const result = []
    while (height >= 0) {
      const arr = treeArr.filter(n => n.height === height)
      const scaleRadian = this.averageRadian(arr.length - 1)
      Array.prototype.push.apply(result, arr.map((n, i) => ({
        ...n,
        x: scaleRadian(i),
        y: 200 * n.depth // {distance} tmp value 200
      })))

      height--
    }

    return this.sortByDepth(result)
  }

  averageRadian = (max: number) => (index:number): number => {
    const scaleRadian = d3.scaleLinear().range([0, 180]).domain([0, max])
    return scaleRadian(index)
  }

  patchHeight = (treeArr: Array<Object>): Array<Object> => {
    const target = treeArr.sort((a, b) => b.depth - a.depth)[0]
    const height = target ? target.depth : 0

    const result = treeArr.map(n => ({
      ...n,
      height: height - n.depth
    }))

    return this.sortByDepth(result)
  }

  flatTree = (data: Object): Array<Object> => {
    const result = []
    const stack = [{
      ...data,
      depth: 0
    }]

    while (stack.length) {
      const node = stack.pop()
      result.push(node)
      const children = node.children
      if (Array.isArray(children) && children.length) {
        Array.prototype.push.apply(stack, children.map(n => ({
          ...n,
          depth: node.depth + 1
        })))
      }
    }

    return this.sortByDepth(result)   
  }


  updateView1 = (rootData: Object, diameter: number): Array<Object> => {
    
    const root = d3.hierarchy(rootData)
    .sum(d => d.size)
    // .sort((a, b) => b.size - a.size)
    
    const tree = d3.tree()
    .size([180, diameter - margin])

    const nodes = tree(root).descendants()

    const rScale = d3.scaleLinear()
    .domain([nodes[0].value, 0])
    .range([80, 20])

    return nodes.map(node => ({
      ...node,
      r: rScale(node.value)
    }))
  }

  handleClick = (n: Object) => (e: Event) => {
    e && e.preventDefault()
    this.props.setFocus(n.uid)
  }
}
