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
export const PATH_PHASE = PREFIX + 'api/process/phase'

export const DataTypeMapping = {
  'response_time': 'respond',
  'ettr': 'ETTR',
  'arrival_time': 'arrived'
}

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
  },
  getPhase(params) {

    return axios.get(PATH_PHASE, { params }).then(_ => {
      _.data.nodes = [0, params.t1, params.t2, params.tmax, Infinity ]
      return _.data
    })
  }
  
}