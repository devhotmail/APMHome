/* @flow */
import React, { Component } from 'react'
import { Menu, Dropdown, Button, Icon, Radio, Select, DatePicker, Form } from 'antd'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'
import moment from 'moment'
import { getRangePresets } from '#/utils'

import { dateFormat, now, PAGE_SIZE, disabledDate } from '#/constants'


import styles from './styles.scss'

const defaultRange = [
  moment(now).subtract(1, 'year'),
  moment(now)
]


const { RangePicker } = DatePicker

const presets = getRangePresets([
  'oneWeek', 'oneMonth', 'oneYear', 'currentMonth',
  'yearBeforeLast', 'lastYear', 'currentYear'
])

const ranges = presets.reduce((prev, cur) => {
  prev[cur.text] = [
    cur.start,
    cur.end
  ]
  return prev
}, {})


export default
@connect(state => ({
  user: state.user.info,
  filters: state.filters
}))
@Form.create()
class FilterBar extends Component {
  handleOk = ([from, to]) => {
    this.props.dispatch({
      type: 'filters/range/set',
      payload: {
        from: from.format(dateFormat),
        to: to.format(dateFormat)
      }
    })
  }

  handleGroupbyClick = key => e => this.props.dispatch({
    type: 'filters/groupBy/set',
    payload: key
  })

  renderGroupBy = () => {
    const groupbyOpts = [
      {
        key: 'type',
        text: '按设备类型',
        onClick: this.handleGroupbyClick('type')
      },
      {
        key: 'month',
        text: '按月份',
        onClick: this.handleGroupbyClick('month')
      }
    ]

    if (this.props.user.isHead) groupbyOpts.unshift({
      key: 'dept',
      text: '按科室',
      onClick: this.handleGroupbyClick('dept')
    })

    return <div className={styles['group-by']}>
      {
        groupbyOpts.map(({key, text, onClick}) => {
          return (
            <Button
              key={key}
              className="m-l-1"
              type={this.props.filters.groupBy === key ? 'primary' : ''}
              onClick={onClick}>
              {text}
            </Button>
          )
        })
      }
    </div>
  }

  render () {
    const { user: { isHead }, location: { query }, className, filters, form } = this.props

    return (
      <div className={`${className} ${styles['filter-bar']}`}>
        <Radio.Group
          onChange={e => this.props.dispatch({
            type: 'filters/type/set',
            payload: e.target.value
          })}
          className={styles['radio-group']}
          value={filters.type}
          size="large"
        >
          <Radio.Button value="history">
            历史
          </Radio.Button>
          <Radio.Button value="future">
            预测
          </Radio.Button>
        </Radio.Group>
        {
          filters.type === 'history'
          ?
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
          :
          <Select
            className={styles['drop-down']}
            defaultValue={new Date().getFullYear()}
            size="large"
            onChange={year => this.props.dispatch({
              type: 'filters/range/set',
              payload: {
                from: `${year}-01-01`,
                to: `${year}-12-31`
              }
            })}
          >
            <Select.Option value={new Date().getFullYear()}>{new Date().getFullYear()}年预测</Select.Option>
            <Select.Option value={new Date().getFullYear() + 1}>{new Date().getFullYear() + 1}年预测</Select.Option>
          </Select>
        }
        {this.renderGroupBy()}
      </div>
    )
  }
}
