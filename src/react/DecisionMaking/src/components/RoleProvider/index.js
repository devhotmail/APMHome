/* @flow */
import React, { PureComponent } from 'react'
import { connect } from 'dva'

const RoleProvider = WrappedComponent => connect()(
  class extends PureComponent {
    componentDidMount () {
      const userInfoEl = document.querySelector('#user-context')
      if (userInfoEl) {
        const children = Array.from(userInfoEl.children)
        const userInfo = children.reduce((prev, cur) => {
          try {
            prev[cur.id] = JSON.parse(cur.value)
          } catch (err) {
            prev[cur.id] = cur.value
          }
          return prev
        }, {})
        this.props.dispatch({
          type: 'user/info/set',
          payload: userInfo
        })
      }
    }

    render () {
      return (
        <WrappedComponent {...this.props} />
      )
    }
  }
)

export default RoleProvider