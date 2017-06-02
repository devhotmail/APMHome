
import _ from 'lodash'
import colors from 'utils/colors'
import SID from 'shortid'
import ColorUtil from 'color'

export function AssetTypesConv(resp) {
  let converted = Object.keys(resp.data).map(i => ({ id: String(i), name: resp.data[i] }))
  return _.sortBy(converted, ['name'])
}

export function BriefConv(resp, type, lastYear) {
  let arr = resp.data.briefs
  if (arr.length === 0) {
    arr.pages = resp.data.pages
    return arr
  }
  let weightMax = _.maxBy(arr, item => item.val[type]).val[type]
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
  let weightMax = _.maxBy(arr, item => item.val[type]).val[type]
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
    label: a.key.name, 
    strips: [{color: color, weight: a.val[type] / max, data: a}] 
  }))
}

function ReasonToothAdapter(array) {
  if (array.length === 0) {
    return array
  }
  let weightMax = _.maxBy(array, item => item.count).count
  let sum = _.sumBy(array, i => i.count)
  let result = []
  array.forEach(e => {
    let color = e.count/sum < .15 ? colors.gray : colors.blue  //  todo, refine algo
    result.push({id: SID.generate(), data: e, mode: 'bar', label: e.name, strips:[{ color: color, weight: e.count/weightMax, data: e}] })
  })
  return result.reverse() // todo: use clockwise props of chart
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