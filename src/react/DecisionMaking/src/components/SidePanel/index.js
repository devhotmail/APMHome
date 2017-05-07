import React, { Component } from 'react'
import { Transition, Animate } from 'react-move'

import SystemPanel from './SystemPanel'
import ManualPanel from './ManualPanel'

import styles from './styles.scss'

const panelOpts = [
  {
    key: 'system',
    text: '系统自动'
  },
  {
    key: 'manual',
    text: '手动调节'
  }
]

const tabWidth = 90

export default class SidePanel extends Component {
  state = {
    activeTab: 0
  }

  render () {
    const { focus } = this.props
    const { activeTab } = this.state
    
    return (
      <div className={styles.sidepanel}>
        <div className={styles.tabs}>
          <div style={{width: tabWidth, left: activeTab * tabWidth}} className={styles.sliding}></div>
          <ul style={{width: tabWidth * 2}}>
            {
              panelOpts.map(({key, text}, index) => {
                return (
                  <li
                    key={key}
                    className={activeTab === index ? styles.active : ''}
                    onClick={this.handleTabClick(index)}>
                    {text}
                  </li>
                )
              })
            }
          </ul>
        </div>
        <div className={styles.tabContent}>
          {
            activeTab === 0
              ? <Animate
                default={{translate: 100}}
                data={{translate: 0}}
                duration={300}
                easing='easeCubic'>
                {data => (
                  <div
                    style={{
                      transform: `translateX(${data.translate}%)`
                    }}
                  >
                  { 
                    focus.node && focus.node.data
                      ? <SystemPanel data={focus.node.data} />
                      : null
                  }
                  </div>
                )}
              </Animate>
            : null
          }
          {
            activeTab === 1
              ? <Animate
                default={{translate: -100}}
                data={{translate: 0}}
                duration={300}
                easing='easeCubic'>
                {data => (
                  <div
                    style={{
                      transform: `translateX(${data.translate}%)`
                    }}
                  >
                    <ManualPanel {...this.props} />
                  </div>
                )}
              </Animate>
            : null
          }
        </div>
      </div>
    )
  }

  handleTabClick = (index: number) => (e: Event) => {
    this.props.dispatch({
      type: 'finance/data/toggle',
      payload: ['system', 'manual'][index]
    })

    this.setState({
      activeTab: index
    })
  }
}
