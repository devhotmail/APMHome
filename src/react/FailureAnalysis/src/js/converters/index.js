
import { maxBy, sumBy, sortBy, clamp } from 'lodash-es'
import colors from 'utils/colors'
import { ellipsis } from 'utils/helpers'
import SID from 'shortid'
import ColorUtil from 'color'

export function AssetTypesConv(resp) {
  let converted = Object.keys(resp.data).map(i => ({ id: String(i), name: resp.data[i] }))
  return sortBy(converted, ['name'])
}

export function BriefConv(resp, type, lastYear) {
  let arr = resp.data.briefs
  if (arr.length === 0) {
    arr.pages = resp.data.pages
    return arr
  }
  let weightMax = maxBy(arr, item => item.val[type]).val[type]
  let result = BriefToothAdapter(arr, weightMax, type, lastYear)
  result.pages = resp.data.pages
  return result
}

export function BriefAssetConv(resp, type, lastYear) {
  let arr = resp.data.briefs
  if (arr.length === 0) {
    arr.pages = resp.data.pages
    return arr
  }
  let weightMax = maxBy(arr, item => item.val[type]).val[type]
  let result = BriefToothAdapter(arr, weightMax, type, lastYear)
  result.pages = resp.data.pages
  return result
}

export function ReasonConv(resp) {
  let result = ReasonToothAdapter(resp.data.reasons)
  return result
}

function BriefToothAdapter(array, max, type, lastYear) {
  let color = getStripColor(type)
  if (lastYear) {
    color = ColorUtil(color).lighten(.4).hexString()
  }
  return array.map(a => ({ 
    id: SID.generate(), 
    data: a, 
    mode: 'bar', 
    label: ellipsis(a.key.name, 8),
    strips: [{color: color, weight: a.val[type] / max, value: a.val[type], data: a}] 
  }))
}

/* ask designer/BE why about below code */
const MAGIC_NAME = '其它'
const MAGIC_DATA = { name: MAGIC_NAME }

function ReasonToothAdapter(array) {
  if (array.length === 0) {
    return array
  }
  let weightMax = maxBy(array, item => item.count).count
  let sum = sumBy(array, i => i.count)
  let result = []
  let cursor = 0
  let otherReasonCount = 0
  array.forEach(e => {
    if (e.name === MAGIC_NAME) {
      otherReasonCount += e.count
      return
    }
    cursor += e.count
    let color = cursor/sum > .85 ? colors.gray : colors.blue
    result.push({
      id: SID.generate(), 
      data: e, 
      mode: 'bar', 
      label: ellipsis(e.name, 5),
      strips:[{ color: color, weight: e.count/weightMax, data: e}] })
  })
  // select top 10 reasons, then append '其他'
  MAGIC_DATA.count = otherReasonCount
  return result.slice(0, 12).concat(otherReasonCount ? [{
    id: SID.generate(),
    data: MAGIC_DATA,
    mode: 'bar',
    label: MAGIC_NAME,
    strips:[{ color: colors.gray, weight: clamp(otherReasonCount, weightMax)/weightMax, data: MAGIC_DATA}]
  }] : [])
}

function getStripColor(type) {
  switch (type) {
    case 'avail':
      return colors.purple
    case 'ftfr':
      return colors.yellow
    case 'fix':
      return colors.green
    default:
      return colors.gray
  }
}