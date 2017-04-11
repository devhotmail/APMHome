import React from 'react'
import ReactDOM from 'react-dom'
import Immutable, { is } from 'immutable'
import { connect } from 'dva/mobile'
import { spring, Motion } from 'react-motion'
import WheelIndicator from 'wheel-indicator'
import withClientRect from '#/HOC/withClientRect'
import { trimString } from '#/utils'
import { formatPrice, CLUSTER_SPACING, ASSET_HEIGHT, ASSET_WIDTH, COLORS, INACTIVE_COLORS } from '#/models/assetBrowser'
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
  columns: assetBrowser.get('columns'),
  width: assetBrowser.getIn(['viewBox', 'width']),
  filters: assetBrowser.get('filters')
}))
class ChartHeader extends ImmutableComponent {
  removeFilter = dimension => () => {
    this.props.dispatch({
      type: 'assetBrowser/filters/remove',
      payload: {
        dimension
      }
    })
  }
  render() {
    const translation = {
      'func': '按功能分组',
      'type': '按类型',
      'dept': '按科室',
      'supplier': '按品牌',
      'price': '按价值',
      'yoi': '按时间'
    }
    const { columns, filters } = this.props
    return (
      <div className={styles['chart-header']}>
        {
          columns.map((column, index) => (
            <Motion
              key={column.get('dimension')}
              defaultStyle={{
                scale: 1
              }}
              style={{
                scale: spring(filters.find(filter => filter.get('dimension') === column.get('dimension')) ? 1.3 : 1)
              }}>
              {
                style => (
                  <div
                    onClick={this.removeFilter(column.get('dimension'))}
                    style={{
                      transform: `translate(-50%, -50%) scale(${style.scale})`,
                      position: 'absolute',
                      left: column.get('left'),
                      bottom: 0,
                      color: COLORS[index],
                      cursor: 'pointer'
                    }}
                    >
                      {translation[column.get('dimension')]}
                    </div>
                )
              }
            </Motion>
          ))
        }
      </div>
    )
  }
}

@connect()
class Asset extends ImmutableComponent {
  onClick = () => {
    this.props.dispatch({
      type: 'assetBrowser/asset/clicked',
      payload: this.props.asset.get('id')
    })
  }

  onMouseEnter = (e) => {
    const keys = ['name', 'func', 'type', 'dept', 'supplier', 'price', 'yoi']
    const reference = ReactDOM.findDOMNode(this)
    const clientRect = reference.getBoundingClientRect()
    const style = {
      pointerEvents: 'none',
      position: 'fixed',
      minWidth: 100,
      // top: clientRect.top,
      // left: clientRect.left,
      background: '#d8d8d8',
      border: '1px solid #5d87d4',
      borderRadius: 3,
      padding: '12px 7px',
      // transform: `translate(${clientRect.width + 10}px, ${clientRect.height / 2}px)`
    }

    if (clientRect.top > window.innerHeight * 0.6) {
      style.bottom = window.innerHeight - clientRect.bottom + clientRect.height / 2 + 'px'
    } else {
      style.top = clientRect.top + clientRect.height / 2 + 'px'
    }

    if (clientRect.left > window.innerWidth * 0.6) {
      style.right = window.innerWidth - clientRect.right + clientRect.width + 10 + 'px'
    } else {
      style.left = clientRect.left + clientRect.width + 10 + 'px'
    }
    this.el = this.el || ReactDOM.render((
      <div style={style}>
        {keys.map((key, index) => (
          <p style={{color: ['#848484'].concat(COLORS)[index]}} key={key}>
            {
              key === 'price' ? formatPrice(this.props.asset.get(key)) : this.props.asset.get(key)
            }
          </p>
        ))}
      </div>
    ), document.createElement('div'))
    document.body.appendChild(this.el)
  }

  onMouseLeave = () => {
    document.body.removeChild(this.el)
  }

  render() {
    const { asset, columnIndex, index } = this.props
    return (
      <rect
        onMouseEnter={this.onMouseEnter}
        onMouseLeave={this.onMouseLeave}
        onClick={this.onClick}
        style={{
          fill: asset.get('active') ? COLORS[columnIndex] : null,
          cursor: 'pointer',
          stroke: '#eaeaea',
          strokeWidth: 1,
          strokeDasharray: `${ASSET_WIDTH}, ${ASSET_HEIGHT}`,
        }}
        width={ASSET_WIDTH}
        height={ASSET_HEIGHT}
        x={0}
        y={index * ASSET_HEIGHT}
      />
    )
  }
}

class Assets extends ImmutableComponent {
  render() {
    const { assets, columnIndex } = this.props
    return (
      <g>
        {
          assets.map((asset, index) => (
            <Asset
              key={asset.get('id')}
              index={index}
              columnIndex={columnIndex}
              asset={asset}
            />
          ))
        }
      </g>
    )
  }
}

@connect()
class Cluster extends ImmutableComponent {
  calcTextY(columnOffset, clusterTop, clusterHeight, viewBoxYRange) {
    if (clusterTop + clusterHeight + columnOffset - 20 < viewBoxYRange[0]) {
      return clusterHeight - 40
    } else if (clusterTop + columnOffset > viewBoxYRange[0]) {
      return 0
    } else {
      return viewBoxYRange[0] - clusterTop - columnOffset
    }
  }

  toggleFilter = () => {
    const { dimension, cluster } = this.props
    this.props.dispatch({
      type: 'assetBrowser/filters/toggle',
      payload: {
        dimension,
        key: cluster.get('key'),
        displayName: cluster.get('displayName')
      }
    })
  }

  render() {
    const { cluster, columnIndex, columnOffset, viewBoxOffsetY, viewBoxHeight, dimension} = this.props
    const textY = this.calcTextY(columnOffset, cluster.get('top'), cluster.get('children').size * ASSET_HEIGHT, [viewBoxOffsetY, viewBoxOffsetY + viewBoxHeight])
    return (
      <g
        transform={`translate(0, ${cluster.get('top')})`}
        fill={cluster.get('assetActive') ? INACTIVE_COLORS[columnIndex] : COLORS[columnIndex]}
        >
        <text
          style={{
            cursor: 'pointer',
            fill: COLORS[columnIndex]
          }}
          onClick={this.toggleFilter}
          textAnchor="end"
          x="0"
          y={textY}
          dx="-0.5em"
          dy="0.8em"
          >
          {trimString(cluster.get('displayName'), 20)}
        </text>
        <text
          style={{
            fill: COLORS[columnIndex]
          }}
          textAnchor="end"
          x="0"
          y={textY}
          dx="-0.5em"
          dy="2em"
          >
          {cluster.get('children').size}台
        </text>
        <Assets
          assets={cluster.get('children')}
          columnIndex={columnIndex}
        />
      </g>
    )
  }
}

class Column extends ImmutableComponent {
  render() {
    const { column, viewBoxWidth, viewBoxHeight, viewBoxOffsetY, index } = this.props
    const clusters = column.get('clusters')
    return (
      <Motion
        defaultStyle={{
          offset: 0
        }}
        style={{
          offset: spring(column.get('offset'))
        }}>
        {
          style => (
            <g transform={`translate(${column.get('left') - ASSET_WIDTH / 2}, ${style.offset})`}>
              {
                clusters.map(cluster => (
                  <Cluster
                    key={cluster.get('key')}
                    cluster={cluster}
                    dimension={column.get('dimension')}
                    columnOffset={style.offset}
                    columnIndex={index}
                    viewBoxOffsetY={viewBoxOffsetY}
                    viewBoxHeight={viewBoxHeight}
                  />
                ))
              }
            </g>
          )
        }
      </Motion>
    )
  }
}

@connect(({assetBrowser}) => ({
  hoveredAssetId: assetBrowser.get('hoveredAssetId'),
  activeAssetId: assetBrowser.get('activeAssetId'),
  columns: assetBrowser.get('columns'),
  viewBoxHeight: assetBrowser.getIn(['viewBox', 'height'])
}))
class Lines extends ImmutableComponent {
  // lineToD(line) {
  //   const { columns } = this.props
  //   return line.reduce((prev, cur, index, line) => {
  //     if (index === 0) return ''
  //     const x0 = columns.getIn([index - 1, 'left']) + ASSET_WIDTH / 2
  //     const y0 = line.getIn([index - 1, 'top']) + ASSET_HEIGHT / 2
  //     const x1 = columns.getIn([index, 'left']) - ASSET_WIDTH / 2
  //     const y1 = line.getIn([index, 'top']) + ASSET_HEIGHT / 2
  //     const midX = (x0 + x1) / 2
  //     return prev.concat(`M ${x0}, ${y0} C ${midX}, ${y0} ${midX}, ${y1} ${x1} ${y1}`)
  //   }, '')
  // }

  render() {
    const { viewBoxOffsetY, viewBoxHeight, hoveredAssetId, activeAssetId, columns } = this.props
    const viewBoxTop = viewBoxOffsetY
    const viewBoxBottom = viewBoxOffsetY + viewBoxHeight
    if (viewBoxHeight === 0) return null
    if (columns.size === 0) return null

    const columnsWithClustersInViewBox = columns.map(column => column.update('clusters', clusters => {
      const columnOffset = column.get('offset')
      return clusters.filter(cluster => {
        const top = columnOffset + cluster.get('top')
        const bottom = top + cluster.get('children').size * ASSET_HEIGHT
        return (top <= viewBoxBottom) && (bottom >= viewBoxTop)
      })
    }))
    const visibleAssetsInColumns = columnsWithClustersInViewBox.map(
      column => column
      .get('clusters')
      .flatMap(cluster => cluster
        .get('children')
        .map((asset, index) => asset.set('top', column.get('offset') + cluster.get('top') + index * ASSET_HEIGHT))
        .filter(asset => {
          const top = asset.get('top')
          const bottom = top + ASSET_HEIGHT
          return (top >= viewBoxTop) && (bottom <= viewBoxBottom)
        })
      )
    )

    const connectableAssets = visibleAssetsInColumns.map(
      assets => assets
      .filter(
        asset => visibleAssetsInColumns
        .every(
          assets => assets
          .find(testAsset => testAsset.get('id') === asset.get('id'))
        )
      )
      // .filter(asset => activeAssetId ? asset.get('id') === activeAssetId : true)
      .sort((a, b) => {
        if (a.get('id') === b.get('id')) return 0
        return a.get('id') > b.get('id') ? 1 : -1
      })
    )

    const firstColumn = connectableAssets.get(0)
    const lines = firstColumn.zipWith(
      (...array) => Immutable.List(array),
      ...connectableAssets.slice(1, connectableAssets.size).toArray()
    )

    return (
      <g>
        {
          lines.flatMap(line => line.reduce((prev, cur, index, line) => {
            if (index === 0) return prev
            return prev.push(Immutable.List([line.get(index - 1), cur]))
          }, Immutable.List([])).map((pair, index) => (
            <Motion
              // key={pair.getIn([0, 'id']) + index}
              key={pair.getIn([0, 'id']) + '-' + index}
              defaultStyle={{
                x0: columns.getIn([index, 'left']) + ASSET_WIDTH / 2,
                y0: pair.getIn([0, 'top']) + ASSET_HEIGHT / 2,
                x1: columns.getIn([index + 1, 'left']) - ASSET_WIDTH / 2,
                y1: pair.getIn([1, 'top']) + ASSET_HEIGHT / 2,
                opacity: 0
              }}
              style={{
                x0: columns.getIn([index, 'left']) + ASSET_WIDTH / 2,
                y0: spring(pair.getIn([0, 'top']) + ASSET_HEIGHT / 2),
                x1: columns.getIn([index + 1, 'left']) - ASSET_WIDTH / 2,
                y1: spring(pair.getIn([1, 'top']) + ASSET_HEIGHT / 2),
                opacity: spring(0.6)
              }}
              >
              {
                style => (
                  <path
                    opacity={style.opacity}
                    stroke={(pair.getIn([0, 'hovered']) || pair.getIn([0, 'active'])) ? '#5d87d4' : '#bcbcbc'}
                    strokeWidth={pair.getIn[0, 'active'] ? 3 : 1.5}
                    fill="none"
                    d={`M ${style.x0}, ${style.y0} C ${(style.x0 + style.x1) / 2}, ${style.y0} ${(style.x0 + style.x1) / 2}, ${style.y1} ${style.x1} ${style.y1}`}
                  />
                )
              }
            </Motion>
          )))
        }
      </g>
    )
  }
}


@connect(({assetBrowser}) => ({assetBrowser}))
class ChartBody extends ImmutableComponent {
  updateViewBox = () => {
    const clientRect = ReactDOM.findDOMNode(this).getBoundingClientRect()
    this.props.dispatch({
      type: 'assetBrowser/viewBox/update',
      payload: {
        width: clientRect.width,
        height: clientRect.height,
        left: clientRect.left,
        right: clientRect.right,
        top: clientRect.top,
        bottom: clientRect.bottom
      }
    })
  }

  componentDidMount() {
    window.addEventListener('resize', this.updateViewBox)
    this.updateViewBox()
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.updateViewBox)
  }

  render() {
    const { assetBrowser } = this.props
    const width = assetBrowser.getIn(['viewBox', 'width'])
    const height = assetBrowser.getIn(['viewBox', 'height'])
    const pageIndex = assetBrowser.get('pageIndex')
    const dimensions = assetBrowser.get('dimensions')
    // const assets = assetBrowser.get('assets')
    const filters = assetBrowser.get('filters')
    const activeAssetId = assetBrowser.get('activeAssetId')
    const hoveredAssetId = assetBrowser.get('hoveredAssetId')
    const columns = assetBrowser.get('columns')

    return (
      <Motion
        defaultStyle={{
          viewBoxOffsetY: 0
        }}
        style={{
          viewBoxOffsetY: spring(pageIndex * height)
        }}>
        {
          style => (
            <svg
              className={styles['chart-body']}
              viewBox={`0 ${style.viewBoxOffsetY} ${width} ${style.viewBoxOffsetY + height}`}
              preserveAspectRatio="xMinYMin slice">
              <Lines
                viewBoxOffsetY={style.viewBoxOffsetY}
              />
              {
                columns.map((column, index) => (
                  <Column
                    key={column.get('dimension')}
                    column={column}
                    viewBoxWidth={width}
                    viewBoxHeight={height}
                    viewBoxOffsetY={style.viewBoxOffsetY}
                    index={index}
                  />
                ))
              }
            </svg>
          )
        }
      </Motion>
    )
  }
}

@connect(({assetBrowser}) => ({
  activeAssetId: assetBrowser.get('activeAssetId'),
  pageIndex: assetBrowser.get('pageIndex'),
  maxPage: assetBrowser.get('maxPage')
}))
class Pagination extends ImmutableComponent {
  diffPage = diff => e => {
    if (this.props.activeAssetId) {
      this.props.dispatch({
        type: 'assetBrowser/active/clear'
      })
      setTimeout(() => this.props.dispatch({
        type: 'assetBrowser/page/change',
        payload: diff
      }), 500)
    } else {
      this.props.dispatch({
        type: 'assetBrowser/page/change',
        payload: diff
      })
    }
  }

  render() {
    const { pageIndex, maxPage } = this.props
    return (
      <div className={styles['pagination']}>
        <div
          className={styles['arrow-up']}
          onClick={this.diffPage(-1)}
        />
        <div className={styles['content']}>
          <span>{pageIndex + 1}</span>
          /
          <span>{maxPage}</span>
        </div>
        <div
          onClick={this.diffPage(1)}
          className={styles['arrow-down']}
        />
      </div>
    )
  }
}

export default
@connect(({assetBrowser}) => ({ activeAssetId: assetBrowser.get('activeAssetId')}))
class Chart extends ImmutableComponent {
  shouldComponentUpdate() {
    return false
  }

  componentDidMount() {
    this.wheelIndicator = new WheelIndicator({
      elem: ReactDOM.findDOMNode(this),
      callback: e => {
        if (this.props.activeAssetId) {
          this.props.dispatch({
            type: 'assetBrowser/active/clear'
          })
          setTimeout(() => this.props.dispatch({
            type: 'assetBrowser/page/change',
            payload: e.direction === 'up' ? -1 : 1
          }), 500)
        } else {
          this.props.dispatch({
            type: 'assetBrowser/page/change',
            payload: e.direction === 'up' ? -1 : 1
          })
        }
      }
    })
  }

  componentWillUnmount() {
    this.wheelIndicator.destroy()
    this.WheelIndicator = null
  }

  render() {
    return (
      <div className={styles['chart']}>
        <ChartHeader />
        <ChartBody />
        <Pagination />
      </div>
    )
  }
}
