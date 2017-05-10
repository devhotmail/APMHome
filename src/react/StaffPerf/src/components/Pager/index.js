import React, { PureComponent, PropTypes } from 'react'
import { Pagination } from 'antd'

import styles from './styles.scss'

export default class Pager extends PureComponent {
  static defaultProps = {
    current: 1,
    pageSize: 10
  }

  static propTypes = {
    current: PropTypes.number,
    pageSize: PropTypes.number
  }

  render () {
    const { total, pageSize, current } = this.props
    const totalPages = Math.ceil(total / pageSize)

    const prevCls = [current === 1 ? styles.disabled : '', styles.prev].join(' ')
    const nextCls = [current === totalPages ? styles.disabled : '', styles.next].join(' ')
    console.log(prevCls, nextCls, current, totalPages)
    return (
      <div className={styles.pager}>
        <div className={styles.wrapper}>
          <div className={prevCls} onClick={this.handlePrev}></div>
          <div className={styles.counter} onClick={this.handleEnable}>
            <span>{current}</span>
            <span>/</span>
            <span>{totalPages}</span>
          </div>
          <div className={nextCls} onClick={this.handleNext}></div>
        </div>
      </div>
    )
  }

  handlePrev = (e: Event) => {
    e.preventDefault()
    const { current } = this.props
    if (current > 1) this.changePage(current - 1)
  }

  handleNext = (e: Event) => {
    e.preventDefault()
    const { total, pageSize, current } = this.props
    const totalPages = Math.ceil(total / pageSize)
    if (current < totalPages) this.changePage(current + 1)
  }

  changePage = (current: number) => {
    if (current) this.props.onChange && this.props.onChange(current)
  }
}
