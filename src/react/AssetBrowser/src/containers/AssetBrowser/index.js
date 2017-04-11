import React from 'react'
import { is } from 'immutable'
import { connect } from 'dva'
import uuid from 'uuid/v4'
import Chart from '#/containers/Chart'
import { formatKey } from '#/models/assetBrowser'
import styles from './index.scss'


class ImmutableComponent extends React.Component {
  shouldComponentUpdate(nextProps, nextState) {
    nextProps = nextProps || {}
    nextState = nextState || {}
    const thisProps = this.props || {}, thisState = this.state || {};
    if (Object.keys(thisProps).length !== Object.keys(nextProps).length ||
        Object.keys(thisState).length !== Object.keys(nextState).length) {
      return true;
    }

    for (const key in nextProps) {
      if (!is(thisProps[key], nextProps[key])) {
        return true;
      }
    }

    for (const key in nextState) {
      if (thisState[key] !== nextState[key] || !is(thisState[key], nextState[key])) {
        return true;
      }
    }
    return false;
  }
}

@connect(({assetBrowser}) => ({
  filters: assetBrowser.get('filters'),
  assets: assetBrowser.get('assets'),
  rulers: assetBrowser.get('rulers')
}))
class Header extends ImmutableComponent {
  removeLastFilter = () => {
    this.props.dispatch({
      type: 'assetBrowser/filters/removeLast'
    })
  }
  render() {
    let { filters, assets, rulers } = this.props
    assets = assets.filter(asset => {
      return filters.every(filter => {
        const formattedKey = formatKey(asset, filter.get('dimension'), rulers.find(ruler => ruler.get('dimension') === filter.get('dimension')))
        return formattedKey === filter.get('key')
      })
    })
    return (
      <div className={styles['header']}>
        <div className={styles['breadcrumb']}>
          <span>全部设备</span>
          {
            filters.map((filter, index) => (
              [
                <span key={index} className={styles['arrow']}>></span>
                ,
                <span key={filter.get('key')} className={styles['filter']} data-is-last={index === filters.size - 1}>{filter.get('displayName')}</span>
              ]
            ))
          }
          {
            filters.size ? <span onClick={this.removeLastFilter} className={styles['remove']}>X</span> : null
          }
        </div>

        <div className={styles['stats']}>
          <span className={styles['current']}>当前设备：</span>
          <span className={styles['number']}>{assets.size}</span>
          <span className={styles['unit']}>台</span>
        </div>
      </div>
    )
  }
}

export default
class AssetBrowser extends ImmutableComponent {
  render() {
    return (
      <div className={styles['asset-browser']}>
        <Header />
        <Chart />
      </div>
    )
  }
}
