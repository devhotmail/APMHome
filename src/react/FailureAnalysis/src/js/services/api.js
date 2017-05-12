import axios from 'axios'
import { load } from './mocks/helper'
import { AssetTypesConv, BriefConv, ReasonConv, BriefAssetConv } from 'converters'
import _ from 'lodash'

const UseMock = true
if (process.env.NODE_ENV === 'development' && UseMock) {
  load('./sample')
}

export default {

  getAssetTypes() {
    return axios.get('/api/msg?type=assetGroup').then(AssetTypesConv)
  },
  getDepartments() {
    return axios.get('/api/org/all').then(resp => resp.data.orgInfos)
  },
  getBriefs(type) {
    if (type === 'left'){
      return axios.get('/api/fa/briefs').then(BriefConv)
    } else {
      return axios.get('/api/fa/briefs/byasset').then(BriefAssetConv)
    }
  },
  getReasons() {
    return axios.get('/api/fa/reasons').then(ReasonConv)
  },


}