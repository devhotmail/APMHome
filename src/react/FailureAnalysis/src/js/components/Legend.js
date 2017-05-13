import React, { Component } from 'react'
import { translate } from 'react-i18next'
import Radium from 'radium'
import autobind from 'autobind-decorator'

type LegendItem = {
  color: string,
  key: string
}
type Props = {
  t: any,
  items: List<LegendItem>,
}

let styles = {
  legend: {
    textAlign: 'center'
  }
}

@autobind
@Radium
export class Legend extends Component<void, Props, void> {

  render() {
    const { t, items } = this.props
    return (
      <div style={styles.legend}>
        { this.props.children }
        { 
          items.map(_ => 
          <div key={_.key} className="legend-item">
            <span className="legend-symbol" style={{ color: _.color }}>◼</span>
            <span className="legend-label">{t(_.key)}</span>
          </div>) 
        }
      </div>
    )
  }
}

export default translate()(Legend)
