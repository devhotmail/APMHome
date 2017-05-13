// @flow
import React from 'react'
import ImmutableComponent from '#/components/ImmutableComponent'
import BodyChart from '#/components/BodyChart'
import type { Map } from 'immutable'
import { connect } from 'dva'
import { Select, DatePicker, Form } from 'antd'
import moment from 'moment'

import { dateFormat, now, PAGE_SIZE, disabledDate } from '#/constants'
import { getRangePresets } from '#/utils'

import styles from './index.scss'

const { RangePicker } = DatePicker

const presets = getRangePresets([
  'oneWeek', 'oneMonth', 'oneYear', 'currentMonth',
  'yearBeforeLast', 'lastYear'
])

const ranges = presets.reduce((prev, cur) => {
  prev[cur.text] = [
    cur.start,
    cur.end
  ]
  return prev
}, {})

const defaultRange = [
  moment(now).subtract(1, 'year'),
  moment(now)
]

const isHead = JSON.parse(document.querySelector('#user-context #isHead').value)

class ScanDetails extends ImmutableComponent<void, {scans: Map<string, any>}, void> {
  onDatetimeChange = e => {
    const from = e.detail.range.from.split('T')[0]
    const to = e.detail.range.to.split('T')[0]

  }

  handleOk = (e: Event) => {
    const [from, to] = this.props.form.getFieldValue('range')

    this.props.dispatch({
      type: 'scans/range/change',
      payload: {
        from: from.format(dateFormat),
        to: to.format(dateFormat)
      }
    })
  }

  render() {
    const { scans, form } = this.props
    return (
      <div className={styles['scan-details']}>
        <Form>
          <Form.Item>
            {
              form.getFieldDecorator('range', {
                initialValue: defaultRange,
                rules: [
                  { type: 'array', required: true, message: '请选择时间' }
                ]
              })(
                <RangePicker
                  showTime
                  disabledDate={disabledDate}
                  format={dateFormat}
                  ranges={ranges}
                  onOk={this.handleOk} />
              )
            }
          </Form.Item>
        </Form>
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
          : null
        }
        <BodyChart className={styles['body-chart']} scans={scans} dispatch={this.props.dispatch} />
      </div>
    )
  }
}

export default
connect(({scans}) => ({scans}))(Form.create()(ScanDetails))
