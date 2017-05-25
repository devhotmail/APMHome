import React, { Component } from 'react'
import One from '#/components/Condition/One'
import Two from '#/components/Condition/Two'
import Three from '#/components/Condition/Three'

class Root extends Component {
  render () {
    return (
      <div style={{width: 400, margin: '0 auto'}}>
        root
        <One />
        <Two />
        <Three />
      </div>
    )
  }
}

export default Root