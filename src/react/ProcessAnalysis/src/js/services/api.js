import axios from 'axios'
import { load } from './mocks/helper'
import { AssetTypesConv } from 'converters'

const UseMock = true
if (process.env.NODE_ENV === 'development' && UseMock) {
  load('./sample')
}

const PREFIX = process.env.LOCAL ? '/geapm/' : '/' 
export const PATH_BRIEF = PREFIX + 'api/process/brief'
export const PATH_DETAIL = PREFIX + 'api/process/detail'
export const PATH_GROSS = PREFIX + 'api/process/gross'

// const DateFormat = 'YYYY-MM-DD'
// const GroupBy = {
//   'display_brand': 'supplier',
//   'display_asset_type': 'type'
// }
export const DataTypeMapping = {
  'response_time': 'respond',
  'ettr': 'ETTR',
  'arrival_time': 'arrived'
}

// function mapParamsToQuery(params, type) {
//   let pag = params.pagination[type]
//   let filterBy = params.filterBy
//   let assetType = filterBy.assettype === 'all_asset_type' ? '' : filterBy.assettype
//   let dept = filterBy.dept === 'all_dept' ? '' : filterBy.dept
//   let supplier = filterBy.supplier === 'all_supplier' ? '' : filterBy.supplier
//   return {
//     from: params.period.from.format(DateFormat),
//     to: params.period.to.format(DateFormat),
//     groupby: GroupBy[params.display],
//     orderby: DataTypeMapping[params.orderBy],
//     type: assetType,
//     dept: dept,
//     start: pag.skip || 0,
//     limit: pag.top,
//     supplier: supplier,
//     dataType: DataTypeMapping[params.dataType],
//   }
// }

export default {

  getAssetTypes() {
    return axios.get(PREFIX + 'api/msg?type=assetGroup').then(AssetTypesConv)
  },
  getDepartments() {
    return axios.get(PREFIX + 'api/org/all').then(resp => resp.data.orgInfos)
  },
  getBriefs(params) {
    return axios.get(PATH_BRIEF, { params }).then(_ => _.data)
  },
  getDetails(params) {
    return axios.get(PATH_DETAIL, { params }).then(_ => _.data)
  },
  getGross(params) {
    return axios.get(PATH_GROSS, { params }).then(_ => _)
  }
}