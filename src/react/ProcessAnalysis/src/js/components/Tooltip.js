// @flow

import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import autobind from 'autobind-decorator'
import _ from 'lodash'
import { JiuGongGe as JGG } from 'utils/helpers'
import { warn } from 'utils/logger'

type Props = {
  mouseX: number,
  mouseY: number,
  offsetX: number,
  offsetY: number,
  component: Component,
  anchor: string
}

type State = {
  offsetX: number,
  offsetY: number,
}

const styles = {
  wrapper: {
    position: 'fixed',
    pointerEvents: 'none',
    zIndex: 861112,
    textAlign: 'center'
  }
}

@autobind
@Radium
export default class Tooltip extends PureComponent<void, Props, State> {

  static getPosition(mouseX, mouseY, offsetX, offsetY) {
    return { left: mouseX + offsetX - window.scrollX, top: mouseY + offsetY - window.scrollY }
  }

  static AnchorValueMap = { L: 0, R: -1, HC: -.5, T: 0, B: -1, VC: -.5 }

  state = {
    _offsetX: 0,
    _offsetY: 0,
  }

  getAnchor = _.memoize(str => JGG.parse(str, { valueMap: Tooltip.AnchorValueMap }))

  render() {
    let { mouseX, mouseY, offsetX, offsetY } = this.props
    let { scrollX, scrollY } = window
    let { _offsetX, _offsetY } = this.state
    let style = Tooltip.getPosition(mouseX, mouseY, offsetX + _offsetX + scrollX, offsetY + _offsetY + scrollY)
    return (<div ref="wrapper" style={[styles.wrapper, style]} className="tooltip-wrapper">
      {this.renderContent()}
    </div>)
  }

  renderContent() {
    let { component, children } = this.props
    if (component) {
      if (children) warn('Tooltip: "children" is ignored when "component" is given')
      return component()
    } else {
      return children 
    }
  }

  componentDidUpdate() {
    /* adjust content offset by its actual width/height */
    let [ hAnchor, vAnchor ] = this.getAnchor(this.props.anchor)
    let wrapper = this.refs.wrapper
    let offsetX = Math.round(wrapper.offsetWidth * hAnchor)
    let offsetY = Math.round(wrapper.offsetHeight * vAnchor)
    if (this.state.offsetX !== offsetX && this.state.offsetY !== offsetY ) {
      this.setState({ _offsetX: offsetX, _offsetY: offsetY })
    }
  }
}

Tooltip.defaultProps = {
  offsetX: 0,
  offsetY: 0,
  anchor: 'hcb'
}

Tooltip.propTypes = {
  mouseX: PropTypes.number.isRequired,
  mouseY: PropTypes.number.isRequired,
}
