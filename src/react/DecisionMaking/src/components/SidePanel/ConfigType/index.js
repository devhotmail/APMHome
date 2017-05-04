import React, { PureComponent } from 'react'

type ConfigT = {
  config: Array<Object>,
  focus: Object,
  depths: Array<number>,
  setFocus: Function
}

class ConfigType extends PureComponent<*, ConfigT, *> {
  render () {
    const { config, focus, depths, setFocus } = this.props
    const configListOne = config.filter(n => n.depth === depths[0])
    return (
      <div>
        <ul>
          {
            configListOne.length ? configListOne.map((n, index) => {
              const cls = n.id === focus.data.id && n.depth === focus.depth
                ? 'active'
                : ''
              const onClick = setFocus(n)
              return <li key={index} className={cls} onClick={onClick}>{n.name}</li>
            }) : null
          }
        </ul>
      </div>
    )
  }
}

export default ConfigType