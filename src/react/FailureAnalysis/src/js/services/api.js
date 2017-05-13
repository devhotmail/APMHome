import axios from 'axios'
import { load } from './mocks/helper'
import { AssetTypesConv, BriefConv, ReasonConv, BriefAssetConv } from 'converters'
import qp from 'query-parse'

const UseMock = true
if (true || process.env.NODE_ENV === 'development' && UseMock) {
  load('./sample')
}
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
  return {
    from: params.period.from.format(DateFormat),
    to: params.period.to.format(DateFormat),
    groupby: GroupBy[params.display],
    orderby: DataTypeMapping[params.orderBy],
    dataType: DataTypeMapping[params.dataType],
    start: pag.skip || 0,
    limit: pag.top
  }
}

export default {

  getAssetTypes() {
    return axios.get('/api/msg?type=assetGroup').then(AssetTypesConv)
  },
  getDepartments() {
    return axios.get('/api/org/all').then(resp => resp.data.orgInfos)
  },
  getBriefs(type, state, lastYear) {
    let params = mapParamsToQuery(state, type)
    if (lastYear) {
      params.from = state.period.from.subtract('1', 'years').format(DateFormat)
      params.to = state.period.to.subtract('1', 'years').format(DateFormat)
    }
    if (type === 'left') {
      return axios.get('/api/fa/briefs', {params})
        .then(resp => BriefConv(resp, params.dataType, lastYear))
    } else {
      params.groupby = 'asset'
      return axios.get('/api/fa/briefs', {params})
        .then(resp => BriefAssetConv(resp, params.dataType, lastYear))
    }
  },
  getReasons(from, to) {
    let params = {
      from,
      to,
    }
    return axios.get('/api/fa/reasons', {params}).then(ReasonConv)
  },

}