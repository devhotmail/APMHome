// @flow
import React from 'react'
import * as Immutable from 'immutable'
import { range } from 'lodash'
import type { Map, IndexedIterable, List } from 'immutable'
import { TransitionMotion, spring } from 'react-motion'
import uuid from 'uuid/v4'
import AnnulusSectorGroup from '#/components/AnnulusSectorGroup'
import ImmutableComponent from '#/components/ImmutableComponent'

type Props = {
  onMouseEnter?: Function,
  onMouseLeave?: Function,
  cx: number,
  cy: number,
  innerRadius: number,
  startAngle: number,
  endAngle: number,
  groups: List<*>,
  curPage: number,
  pageSize: number,
  onPageChange?: number => void,
  onGroupClick?: * => void,
  showPrev?: boolean,
  showNext?: boolean
}

type Position = {
  startAngle: number,
  endAngle: number
}

const MAX_GROUP_ANGLE = 10 * Math.PI / 180

function distribute(startAngle: number, endAngle: number, count: number): Position[]  {
  const span = endAngle - startAngle
  if (Math.abs(span) / count <= MAX_GROUP_ANGLE) {
    return range(count).map((_, index) => ({
      startAngle: startAngle + index * span / count,
      endAngle: startAngle + (index + 1) * span / count
    }))
  } else {
    const sign = span > 0 ? 1 : -1
    const padding = (Math.abs(span) - count * MAX_GROUP_ANGLE) / (count + 1)
    return range(count).map((_, index) => ({
      startAngle: startAngle + ((index + 1) * padding + index * MAX_GROUP_ANGLE) * sign,
      endAngle: startAngle + (index + 1) * (padding + MAX_GROUP_ANGLE) * sign
    }))
  }
}

export default
class AnnulusRing extends ImmutableComponent<*, Props, *> {
  generateArrows(startAngle, endAngle, innerRadius) {
    const sign1 = startAngle >= 0 ? -1: 1
    const sign2 = endAngle >= 0 ? -1: 1
    const d1 = `M${sign1 * 10},${-20}L${sign1 * 30},${-10}L${sign1 * 10},${0}`
    const d2 = `M${sign2 * 10},${0}L${sign2 * 30},${10}L${sign2 * 10},${20}`
    return (
      <g>
        {
          this.props.showPrev
          ?
          <path
            onClick={e => this.props.onPageChange(-1)}
            fill="#cecece"
            d={d1}
            transform={`translate(${innerRadius * Math.sin(startAngle)}, ${-innerRadius * Math.cos(startAngle)}) rotate(${startAngle * 180 / Math.PI})`}
          />
          : null
        }
        {
          this.props.showNext
          ?
          <path
            onClick={e => this.props.onPageChange(1)}
            fill="#cecece"
            d={d2}
            transform={`translate(${innerRadius * Math.sin(endAngle)}, ${-innerRadius * Math.cos(endAngle)}) rotate(${endAngle * 180 / Math.PI + (endAngle > 0 ? 1 : -1) * 180})`}
          />
          : null
        }
      </g>
    )
  }
  componentWillReceiveProps(nextProps, nextState) {
    this.leaveDirection = nextProps.curPage > this.props.curPage ? 1 : -1
  }

  onMouseEnter = data => rect => this.props.onMouseEnter && this.props.onMouseEnter(rect, data)
  onMouseLeave = data => e => this.props.onMouseLeave && this.props.onMouseLeave()

  render() {
    const { cx, cy, innerRadius, startAngle, endAngle, groups, curPage, pageSize, showNext, showPrev } = this.props
    const angles = distribute(startAngle, endAngle, groups.size)
    const id = uuid()
    // console.log(path().arc(0, 0, 10000, startAngle, endAngle));
    const sign = endAngle - startAngle > 0 ? 1 : 0
    const d = `M0,0L${500 * Math.sin(startAngle)},${-500 * Math.cos(startAngle)}A500,500,0,${sign},${sign},${500 * Math.sin(endAngle)},${-500 * Math.cos(endAngle)}`

    return (
      <g transform={`translate(${cx}, ${cy})`}>
        <defs>
          <clipPath id={id}>
            <path d={d} />
          </clipPath>
        </defs>
        {
          curPage === undefined || pageSize === undefined
          ?
          <g clipPath={`url(#${id})`}>
            {groups.map((group, index) => (
              <AnnulusSectorGroup
                opacity={group.get('opacity')}
                onMouseEnter={this.onMouseEnter(group)}
                onMouseLeave={this.onMouseLeave(group)}
                onClick={e => this.props.onGroupClick(group)}
                key={group.get('id', index)}
                innerRadius={innerRadius}
                text={group.get('text')}
                {...angles[index]}
                annuluses={group.get('annuluses')}
              />
            ))}
          </g>
          :
          <TransitionMotion
            defaultStyles={groups.map((group, index) => ({
              key: '' + group.get('id', index),
              data: group,
              style: {
                startAngle: angles[index].startAngle,
                endAngle: angles[index].endAngle
              }
            })).toJS()}
            styles={groups.map((group, index) => ({
              key: '' + group.get('id', index),
              data: group,
              style: {
                startAngle: spring(angles[index].startAngle),
                endAngle: spring(angles[index].endAngle),
                opacity: spring(1)
              }
            })).toJS()}
            willLeave={() => ({
              startAngle: spring(startAngle - (endAngle - startAngle) * this.leaveDirection),
              endAngle: spring(endAngle - (endAngle - startAngle) * this.leaveDirection),
              opacity: spring(0)
            })}
            willEnter={() => ({
              startAngle: startAngle + (endAngle - startAngle) * this.leaveDirection,
              endAngle: endAngle + (endAngle - startAngle) * this.leaveDirection,
              opacity: 0
            })}
          >
            {interpolatedStyles => (
              <g clipPath={`url(#${id})`}>
                {
                  interpolatedStyles.map((config => (
                    <AnnulusSectorGroup
                      opacity={config.style.opacity}
                      key={config.key}
                      onMouseEnter={this.onMouseEnter(config.data)}
                      onMouseLeave={this.onMouseLeave(config.data)}
                      onClick={e => this.props.onGroupClick && this.props.onGroupClick(config.data)}
                      innerRadius={innerRadius}
                      text={config.data.get('text')}
                      extraText={config.data.get('extraText')}
                      startAngle={config.style.startAngle}
                      endAngle={config.style.endAngle}
                      annuluses={config.data.get('annuluses')}
                    />
                  )))
                }
              </g>
            )}
          </TransitionMotion>
        }
        {this.generateArrows(startAngle, endAngle, innerRadius)}
        {/* {
          curPage === undefined || pageSize === undefined
          ?
          null
          :
          this.generateArrows(startAngle, endAngle, innerRadius)
        } */}
      </g>
    )
  }
}
