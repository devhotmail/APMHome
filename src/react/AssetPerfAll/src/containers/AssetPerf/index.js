import React from 'react'
import { connect } from 'dva'
import BubbleChart from '#/components/BubbleChart'
import SidePanel from '#/components/SidePanel'
import FilterBar from '#/components/FilterBar'
import styles from './index.scss'

class AssetPerf extends React.PureComponent {
  render() {
    const { profit, filters } = this.props
    const { data } = profit
    const { groupBy, data: filtersData } = filters
    if (data.length === 0) return null
    const chartData = data
      .reduceRight((prev, cur, index) => {
        const i = cur.items.findIndex(item => item.root.id === filtersData[data.length - 1 - index][groupBy])
        if (i >= 0) {
          return {
            ...cur,
            items: cur.items.slice(0, i).concat([{...prev, root: {...cur.items[i].root}}]).concat(cur.items.slice(i + 1, cur.items.length))
          }
        }
        return cur
      })
    return (
      <div className={styles['asset-perf']}>
        <FilterBar className={styles['filter-bar']} location={this.props.location}/>
        <BubbleChart data={chartData} depth={data.length}/>
        <SidePanel className={styles['side-panel']} />
      </div>
    )
  }
}

export default
connect(({ profit, filters }) => ({ profit, filters }))(AssetPerf)
