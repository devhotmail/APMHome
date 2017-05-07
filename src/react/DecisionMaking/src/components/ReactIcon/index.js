import React from 'react'
import styles from './styles.scss'

// for svg sprites
const _require = require.context('#/assets/icons', false, /\.svg$/)
_require.keys().forEach(key => _require(key))

export default class PinenutIcon extends React.PureComponent {
  static propTypes = {
    symbolId: React.PropTypes.string.isRequired,
    className: React.PropTypes.string
  }

  render() {
    const { className, symbolId } = this.props
    const iconClass = className
      ? `${styles.icon} icon-${symbolId} ${className}`
      : `${styles.icon} icon-${symbolId}`
    return (
      <svg className={iconClass}>
        <use xlinkHref={`#${symbolId}`} />
      </svg>
    )
  }
}
