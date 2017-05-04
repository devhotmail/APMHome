/* @flow */
import React, { Component } from 'react'

const RoleProvider = WrappedComponent => {
  return class extends Component {
    state = {
      userInfo: undefined
    }

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
        this.setState({ userInfo })
      }
    }

    render () {
      return (
        <WrappedComponent userInfo={this.state.userInfo} {...this.props} />
      )
    }
  }
}

export default RoleProvider
