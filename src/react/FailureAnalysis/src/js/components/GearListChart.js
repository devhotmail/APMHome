// @flow

import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { TransitionMotion, spring, presets } from 'react-motion'
import autobind from 'autobind-decorator'
import Radium from 'radium'
import classnames from 'classnames'
import Tooth from 'components/Tooth'
import { AnnulusViewport, NormalizeAngleRange } from 'utils/math'

type GearListProps = {
  id: string,
  startAngle: number,
  endAngle: number,
  innerRadius: number,
  outerRadius: number,
  margin: number,
  limit: number,
  clockwise: boolean,
  items: Array<Strip>
}

const Styles = {
  container: {
    display: 'inline-block',
    padding: '1em',
  },
  pointer: {
    cursor: 'pointer',
  }
}

/**
 * Use Polar Coordinate System with convention:
 * https://en.wikipedia.org/wiki/Polar_coordinate_system#/media/File:Polar_graph_paper.svg
 */
@autobind
@Radium
export default class GearListChart extends PureComponent<void, GearListProps, void> {

  static getToothParam(index, angle, margin, baseAngle) {
    let start = baseAngle + index * angle
    let end = start + angle - margin
    return [ start, end ]
  }

  static getRegistrationName(evt) {
    return evt.dispatchConfig.registrationName || 
           evt.dispatchConfig.phasedRegistrationNames.bubbled
  }

  /** properties for animation */
  _defaultOffsetAngle = 0
  _springOffsetAngle = 360

  mouseEventHandler = (evt) => {
    let name = GearListChart.getRegistrationName(evt)
    this.props[name](evt)
    if (name === 'onClick') {
      let self = this.refs.chart
      let teeth = self.querySelectorAll('.tooth')
      let focusedTooth = self.querySelector('.tooth.focused')
      if (focusedTooth && focusedTooth.contains(evt.target) && this.state.childFocused) {
        this.setState({ childFocused: false })
      } else {
        this.setState({ childFocused: true })
      }
      teeth.forEach(t => t.classList.remove('focused'))
    }
  }
  /** willEnter for react-motion */
  motionWillEnter = () => ({ offsetAngle: this._defaultOffsetAngle })

  /** willLeave for react-motion */
  motionWillLeave = () => ({ offsetAngle: this._springOffsetAngle })

  /** Clear focus status if need to */
  clearFocus = () => { 
    let focused = this.refs.chart.querySelector('.tooth.focused')
    focused && focused.classList.remove('focused')
    this.setState({ childFocused: false })
  }

  render() {
    let { id, innerRadius, outerRadius, items, margin, limit, startAngle, endAngle, clockwise, className, style,
      onMouseMove, onMouseEnter, onMouseLeave, onMouseOver, onClick } = this.props
    let { childFocused } = this.state
    let [ _startAngle, _endAngle ] = NormalizeAngleRange(startAngle, endAngle)
    let [ width, height, cx, cy ] = AnnulusViewport(startAngle, endAngle, outerRadius, innerRadius, 10)
    let _perItemAngle = (_endAngle - _startAngle) / items.length
    if (_perItemAngle > limit) _perItemAngle = limit
    _startAngle = _startAngle + margin / 2 /* shift half of the margin to centerize teeth */
    if (!clockwise) {
      this._defaultOffsetAngle = 360
      this._springOffsetAngle = 0
    }
    return (
      <div id={id} ref="chart" className={classnames('gear-list-chart', className, childFocused ? 'child-focused' : '')} 
        style={[Styles.container, style]}>

        <svg width={width} height={height}>
          <TransitionMotion
            willEnter={this.motionWillEnter}
            willLeave={this.motionWillLeave}
            defaultStyles={items.map((item, i) => ({
              key: item.id || String(i),
              data: item,
              style: {
                offsetAngle: this._defaultOffsetAngle
              }
            }))}
            styles={items.map((item, i) => ({
              key: item.id || String(i),
              data: item,
              style: {
                offsetAngle: spring(this._springOffsetAngle, presets.wobbly)
              }
            }))}
          >
            { interpolated => 
                (<g>{
                  interpolated.map((conf, i) => {
                    let item = conf.data
                    let [ start, end ] = GearListChart.getToothParam(i, _perItemAngle , margin, _startAngle)
                    return (<Tooth
                      key={conf.key || i}
                      style={onClick && {cursor:'pointer'}}
                      startAngle={start} 
                      endAngle={end} 
                      offsetAngle={conf.style.offsetAngle}
                      cx={cx} 
                      cy={cy}
                      outerRadius={outerRadius}
                      innerRadius={innerRadius}
                      mode={item.mode}
                      label={item.label}
                      strips={item.strips}
                      onMouseMove={onMouseMove && this.mouseEventHandler}
                      onMouseLeave={onMouseLeave && this.mouseEventHandler}
                      onMouseEnter={onMouseEnter && this.mouseEventHandler}
                      onMouseOver={onMouseOver && this.mouseEventHandler}
                      onClick={onClick && this.mouseEventHandler}
                    />)
                  })
                }</g>)
            }
          </TransitionMotion>
        </svg>
      </div>
    )
  }
}

GearListChart.defaultProps = {
  limit: 90,
  startAngle: 0,
  endAngle: 0,
  margin: 0,
  clockwise: true
}

GearListChart.propTypes = {
  startAngle: PropTypes.number.isRequired,
  endAngle: PropTypes.number.isRequired,
  innerRadius: PropTypes.number.isRequired,
  outerRadius: PropTypes.number.isRequired,
  limit: PropTypes.number,
}


