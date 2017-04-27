// @flow
import React from 'react'
import ImmutableComponent from '#/components/ImmutableComponent'
import BodyChart from '#/components/BodyChart'
import type { Map } from 'immutable'
import { connect } from 'dva'
import styles from './index.scss'

class ScanDetails extends ImmutableComponent<void, {scans: Map<string, any>}, void> {
  render() {
    const { scans } = this.props
    return (
      <div className={styles['scan-details']}>
        <BodyChart className={styles['body-chart']} scans={scans} />
      </div>
    )
  }
}

export default
connect(({scans}) => ({scans}))(ScanDetails)
