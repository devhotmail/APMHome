/* @flow */
import React, { Component } from 'react'
import { Modal } from 'antd'

import moment from 'moment'

import RangePicker from 'react-daterange-picker'

import 'react-daterange-picker/dist/css/react-calendar.css'

import styles from './styles.scss'

export default class DatePicker extends Component<*, PropsT, *> {
  state = {
    value: null,
    states: null,
    visible: false
  }

  render() {
    const { visible, value } = this.state
    return (
      <div className={styles.rangepicker}>
        <div>
          <input type="text"
            onClick={this.showModal}
            value={this.state.value ? this.state.value.start.format('LL') : ""}
            readOnly={true}
            placeholder="Start date" />
          <input type="text"
            onClick={this.showModal}
            value={this.state.value ? this.state.value.end.format('LL') : ""}
            readOnly={true}
            placeholder="End date" />
        </div>
        <Modal
          title=""
          closable={false}
          width={700}
          visible={visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}>
          <div className="flex flex--justify-content--center">
            <RangePicker
              numberOfCalendars={2}
              selectionType="range"
              singleDateRange={true}
              minimumDate={new Date()}
              onSelect={this.handleSelect}
              value={this.state.value} />
          </div>
        </Modal>        
      </div>
    )
  }

  showModal = () => {
    this.setState({
      visible: true
    })
  }

  handleOk = e => {
    this.setState({
      visible: false
    })
    console.log(this.state)
  }

  handleCancel = e => {
    this.setState({
      visible: false
    })
  }

  handleSelect = (value, states) => {
    this.setState({value, states})
  }
}
