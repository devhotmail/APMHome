// @flow
import React from 'react'
import RingSectorLayout, { AnnulusSectorStack } from 'ring-sector-layout'
import classnames from 'classnames'
import styles from './index.scss'

type Props = {
  className?: string,
  items: Array<*>
}

export default
class AssetsForecast extends React.PureComponent<*, Props, *> {
  render() {
    const { items, className } = this.props

    return (
      <RingSectorLayout
        startAngle={0}
        endAngle={Math.PI}
        getCx={(width, height) => 0}
        getCy={(width, height) => height / 2}
        items={items}
        className={classnames(className, styles['assets-predict'])}
      >
        {
          (item, index, innerRadius, outerRadius) => {
            const span = Math.PI / Math.max(30, items.length)
            const startAngle = item.style.position - span / 2
            const endAngle = item.style.position + span / 2
            return (
              <AnnulusSectorStack
                key={item.data.id}
                innerRadius={innerRadius}
                startAngle={startAngle}
                endAngle={endAngle}
                text={{
                  content: item.data.name,
                  offset: (outerRadius - innerRadius) / 10,
                  fontSize: (outerRadius - innerRadius) / 4,
                  fill: item.data.color
                }}
                sectors={[]}
              />
            )
          }
        }
      </RingSectorLayout>
    )
  }
}
