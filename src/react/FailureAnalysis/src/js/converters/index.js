
import _ from 'lodash'
import colors from 'utils/colors'

export function AssetTypesConv(resp) {
  let converted = Object.keys(resp.data).map(i => ({ id: String(i), name: resp.data[i] }))
  return _.sortBy(converted, ['name'])
}

export function BriefConv(resp) {
  let arr = resp.data.briefs.slice(0, 6)
  let weightMax = _.maxBy(arr, item => item.val.avail).val.avail
  return BriefToothAdapter(arr, weightMax)
}

export function BriefAssetConv(resp) {
  let arr = resp.data.briefs.slice(0, 16)
  let weightMax = _.maxBy(arr, item => item.val.avail).val.avail
  return BriefToothAdapter(arr, weightMax)
}

export function ReasonConv(resp) {
  let result = ReasonToothAdapter(resp.data.reasons)
  return result
}

function BriefToothAdapter(array, sum, type = 'avail') {
  return array.map(a => ({ data: a, mode: 'bar', label: a.key.name, strips: [{color: getStripColor(type), weight: a.val[type] / sum, data: a}] }))
}

function ReasonToothAdapter(array) {
  let weightMax = _.maxBy(array, item => item.count).count
  let sum = _.sumBy(array, i => i.count)
  let result = []
  array.forEach(e => {
    let color = e.count/sum < .15 ? colors.gray : colors.blue  //  todo, refine algo
    result.push({data: e, mode: 'bar', label: e.name, strips:[{ color: color, weight: e.count/weightMax, data: e}] })
  })
  return result
}

function getStripColor(type) {
  switch (type) {
    case 'avail':
      return colors.red
    case 'ftfr':
      return colors.yellow
    case 'fix':
      return colors.blue
    default:
      return colors.gray
  }
}