/* @flow */
import React, { Component } from 'react'

import styles from './styles.scss'

function debounce(func, wait = 500, immediate) {
  let timeout
  return function() {
    const args = arguments
    const later = () => {
      timeout = null
      if (!immediate) func.apply(this, args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

const SizeProvider = WrappedComponent => {
  return class extends Component {
    state = {
      width: 0,
      height: 0,
      diameter: 0
    }

    componentDidMount () {
      this.getSizes()
      window.addEventListener('resize', debounce(this.getSizes))
    }

    componentWillUnmount () {
      window.removeEventListener('resize', debounce(this.getSizes))
    }

    render () {
      return (
        <div ref={client => this.client = client} className={styles.sizeProvider}>
          <WrappedComponent {...this.state} {...this.props} />
        </div>
      )
    }

    getSizes = () => {
      const { width, height } = this.client.getBoundingClientRect()
      const diameter = Math.min(width, height)
      this.setState((state, props) => {
        return {
          ...state,
          width,
          height,
          diameter
        }
      })
    }
  }
}

export default SizeProvider
