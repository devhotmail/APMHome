
import { maxBy, sortBy } from 'lodash-es'
import colors from 'utils/colors'
import SID from 'shortid'

export function AssetTypesConv(resp) {
  let converted = Object.keys(resp.data).map(i => ({ id: String(i), name: resp.data[i] }))
  return sortBy(converted, ['name'])
}

const DataTypeMap = {
  'arrival_time': 'arrived',
  'response_time': 'respond',
  'ettr': 'ETTR'
}

export function BriefConv(briefs, type) {
  type = DataTypeMap[type]
  let weightMax = maxBy(briefs, item => item[type])[type]
  let result = ToothAdapter(briefs, weightMax, type)
  return result
}

export function DetailConv(details, type) {
  type = DataTypeMap[type]
  let weightMax = maxBy(details, item => item[type])[type]
  weightMax = 100 // todo hardcode!!!
  let result = ToothAdapter(details, weightMax, type)
  return result
}

function ToothAdapter(array, max, type) {
  let color = getStripColor(type)

  return array.map(a => ({ 
    id: SID.generate(), 
    data: a, 
    mode: 'bar', 
    label: a.name, 
    strips: [{color: color, weight: /*a[type] */ (max * Math.random()) / max, data: a}]  // TODO hardcode!!!
  }))
}


function getStripColor(type) {
  switch (type) {
    case 'respond':
      return colors.yellow
    case 'arrived':
      return colors.green
    case 'ETTR':
      return colors.purple
    default:
      return colors.gray
  }
}