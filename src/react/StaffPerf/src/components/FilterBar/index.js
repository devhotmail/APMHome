/* @flow */
import React, { Component } from 'react'
import { Menu, Dropdown, Button, Icon, DatePicker, Form } from 'antd'
import { connect } from 'dva'
import { routerRedux } from 'dva/router'
import moment from 'moment'

import { getRangePresets } from '#/utils'
import { dateFormat, disabledDate } from '#/constants'

import styles from './styles.scss'

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

@Form.create()
@connect()
export default class FilterBar extends Component {
  render () {
    const { from, to } = this.props.location.query

    const defaultValue= [
      moment(from, dateFormat),
      moment(to, dateFormat)
    ]

    return (
      <div className="p-a-1">
        <Form>
          <Form.Item>
            {
              this.props.form.getFieldDecorator('range', {
                initialValue: defaultValue,
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
      </div>
    )
  }

  handleOk = (e: Event) => {
    const [from, to] = this.props.form.getFieldValue('range')

    this.handlePush({
      from: from.format(dateFormat),
      to: to.format(dateFormat)
    })
  }

  handlePush = (newQuery: Object) => {
    const { location, dispatch } = this.props

    dispatch(routerRedux.push({
      pathname: '/',
      query: {
        ...location.query,
        ...newQuery
      }
    }))
  }
}
