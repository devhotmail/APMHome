/* @flow */
import React from 'react'
import styles from './styles.scss'

// for svg sprites
const _require = require.context('#/assets/icons', false, /\.svg$/)
_require.keys().forEach(key => _require(key))

type PropT = {
  symbolId: string
}

export default class PinenutIcon extends React.PureComponent<*, PropT, *> {
  render() {
    const { symbolId, ...restProps } = this.props
    return (
      <span {...restProps}>
        <svg className={styles.icon}>
          <use xlinkHref={`#${symbolId}`} />
        </svg>
      </span>
    )
  }
}
