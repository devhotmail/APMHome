// @flow
import React from 'react'
import { connect } from 'dva'
import moment from 'moment'
import FilterBar from 'dew-filterbar'
import StatusBar from 'dew-statusbar'
import { Motion, spring } from 'react-motion'
import Chart from '#/components/Chart'

import styles from './index.scss'

const isHead = JSON.parse(document.querySelector('#user-context #isHead').value)

export default
@connect(({filters, depts, assets, steps }) => ({filters, depts, assets, steps}))
class ScanDetails extends React.PureComponent {
  onFilterChange = (e) => this.props.dispatch({
    type: 'filters/field/set',
    payload: e
  })

  render() {
    const { filters, depts, assets, steps} = this.props
    const filterOptions = [
      { type: 'range', key: 'range', value: filters.range }
    ]
    if (isHead) filterOptions.push({
      type: 'select',
        key: 'dept',
        value: filters.dept,
        options: depts,
        placeholder: '全部科室'
    })
    return (
      <div className={styles['scan-details']}>
        <FilterBar options={filterOptions} onChange={this.onFilterChange}/>
        <Chart />
        {
          depts.loading || assets.loading || steps.loading
          ?
          <Motion
            defaultStyle={{opacity: 0}}
            style={{opacity: spring(1)}}
          >
            {
              ({opacity}) => (
                <div className={styles['loading-container']} style={{opacity}}>
                  <StatusBar className={styles['loading']} type="loading"/>
                </div>
              )
            }
          </Motion>
          : null
        }
      </div>
    )
  }
}
