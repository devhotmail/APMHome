import React from 'react'
import { connect } from 'dva'
import { Motion, spring } from 'react-motion'
import RingSectorLayout, { AnnulusSector, AnnulusSectorStack } from 'ring-sector-layout'
import { groupBy, flatMap } from 'lodash'
import Briefs from '#/components/Briefs'
import Parts from '#/components/Parts'
import Assets from '#/components/Assets'
import Steps from '#/components/Steps'
import Center from '#/components/Center'

import styles from './index.scss'


export default
class Chart extends React.PureComponent {
  render() {
    return (
      <div className={styles['chart']}>
        <Briefs />
        <Parts />
        <Assets />
        <Steps />
        <Center />
      </div>
    )
  }
}
