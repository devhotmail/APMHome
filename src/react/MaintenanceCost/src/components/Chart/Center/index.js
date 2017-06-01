import React from 'react'
import { connect } from 'dva'
import classnames from 'classnames'
import { round } from '#/utils'
import styles from './index.scss'

type Props = {
  filters: any,
  overview: any
}

class Center extends React.PureComponent<*, Props, *> {
  static getIncrease(now, past) {
    if (!past) return ''
    return ((now - past) / past * 100).toFixed(1) + '%'
  }
  toggleTarget = target => e => {
    this.props.dispatch({
      type: 'filters/field/set',
      payload: {
        key: 'target',
        value: target
      }
    })
  }
  render() {
    const { filters, overview } = this.props
    const { cursor, target } = filters
    return (
      <div className={styles['center']}>
        <h1>{cursor[1] ? overview.data.name : cursor[0] ? '汇总' : '全部选中设备'}</h1>
        <table className={styles['cost']} onClick={this.toggleTarget('acyman')} data-active={target === 'acyman'}>
          <thead>
            <tr>
              <th></th>
              <th>选中时段</th>
              <th>上一年同时段</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className={classnames(styles['with-rect'], styles['labor'])}>按成本类型</td>
              <td>{round(overview.data.labor + overview.data.parts, 0)}({Center.getIncrease(overview.data.labor + overview.data.parts, overview.pastData.labor + overview.pastData.parts)})</td>
              <td>{round(overview.pastData.labor + overview.pastData.parts, 0)}</td>
            </tr>
            <tr>
              <td className={classnames(styles['with-rect'], styles['labor'])}>人力</td>
              <td>{round(overview.data.labor, 0)}({Center.getIncrease(overview.data.labor, overview.pastData.labor)})</td>
              <td>{round(overview.pastData.labor, 0)}</td>
            </tr>
            <tr>
              <td className={classnames(styles['with-rect'], styles['parts'])}>备件</td>
              <td>{round(overview.data.parts, 0)}({Center.getIncrease(overview.data.labor, overview.pastData.labor)})</td>
              <td>{round(overview.pastData.parts, 0)}</td>
            </tr>
          </tbody>
        </table>
        <table className={styles['wo']} onClick={this.toggleTarget('mtpm')} data-active={target === 'mtpm'}>
          <thead>
            <tr>
              <th></th>
              <th>选中时段</th>
              <th>上一年同时段</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className={classnames(styles['with-rect'], styles['repair'])}>按派工单类型</td>
              <td>{round(overview.data.repair + overview.data.PM, 0)}({Center.getIncrease(overview.data.repair + overview.data.PM, overview.pastData.repair + overview.pastData.PM)})</td>
              <td>{round(overview.pastData.repair + overview.pastData.PM, 0)}</td>
            </tr>
            <tr>
              <td className={classnames(styles['with-rect'], styles['repair'])}>维修</td>
              <td>{round(overview.data.repair, 0)}({Center.getIncrease(overview.data.repair, overview.pastData.repair)})</td>
              <td>{round(overview.pastData.repair, 0)}</td>
            </tr>
            <tr>
              <td className={classnames(styles['with-rect'], styles['PM'])}>PM</td>
              <td>{round(overview.data.PM, 0)}({Center.getIncrease(overview.data.PM, overview.pastData.PM)})</td>
              <td>{round(overview.pastData.PM, 0)}</td>
            </tr>
          </tbody>
        </table>
      </div>
    )
  }
}

export default connect(({ filters, overview }) => ({ filters, overview }))(Center)
