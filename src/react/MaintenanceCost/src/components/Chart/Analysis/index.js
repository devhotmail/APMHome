import React from 'react'
import DewTabs from 'dew-tabs'
import styles from './index.scss'

export default
class Analysis extends React.Component {
  state = {
    activeIndex: 0
  }

  onTabChange = index => this.setState({
    activeIndex: index
  })

  render() {
    return (
      <div className={styles['analysis']}>
        <DewTabs
          activeIndex={this.state.activeIndex}
          options={[{key: 'system', text: '系统自动'}, {key: 'manual', text: '手工调节'}]}
          onChange={this.onTabChange}
        />
      </div>
    )
  }
}
