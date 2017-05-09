import React, { Component } from 'react'
import { connect } from 'dva'
import { Form, Input, InputNumber } from 'antd'
import ClickOutside from 'react-click-outside'

import { debounce } from '#/utils'

import styles from './styles.scss'

@connect()
@Form.create()
export default class EditBlock extends Component {
  state = {
    disabled: true
  }

  render () {
    const { form, val, fieldKey } = this.props
    const { disabled } = this.state

    return (
      <div className={styles.editBlock} onClick={this.handleClick}>
        <Form>
          <Form.Item>
            <div className={styles.wrapper}>
              {form.getFieldDecorator(fieldKey, {
                initialValue: val
              })(
                <Input
                  onPressEnter={this.handleBlur}
                  onBlur={this.handleBlur}
                  disabled={disabled}
                  size="small" />
              )}
            </div>
          </Form.Item>
        </Form>
      </div>
    )
  }

  handleClick = (e) => {
    this.setState({
      disabled: false
    })
  }

  handleBlur = (e) => {
    this.setState({
      disabled: true
    }, () => {
      const { dispatch, cursor, val, fieldKey, form } = this.props
      const value = form.getFieldValue(fieldKey)
      if (value === val) return // dont dipatch changes when the val is the same 
      dispatch({
        type: 'config/changes',
        payload: {
          cursor,
          [fieldKey]: value / 100
        }
      })
    })
  }
}
