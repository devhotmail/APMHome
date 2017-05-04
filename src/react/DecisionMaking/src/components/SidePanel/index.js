import React, { Component } from 'react'
import { Tabs } from 'antd'

import SystemPanel from './SystemPanel'
import ManualPanel from './ManualPanel'

import styles from './styles.scss'

const TabPane = Tabs.TabPane

function callback (key) {
  console.log(key)
}

export default class SidePanel extends Component {
  state = {
    panel: 'system'
  }

  render () {
    const { focus } = this.props
    return (
      <div className="flex flex--justify-content--center">
        {/*<div class="type-select btn-group">
          <template is="dom-repeat" items="[[typeOpts]]">
            <div class$="[[calcTabCls(item.val, selectedType)]]"
              on-click="handleSelect">
              [[item.key]]
            </div>
          </template>
        </div>*/}
        {
          focus
            ? <Tabs defaultActiveKey="2" onChange={callback}>
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
