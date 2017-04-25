import React, { Component } from 'react'
import queryStr from 'query-string'

function ChartWrapper(WrappedComponent) {
  return class extends Component {
    render() {
      const loadData = item => {
        console.log(item)
        const href = item.data.link.href
        const ref = item.data.link.ref
        if (ref === 'child' && href) {
          const query = queryStr.parse(href.split('?')[1])

          this.props.dispatch({
            type: 'financial/data/get',
            payload: query
          })
        }
      }

      return <WrappedComponent loadData={loadData} {...this.props} />
    }
  }
}

export default ChartWrapper
