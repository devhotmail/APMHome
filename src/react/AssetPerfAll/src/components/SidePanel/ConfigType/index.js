/* @flow */
import React, { Component } from 'react'
import { Tooltip, Table } from 'antd'
import uuid from 'uuid/v4'

import { round } from '#/utils'

import type { ConfigT, NodeT, cursorT } from '#/types'

import EditBlock from '#/components/EditBlock'

import styles from './styles.scss'

const defaultTableProps = {
  size: 'small',
  bordered: true,
  pagination: false,
  scroll: { y: 174 },
  rowKey: n => n.id
}

export default class ConfigType extends Component<*, ConfigT, *> {
  render () {
    // const { config, focus: { cursor}, depths, setFocus } = this.props
    //
    // const configListOne = config.filter(n => n.depth === depths[0])
    // .map(n => ({
    //   ...n,
    //   children: null
    // }))
    //
    // if (!configListOne.length) return null
    //
    // const activeCursors = this.getParentCursors()

    const { config } = this.props

    const tableProps = {
      ...defaultTableProps,
      // onRowClick: node => setFocus(getCursor(node)),
      // rowClassName: node => `${styles.tr} ${isFocusNode(node, activeCursors[0]) ? 'active': ''}`
    }

    // const thresholdNode = [
    //   <div>
    //     <Tooltip placement="topRight" title="您可以自定义使用率大于多少为满负荷">
    //       <i className="dewicon dewicon-circle-full"></i>
    //       <span>满负荷</span>
    //     </Tooltip>
    //   </div>,
    //   <div>
    //     <Tooltip placement="topRight" title="您可以自定义使用率小于多少为低负荷">
    //       <i className="dewicon dewicon-circle-low"></i>
    //       <span>低负荷</span>
    //     </Tooltip>
    //   </div>
    // ]

    return (
      <div>
        <Table dataSource={config} {...tableProps}>
          <Table.Column
            title="设备类型"
            dataIndex="name"
            key="name" />
          <Table.Column
            title="收入"
            dataIndex="revenue_increase"
            key="revenue_increase"
            width={70}
            render={(text, node, index) =>
              <EditBlock
                cursor={node}
                fieldKey="revenue_increase"
                val={round(text * 100, 1)} />
            } />
          <Table.Column
            title="维护成本"
            dataIndex="cost_increase"
            key="cost_increase"
            width={70}
            render={(text, node, index) =>
              <EditBlock
                cursor={node}
                fieldKey="cost_increase"
                val={round(text * 100, 1)} />
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
