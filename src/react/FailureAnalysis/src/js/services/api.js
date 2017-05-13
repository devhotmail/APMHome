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
function mapParamsToQuery(params) {
  return {
    from: params.period.from.format(DateFormat),
    to: params.period.to.format(DateFormat),
    groupby: GroupBy[params.display],
    orderby: DataTypeMapping[params.orderBy],
    dataType: DataTypeMapping[params.dataType],
  }
}

export default {

  getAssetTypes() {
    return axios.get('/api/msg?type=assetGroup').then(AssetTypesConv)
  },
  getDepartments() {
    return axios.get('/api/org/all').then(resp => resp.data.orgInfos)
  },
  getBriefs(type, state) {
    let params = mapParamsToQuery(state)
    if (type === 'left'){
      return axios.get('/api/fa/briefs', {params})
        .then(resp => BriefConv(resp, params.dataType))
    } else {
      params.groupby = 'asset'
      return axios.get('/api/fa/briefs', {params})
        .then(resp => BriefAssetConv(resp, params.dataType))
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