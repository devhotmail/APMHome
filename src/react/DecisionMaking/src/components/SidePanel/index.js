import React, { Component } from 'react'
import { Tabs } from 'antd'

import SystemPanel from './SystemPanel'
import ManualPanel from './ManualPanel'

const TabPane = Tabs.TabPane

function callback (key) {
  console.log(key)
}

export default class SidePanel extends Component {
  render () {
    const { focus } = this.props
    return (
      <div>
        {
          focus
            ? <Tabs defaultActiveKey="1" onChange={callback}>
              <TabPane tab="系统自动" key="1">
                <SystemPanel {...this.props} />
              </TabPane>
              <TabPane tab="手动调节" key="2">
                <ManualPanel {...this.props} />
              </TabPane>
            </Tabs>
            : null
        }
      </div>
    )
  }
}
