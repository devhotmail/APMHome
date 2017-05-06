/* @flow */
import React, { Component } from 'react'
import { Tooltip, Table } from 'antd'

import { getCursor, isSameCursor, isFocusNode, round } from '#/utils'

import type { ConfigT, NodeT, cursorT } from '#/types'

import EditBlock from '#/components/EditBlock'

import styles from './styles.scss'

const defaultTableProps = {
  size: 'small',
  bordered: true,
  pagination: false,
  scroll: { y: 160 },
  rowKey: n => n.data.id
}

export default class ConfigDept extends Component<*, ConfigT, *> {
  render () {
    const { config, focus: { cursor}, depths, setFocus } = this.props

    const configListOne = config.filter(n => n.depth === depths[0])
    .map(n => ({
      ...n,
      children: null
    }))

    if (!configListOne.length) return null

    const activeCursors = this.getParentCursors()

    const tableProps = {
      ...defaultTableProps,
      onRowClick: node => setFocus(getCursor(node)),
      rowClassName: node => `${styles.tr} ${isFocusNode(node, activeCursors[0]) ? 'active': ''}`
    }

    return (
      <div>
        <Table dataSource={configListOne} {...tableProps}>
          <Table.Column
            title="科室名称"
            dataIndex="data.name"
            key="name" />
          <Table.Column
            title="预期增长"
            dataIndex="data.usage_predict_increase"
            key="increase"
            width={120}
            render={(text, node, index) => (
              <div>
                {round(text * 100, 1)}%
              </div>
            )} />
        </Table>
        { activeCursors[0] ? this.renderConfigTwo(activeCursors) : null}
      </div>
    )
  }

  renderConfigTwo = (activeCursors) => {
    const { config, focus: { cursor}, depths, setFocus } = this.props

    const configListTwo = config.filter(n => n.depth === depths[1])
    .filter(n => isFocusNode(n.parent, activeCursors[0]))
    .map(n => ({
      ...n,
      children: null
    }))

    const tableProps = {
      ...defaultTableProps,
      onRowClick: node => setFocus(getCursor(node)),
      rowClassName: node => `${styles.tr} ${isFocusNode(node, activeCursors[1]) ? 'active': ''}`
    }

    const thresholdNode = [
      <div>
        <i className="dewicon-circle-full" style={{color:'#e26d26'}}></i>
        <span>满负荷</span>
      </div>,
      <div>
        <i className="dewicon-circle-low" style={{color:'#ebda51'}}></i>
        <span>低负荷</span>
      </div>
    ]

    return (
      <div>
        <div className="text-center">
          <div className={styles.arrow}></div>
        </div>
        <Table dataSource={configListTwo} {...tableProps}>
          <Table.Column
            title="设备类型"
            dataIndex="data.name"
            key="name" />
          <Table.Column
            title="预期增长"
            dataIndex="data.usage_predict_increase"
            key="increase"
            width={70}
            render={(text, node, index) =>
              <EditBlock
                cursor={getCursor(node)}
                fieldKey="increase"
                val={round(text * 100, 1)} />              
            } />
          <Table.Column
            title={thresholdNode[0]}
            dataIndex="data.usage_threshold[1]"
            key="max"
            width={70}
            render={(text, node, index) =>
              <EditBlock
                cursor={getCursor(node)}
                fieldKey="max"
                val={text * 100} />             
            } />
          <Table.Column
            title={thresholdNode[1]}
            dataIndex="data.usage_threshold[0]"
            key="min"
            width={70}
            render={(text, node, index) =>
              <EditBlock
                cursor={getCursor(node)}
                fieldKey="min"
                val={text * 100} />
            } />
        </Table>
      </div>
    )
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
