/* @flow */
import React, { Component } from 'react'
import Sizes from 'react-sizes'

type SizeParams = {
  width: number,
  height: number,
  diameter: number
}

function mapSizesToProps(sizes: Object): SizeParams {
  const {
    width = window.innerWidth,
    height = window.innerHeight
  } = sizes

  return {
    width: width,
    height: height,
    diameter: Math.min(width, height)
  }
}

function SizeProvider(WrappedComponent: Class<Component<*, *, *>>): Class<Component<*, *, *>> {
  return Sizes(mapSizesToProps)(WrappedComponent)
}

export default SizeProvider
