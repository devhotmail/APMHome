
import _ from 'lodash'
import colors from 'utils/colors'
import SID from 'shortid'

export function AssetTypesConv(resp) {
  let converted = Object.keys(resp.data).map(i => ({ id: String(i), name: resp.data[i] }))
  return _.sortBy(converted, ['name'])
}

export function BriefConv(resp, type) {
  let arr = resp.data.briefs.slice(0, 6) // todo, remove this line
  let weightMax = _.maxBy(arr, item => item.val[type]).val[type]
  return BriefToothAdapter(arr, weightMax, type)
}

export function BriefAssetConv(resp, type) {
  let arr = resp.data.briefs.slice(0, 16) // todo, remove this line
  let weightMax = _.maxBy(arr, item => item.val[type]).val[type]
  return BriefToothAdapter(arr, weightMax, type)
}

export function ReasonConv(resp) {
  let result = ReasonToothAdapter(resp.data.reasons)
  return result
}

function BriefToothAdapter(array, max, orderby) {
  let color = getStripColor(orderby)
  return array.map(a => ({ id: SID.generate(), data: a, mode: 'bar', label: a.key.name, strips: [{color: color, weight: a.val[orderby] / max, data: a}] }))
}

function ReasonToothAdapter(array) {
  let weightMax = _.maxBy(array, item => item.count).count
  let sum = _.sumBy(array, i => i.count)
  let result = []
  array.forEach(e => {
    let color = e.count/sum < .15 ? colors.gray : colors.blue  //  todo, refine algo
    result.push({id: SID.generate(), data: e, mode: 'bar', label: e.name, strips:[{ color: color, weight: e.count/weightMax, data: e}] })
  })
  return result
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