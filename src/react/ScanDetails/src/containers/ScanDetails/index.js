// @flow
import React from 'react'
import ImmutableComponent from '#/components/ImmutableComponent'
import BodyChart from '#/components/BodyChart'
import type { Map } from 'immutable'
import { connect } from 'dva'
import { Select } from 'antd'
import PxRangepicker from '../../../public/bower_components/px-rangepicker/px-rangepicker.html'
import { PAGE_SIZE } from '#/constants'
import styles from './index.scss'

const isHead = JSON.parse(document.querySelector('#user-context #isHead').value)

class ScanDetails extends ImmutableComponent<void, {scans: Map<string, any>}, void> {
  onDatetimeChange = e => {
    const from = e.detail.range.from.split('T')[0]
    const to = e.detail.range.to.split('T')[0]
    this.props.dispatch({
      type: 'scans/range/change',
      payload: {
        from,
        to
      }
    })
  }

  rangePicker = (
    <PxRangepicker
      hideTime
      showButtons
      blockFutureDates
      dateFormat="YYYY/MM/DD"
      showTimeZone="none"
      range={{
        from: this.props.scans.getIn(['range', 'from']),
        to: this.props.scans.getIn(['range', 'to'])
      }}
      onPxDatetimeRangeSubmitted={this.onDatetimeChange}
    />
  )
  render() {
    const { scans } = this.props
    return (
      <div className={styles['scan-details']}>
        {this.rangePicker}
        {
          isHead
          ?
          <Select
            className={styles['select']}
            dropdownMatchSelectWidth={false}
            style={{
              position: 'absolute',
              right: 180,
              cursor: 'pointer',
              width: 100
            }}
            showSearch
            allowClear
            placeholder="全部科室"
            optionFilterProp="children"
            onChange={value => this.props.dispatch({
              type: 'scans/dept/set',
              payload: value
            })}
            // value={this.getActiveFilterValue(column)}
            // filterOption={(input, option) => option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0}
          >
            {
              scans.get('depts').map(dept => (
                <Select.Option key={dept.get('id')} value={dept.get('id')}>{dept.get('name')}</Select.Option>
              ))
            }
          </Select>
          :null
        }
        <BodyChart className={styles['body-chart']} scans={scans} dispatch={this.props.dispatch} />
      </div>
    )
  }
}

export default
connect(({scans}) => ({scans}))(ScanDetails)
