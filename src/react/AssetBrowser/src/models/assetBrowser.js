import Immutable from 'immutable'
import uuid from 'uuid/v4'
import { rnorm } from 'randgen'
import groupBy from 'lodash.groupby'
import clamp from 'lodash.clamp'
import { trimString } from '#/utils'

export const CLUSTER_SPACING = 20
export const ASSET_HEIGHT = 15
export const ASSET_WIDTH = 25

export const COLORS = ['#6e83a7', '#6e9aa9', '#8888a2', '#76aa9f', '#ba82bb', '#58bbb2']
export const INACTIVE_COLORS = ['#c4cbd6', '#c4d2d7', '#ccccd5', '#c6d7d3', '#dccadc', '#bddcd9']

const funcs = [
  '6281医用电子仪器设备',
  '6815穿刺注射设备',
  '6823医用超声波仪器及还有很多字需要隐藏',
  '6808胸部外科手术器械',
  '6806口腔科手术设备',
  ''
]

const types = [
  'MR',
  'CT',
  'DR',
  'CR',
  'LCS-Monitor',
  'LCS-Ventilator',
  'LCS-Anesthedia',
  ''
]

const priceRangeForType = [
  [8e6, 2e7],
  [2e6, 9e6],
  [1e5, 9e5],
  [1e5, 9e5],
  [1e4, 9e4],
  [1e4, 9e4],
  [1e4, 9e4],
  [1e4, 9e4],
  [1e4, 2e7]
]

const depts = [
  '放射科',
  'XXXXX',
  'XXX',
  'BLA',
  'BLABLA',
  'WHATEVER',
  ''
]

const suppliers = [
  '通用电气',
  '西门子',
  '飞利浦',
  'XXXXX',
  'BLA',
  'BLABLA',
  ''
]

const assets = []

function randInt(range) {
  return Math.floor(range * Math.random())
}

function randRange(min, max) {
  return (max - min) * Math.random() + min
}

function rnormRange(min, max) {
  const mean = (max + min) / 2
  const std = (max - min) / 2 / 3
  const r = rnorm(mean, std)
  if (r > max) return max
  if (r < min) return min
  return r
}

for (let i = 0; i < randRange(2000, 4000); i++) {
  const randI = randInt(types.length)
  assets.push({
    id: uuid(),
    func: funcs[randInt(funcs.length)],
    type: types[randI],
    dept: depts[randInt(depts.length)],
    supplier: suppliers[randInt(suppliers.length)],
    price: rnormRange(...priceRangeForType[randI]),
    yoi: randRange(+new Date('2005-1-1'), +new Date())
  })
}

const rulers = [{
  dimension: 'price',
  ticks: [
    1e4,
    1e5,
    5e5,
    1e6,
    5e6,
    1e7
  ]
}]

const dimensions = ['func', 'type', 'dept', 'supplier', 'price', 'yoi']


const filters = [
  // {dimension: 'type', displayName: 'LCS-Monitor', key: 'LCS-Monitor'},
  // {dimension: 'type', displayName: '放射科', key: '放射科'}
]


function distributeColumns(width, num) {
  const spacing =  width / (num + 1)
  const res = []
  for (let i = 0; i < num; i++) {
    res.push(spacing * (i + 1))
  }
  return res
}

export function formatPrice(price) {
  // if (price >= 10000000) return `${Math.floor(price / 10000000)}千万`
  // if (price >= 1000000) return `${Math.floor(price / 1000000)}百万`
  if (price >= 10000) return `${Math.floor(price / 10000)}万`
  if (price >= 1000) return `${Math.floor(price / 1000)}千`
  if (price >= 100) return `${Math.floor(price / 100)}百`
}

function getDisplayName(key) {
  if (key === '') return '其他'
  if (key === -1) return '其他'
  if (key.includes('~')) {
    const range = key.split('~').map(num => Number(num))
    if (range.every(num => !isNaN(num))) {
      if (range[0] === -Infinity) return `小于${formatPrice(range[1])}`
      if (range[1] === +Infinity) return `大于${formatPrice(range[0])}`
      return `${formatPrice(range[0])}－${formatPrice(range[1])}`
    }
  }
  return key
}

export function formatKey(asset, dimension, ruler) {
  if (dimension === 'yoi') return '' + new Date(asset.get(dimension)).getFullYear()
  if (ruler) {
    const range = ruler.get('ticks').reduce((prev, cur) => {
      if (cur > asset.get(dimension) && cur < prev[1]) prev[1] = cur
      if (cur < asset.get(dimension) && cur > prev[0]) prev[0] = cur
      return prev
    }, [-Infinity, +Infinity])
    return range.join('~')
  }
  return asset.get(dimension)
}

function getClusters(dimension, ruler, assets, activeAssetId, hoveredAssetId) {
  const map = assets.groupBy(asset => formatKey(asset, dimension, ruler))

  const list = map.entrySeq().map(([key, value]) => Immutable.fromJS({
    key,
    displayName: getDisplayName(key),
    assetActive: !!activeAssetId,
    children: value.map(asset => {
      const id = asset.get('id')
      return asset.withMutations(asset => {
        asset
        .set('active', id === activeAssetId)
        .set('hovered', id === hoveredAssetId)
      })
    })
  })).sort((a, b) => b.get('children').size - a.get('children').size)

  const res = list.reduce((prev, cur) => {
    const prevCluster = prev.get(-1)
    if (prevCluster === undefined) return prev.push(cur.set('top', 0))

    return prev.push(cur.set('top', prevCluster.get('top') + prevCluster.get('children').size * ASSET_HEIGHT + CLUSTER_SPACING))

  }, Immutable.fromJS([]))

  return res
}

// 0: in ViewBox, 1: below ViewBox, -1: above ViewBox
function calcAssetPos(assetTop, viewBoxYRange) {
  if (assetTop < viewBoxYRange[0]) return -1
  if (assetTop + ASSET_HEIGHT > viewBoxYRange[1]) return 1
  return 0
}

function updateColumnOffset(column, assetId, viewBoxYRange) {
  if (!assetId) return column.set('offset', 0)
  const currentOffset = column.get('offset', 0)

  const cluster = column.get('clusters').find(cluster => cluster.get('children').some(asset => asset.get('id') === assetId))
  const assetIndex = cluster.get('children').findIndex(asset => asset.get('id') === assetId)

  const assetTop = cluster.get('top') + assetIndex * ASSET_HEIGHT
  const assetPos = calcAssetPos(assetTop + currentOffset, viewBoxYRange)

  if (assetPos === 0) {
    return column.set('offset', currentOffset)
  } else if (assetPos === 1) {
    let magicVal = cluster.get('children').size * ASSET_HEIGHT / (viewBoxYRange[1] - viewBoxYRange[0])
    magicVal = 1 / (1 + Math.pow(Math.E, magicVal))
    magicVal = magicVal / 6 + 1 / 3
    let offset = viewBoxYRange[1] - (viewBoxYRange[1] - viewBoxYRange[0]) * magicVal - assetTop
    return column.set('offset', offset)
  } else if (assetPos === -1) {
    let magicVal = cluster.get('children').size * ASSET_HEIGHT / (viewBoxYRange[1] - viewBoxYRange[0])
    magicVal = 1 / (1 + Math.pow(Math.E, magicVal))
    magicVal = magicVal / 6 + 1 / 3
    let offset = viewBoxYRange[0] + (viewBoxYRange[1] - viewBoxYRange[0]) * magicVal - assetTop

    return column.set('offset', Math.min(offset, viewBoxYRange[0]))
  }
}

export default {
  namespace: 'assetBrowser',
  state: Immutable.fromJS({
    filters: [],
    hoveredAssetId: null,
    activeAssetId: null,
    pageIndex: 0,
    rulers: [],
    assets: [],
    dimensions: [],
    viewBox: {
      width: 0,
      height: 0,
      left: 0,
      right: 0,
      top: 0,
      bottom: 0
    },
    columns: []
  }),
  subscriptions: {
    setup({ dispatch }) {
      dispatch({
        type: 'hydrate',
        payload: {
          assets,
          rulers,
          dimensions
        }
      })
    }
  },
  effects: {
    addWatcher: [ function* ({ takeLatest, put, call, select }) {
      const paload = yield takeLatest(
        [
          'assetBrowser/active/clear',
          'assetBrowser/hydrate',
          'assetBrowser/asset/clicked',
          'assetBrowser/viewBox/update',
          'assetBrowser/filters/removeLast',
          'assetBrowser/filters/toggle',
          'assetBrowser/filters/remove'
        ],
        function* (action) {
          const assetBrowser = yield select(state => state.assetBrowser)
          const height = assetBrowser.getIn(['viewBox', 'height'])
          const pageIndex = assetBrowser.get('pageIndex')
          const dimensions = assetBrowser.get('dimensions')
          const filters = assetBrowser.get('filters')
          const rulers = assetBrowser.get('rulers')
          const assets = assetBrowser.get('assets').filter(asset => {
            return filters.every(filter => {
              const formattedKey = formatKey(asset, filter.get('dimension'), rulers.find(ruler => ruler.get('dimension') === filter.get('dimension')))
              return formattedKey === filter.get('key')
            })
          })
          const activeAssetId = assetBrowser.get('activeAssetId')
          const hoveredAssetId = assetBrowser.get('hoveredAssetId')
          const width = assetBrowser.getIn(['viewBox', 'width'])
          let columns = assetBrowser.get('columns')

          const columnXs = distributeColumns(width, dimensions.size)

          columns = dimensions.map((dimension, index) => {
            return columns.get(index, Immutable.fromJS({})).merge(Immutable.fromJS({
              dimension,
              left: columnXs[index],
              clusters: getClusters(
                dimension,
                rulers.find(ruler => ruler.get('dimension') === dimension),
                assets,
                activeAssetId,
                hoveredAssetId
              )
            }))
          })

          columns = columns.map(column => updateColumnOffset(column, activeAssetId, [pageIndex * height, (pageIndex + 1) * height]))

          const maxPage = columns.reduce((prev, column) => {
            const lastCluster = column.getIn(['clusters', -1])
            const bottom = lastCluster.get('top') + lastCluster.get('children').size * ASSET_HEIGHT + CLUSTER_SPACING
            const maxPage = Math.floor(bottom / height) + 1
            return prev > maxPage ? prev : maxPage
          }, 0)

          if (assetBrowser.get('maxPage') !== maxPage) yield put({
            type: 'maxPage/update',
            payload: maxPage
          })

          yield put({
            type: 'columns/update',
            payload: columns
          })
        }
      )
    }, { type: 'watcher'}]
  },
  reducers: {
    ['hydrate'](state, { payload }) {
      const { assets, rulers, dimensions } = payload
      return state.withMutations(state => {
        state
        .set('assets', Immutable.fromJS(assets))
        .set('rulers', Immutable.fromJS(rulers))
        .set('dimensions', Immutable.fromJS(dimensions))
        .set('filters', Immutable.fromJS(filters))
        .set('hoveredAssetId', null)
        .set('activeAssetId', null)
        .set('pageIndex', 0)
      })
    },
    ['viewBox/update'](state, { payload }) {
      return state.set('viewBox', Immutable.fromJS(payload))
    },
    ['columns/update'](state, { payload }) {
      return state.set('columns', payload)
    },
    ['maxPage/update'](state, { payload }) {
      return state.set('maxPage', payload)
    },
    ['asset/clicked'](state, { payload }) {
      return state.update('activeAssetId', val => val === payload ? null : payload)
    },
    ['active/clear'] (state) {
      return state.set('activeAssetId', null)
    },
    ['filters/removeLast'](state) {
      return state.withMutations(state => {
        state
        .update('filters', filters => filters.butLast())
        .set('pageIndex', 0)
        .set('activeAssetId', null)
      })
    },
    ['filters/remove'](state, { payload: { dimension }}) {
      return state.withMutations(state => {
        state
        .update('filters', filters => filters.filter(filter => filter.get('dimension') !== dimension))
        .set('pageIndex', 0)
        .set('activeAssetId', null)
      })
    },
    ['filters/toggle'](state, { payload }) {
      const { dimension, key, displayName} = payload
      return state.withMutations(state => {
        state
        .set('activeAssetId', null)
        .set('pageIndex', 0)
        .update('filters', filters => {
          const index = filters.findIndex(filter => filter.get('dimension') === dimension)
          if (index === -1) return filters.push(Immutable.fromJS(payload))
          if (filters.getIn([index, 'key']) === key) return filters.delete(index)
          else return filters.withMutations(filters => {
            filters
            .delete(index)
            .push(Immutable.fromJS(payload))
          })
        })
      })
    },
    ['page/change'](state, { payload }) {
      return state.withMutations(state => {
        state
        .set('activeAssetId', null)
        .update('pageIndex', index => clamp(
          index + payload,
          0,
          state.get('maxPage') - 1
        ))
      })
    }
  }
}
