import React from 'react'
import DewTabs from 'dew-tabs'
import System from './System'
import One from '#/components/Condition/One'
import Two from '#/components/Condition/Two'
import Three from '#/components/Condition/Three'
import SingleAssetSuggestion from '#/components/Chart/Analysis/SingleAssetSuggestion'
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
        {
          this.state.activeIndex === 0
          ? <System />
          : null
        }
        <SingleAssetSuggestion />
        <div>
          <div className="lead m-b-1">建议购买MSA条件</div>
          <div>（满足任何一个条件都建议购买）</div>
          <One />
          <Two />
          <Three />
        </div>
      </div>
    )
  }
}
