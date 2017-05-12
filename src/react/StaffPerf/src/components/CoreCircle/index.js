import React, { Component } from 'react'
import StarRate from '../StarRate'

import StatusBar from '../StatusBar'

import { round } from '#/utils'
import { ORDER, HOUR, RATE } from '#/constants'

import styles from './styles.scss'

type PropsT = {
  focus: Object,
  range: Object,
  onClick: Function
}

export default class CoreCircle extends Component<*, PropsT, *> {
  render () {
    const { loading } = this.props
    
    if (loading) return <StatusBar type="loading" />

    return (
      <div className={styles.coreCircle}>
        {this.renderContent()}
      </div>
    )
  }

  renderContent () {
    const { filter, focus, range, onClick } = this.props

    if (!focus || !range) return null
    
    return (
      <div className={styles.content}>
        <div className="m-b-1">{focus.owner_name}</div>
        <div className={styles.info}>
          <div className="m-b-1" style={{opacity: filter === HOUR ? 1 : 0.3}}>
            <div
              className="clickable flex flex--align-items--center"
              onClick={onClick(HOUR)}>
              <div className={styles.rect} style={{color: 'rgb(106,180,166)'}}></div>
              <div>
                <span>工作量</span>
                <span>&nbsp;{focus.man_hour} / {range.hour_total}</span>
                <span>&nbsp;(法定小时) = {round(focus.man_hour * 100 / range.hour_total)} %</span>
              </div>
            </div>
            <div className="m-l-2 flex flex--align-items--center">
              <div className={styles.rect} style={{color: 'rgb(106,180,166)'}}></div>
              <div>
                <span>维修</span>
                <span>&nbsp;{focus.repair} / {focus.man_hour}</span>
                <span>&nbsp;(工作小时) = {round(focus.repair * 100 / focus.man_hour)} %</span>
              </div>
            </div>
            <div className="m-l-2 flex flex--align-items--center">
              <div className={styles.rect} style={{color: 'rgb(123,190,178)'}}></div>
              <div>
                <span>保养</span>
                <span>&nbsp;{focus.maintenance} / {focus.man_hour}</span>
                <span>&nbsp;(工作小时) = {round(focus.maintenance * 100 / focus.man_hour)} %</span>
              </div>                
            </div>
            <div className="m-l-2 flex flex--align-items--center">
              <div className={styles.rect} style={{color: 'rgb(135,203,190)'}}></div>
              <div>
                <span>计量</span>
                <span>&nbsp;{focus.meter} / {focus.man_hour}</span>
                <span>&nbsp;(工作小时) = {round(focus.meter * 100 / focus.man_hour)} %</span>
              </div>                
            </div>
            <div className="m-l-2 flex flex--align-items--center">
              <div className={styles.rect} style={{color: 'rgb(154,201,192)'}}></div>
              <div>
                <span>巡检</span>
                <span>&nbsp;{focus.inspection} / {focus.man_hour}</span>
                <span>&nbsp;(工作小时) = {round(focus.inspection * 100 / focus.man_hour)} %</span>
              </div>              
            </div>
          </div>
          <div
            className="clickable m-b-1 flex flex--align-items--center"
            style={{opacity: filter === RATE ? 1 : 0.3}}
            onClick={onClick(RATE)}>
            <div className={styles.rect} style={{color: '#d6c25e'}}></div>
            <div className="m-r-3">满意度</div>
            <StarRate total={range.score} value={focus.score} />
            <div className="m-l-1">{round(focus.score)} 分</div>
          </div>
          <div style={{opacity: filter === ORDER ? 1 : 0.3}}>
            <div
              className="clickable flex flex--align-items--center"
              onClick={onClick(ORDER)}>
              <div className={styles.rect} style={{color: '#bb81b8'}}></div>
              <div>工单数量 {focus.work_order} 个</div>
            </div>
            <div className="m-l-2 flex flex--align-items--center">
              <div className={styles.rect} style={{color: '#bb81b8'}}></div>
              <div>已完成 {focus.closed} 个</div>
            </div>
            <div className="m-l-2 flex flex--align-items--center">
              <div className={styles.rect} style={{color: '#896089'}}></div>
              <div>未完成 {focus.open} 个</div>
            </div>
          </div>
        </div>
      </div>
    )
  }
}
