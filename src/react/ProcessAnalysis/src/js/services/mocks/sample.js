import { info as _info } from 'utils/logger'
import { briefsByType, briefsBySupplier, briefsByDept } from './briefs'
import { details } from './details'
import { grossByDept, grossBySupplier, grossByType } from './gross'
import { phaseArrival, phaseRespond, phaseETTR } from './phase'

const TAG = '[Mock]: '

let info = msg => _info(TAG + msg)

const Departments = {
  "orgInfos": [{
      "id": 5,
      "parent_id": 2,
      "site_id": 1,
      "hospital_id": 1,
      "name": "超声室",
      "name_en": "cariology Dept"
    },
    {
      "id": 6,
      "parent_id": 2,
      "site_id": 1,
      "hospital_id": 1,
      "name": "设备科",
      "name_en": "Asset Dept"
    },
    {
      "id": 4,
      "parent_id": 2,
      "site_id": 1,
      "hospital_id": 1,
      "name": "放射科",
      "name_en": ""
    }
  ]
}

const AssetTypes = {
  "11": "PET-MR",
  "12": "US",
  "13": "其它",
  "1": "CT",
  "2": "MR",
  "3": "DR",
  "4": "CR",
  "5": "RF",
  "6": "DSA",
  "7": "乳腺机",
  "8": "PET",
  "9": "NM",
  "10": "PET-CT"
}

// todo

export default function (mock) {

  mock.onGet('/api/msg?type=assetGroup').reply(function (config) {
    info(config.url)
    return [200, AssetTypes]
  })

  mock.onGet('/api/org/all').reply(function (config) {
    info(config.url)
    return [200, Departments]
  })

  mock.onGet('/api/process/brief')
    .reply(function (config) {
      info(config.url)
      let params = config.params
      switch (params.groupby) {
        case 'type':
          return [200, simulatePaging(params.limit, params.start, briefsByType)]
        case 'dept':
          return [200, simulatePaging(params.limit, params.start, briefsByDept)]
        default: // 'supplier'
          return [200, simulatePaging(params.limit, params.start, briefsBySupplier)]
      }
    })

  mock.onGet('/api/process/detail')
    .reply(function (config) {
      info(config.url)
      let params = config.params
      return [200, simulatePaging(params.limit, params.start, details)]
    })

  mock.onGet('/api/process/gross')
    .reply(function (config) {
      info(config.url)
      let params = config.params
      switch (params.groupby) {
        case 'type':
          return [200, grossByType]
        case 'dept':
          return [200, grossByDept]
        default: // 'supplier'
          return [200, grossBySupplier]
      }
    })
  mock.onGet('/api/process/phase')
    .reply(function (config) {
      info(config.url)
      let params = config.params
      switch (params.phase) {
        case 'ETTR':
          return [200, phaseETTR]
        case 'arrived':
          return [200, phaseArrival]
        case 'respond':
          return [200, phaseRespond]
      }
      return [200, simulatePaging(params.limit, params.start, details)]
    })
}

function simulatePaging(top, skip = 0, resp) {

  let result = resp.data.slice(skip, skip + top)

  return {
    page: {
      total: resp.data.length,
      skip: skip,
      limit: Math.min(result.length, top)
    },
    data: result
  }
}
