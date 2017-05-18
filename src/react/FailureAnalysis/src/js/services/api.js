import axios from 'axios'
import { load } from './mocks/helper'
import { AssetTypesConv, BriefConv, ReasonConv, BriefAssetConv } from 'converters'

const UseMock = true
if (process.env.NODE_ENV === 'development' && UseMock) {
  load('./sample')
}

const prefix = process.env.LOCAL ? '/geapm/' : '/' 
const DateFormat = 'YYYY-MM-DD'
const GroupBy = {
  'display_brand': 'supplier',
  'display_asset_type': 'type',
  'display_dept': 'dept', 
}
export const DataTypeMapping = {
  'operation_rate': 'avail',
  'ftfr': 'ftfr',
  'incident_count': 'fix'
}
function mapParamsToQuery(params, type) {
  let pag = params.pagination[type]
  let filterBy = params.filterBy
  let assetType = filterBy.assettype === 'all_asset_type' ? '' : filterBy.assettype
  let dept = filterBy.dept === 'all_dept' ? '' : filterBy.dept
  let supplier = filterBy.supplier === 'all_supplier' ? '' : filterBy.supplier
  return {
    from: params.period.from.format(DateFormat),
    to: params.period.to.format(DateFormat),
    groupby: GroupBy[params.display],
    orderby: DataTypeMapping[params.orderBy],
    type: assetType,
    dept: dept,
    start: pag.skip || 0,
    limit: pag.top,
    supplier: supplier,
    dataType: DataTypeMapping[params.dataType],
    key: params.keys
  }
}

export default {

  getAssetTypes() {
    return axios.get(prefix + 'api/msg?type=assetGroup').then(AssetTypesConv)
  },
  getDepartments() {
    return axios.get(prefix + 'api/org/all').then(resp => resp.data.orgInfos)
  },
  getBriefs(type, state, lastYear) {
    let params = mapParamsToQuery(state, type)
    if (lastYear) {
      params.from = state.period.from.clone().subtract('1', 'years').format(DateFormat)
      params.to = state.period.to.clone().subtract('1', 'years').format(DateFormat)
    }
    if (type === 'left') {
      return axios.get(prefix + 'api/fa/briefs', {params})
        .then(resp => BriefConv(resp, params.dataType, lastYear))
    } else {
      params.groupby = 'asset'
      return axios.get(prefix + 'api/fa/briefs', {params})
        .then(resp => BriefAssetConv(resp, params.dataType, lastYear))
    }
  },
  getReasons(state, { type, asset, supplier, dept }) {
    let { period: { from, to }, filterBy } = state
    let params = {
      from: from.format(DateFormat),
      to: to.format(DateFormat),
      type: filterBy.assettype === 'all_asset_type' ? type : filterBy.assettype,
      dept: filterBy.dept === 'all_dept' ? dept : filterBy.dept,
      supplier: supplier,
      asset: asset,
    }
    return axios.get(prefix + 'api/fa/reasons', {params}).then(ReasonConv)
  },

}