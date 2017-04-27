// @flow
import * as Immutable from 'immutable'
import type { Map } from 'immutable'
import * as sagaEffects from 'redux-saga/effects'
import { rpoisson } from 'randgen'
import { groupBy } from 'lodash'
import { randRange } from '#/utils'

type Type = {
  id: number|string,
  name: string
}

type Part = {
  id: number|string,
  name: '头部'|'胸部'| '腹部'|'脊柱'|'血管照影'|'大部位'|'第七个部位'|'最后一个部位'
}

type Subpart = {
  id: number|string,
  name: string
}

type Sequence = {
  id: number|string,
  name: string
}

type Asset = {
  id: number|string,
  name: string
}

type Scan = {
  id: number|string,
  type: Type,
  part: Part,
  subpart?: Subpart,
  sequence: Sequence,
  asset: Asset
}

const types = ['CT', 'MRI', '超声', 'DR', '乳腺仪', '胃肠X光机器', '第七个类型', '第八个类型', '其他']
const parts = ['头部', '胸部',  '腹部', '脊柱', '血管照影', '大部位'
  // , '第七个部位', '最后一个部位'
]

const scans: Scan[] = []

for (let i = 0; i < ~~randRange(200, 400); i++) {
  const typeIndex = ~~randRange(0, types.length)
  const partIndex = ~~randRange(0, parts.length)
  const subpartIndex = ~~randRange(0, 100)
  const sequenceIndex = ~~randRange(0, 100)
  const assetIndex = ~~randRange(0, 100)
  const scan: Scan = {
    id: i,
    type: {
      id: typeIndex,
      name: types[typeIndex]
    },
    part: {
      id: partIndex,
      name: parts[partIndex]
    },
    subpart: {
      id: subpartIndex,
      name: `subpart ${subpartIndex}`
    },
    sequence: {
      id: sequenceIndex,
      name: `sequence ${sequenceIndex}`
    },
    asset: {
      id: assetIndex,
      name: `assetIndex ${assetIndex}`
    }
  }
  scans.push(scan)
}


type BriefItems = {
  desc: Part[],
  data: {
    id: string|number,
    count: number
  }[]
}

type Brief = {
  type: {
    id: number|string,
    name: string
  },
  items: BriefItems
}

type BriefResponse = {
  brief: Brief[]
}


function fetchBrief(endpoint?: string): Promise<BriefResponse> {
  return new Promise((resolve, reject) => {
    type GroupedScans = {
      [string|number]: Scan[]
    }
    const groupedScans = groupBy(scans, scan => scan.type.id)

    const groupedAgainByPart = Object.keys(groupedScans).map((typeId: string|number): GroupedScans => groupBy(groupedScans[typeId], scan => scan.part.id))


    const res = groupedAgainByPart.map(groupedByParts => ({
      // $FlowFixMe
      type: Object.values(groupedByParts)[0][0].type,
      items: {
        // $FlowFixMe
        desc: Object.values(groupedByParts).map((value: Scan[]): Part => value[0].part),
        data: Object.values(groupedByParts).map(value => ({
          // $FlowFixMe
          id: value[0].part.id,
          // $FlowFixMe
          count: value.length
        }))
      }
    }))

    resolve({
      brief: res
    })
  })
}

function fetchDetail(endpoint: string = '', filters: Array<{key: string, value: string|number}> = [], groupByKey?: 'sequence', start?: number, limit?: number): Promise<*> {
  return new Promise((resolve, reject) => {
    const filteredScans = scans.filter(scan => filters.reduce((prev, cur) => prev && scan[cur.key].id === cur.value, true))
    const groupedByAsset = groupBy(filteredScans, scan => scan.asset.id)
    if (!groupByKey) {
      const res = Object.keys(groupedByAsset).map(key => ({
        // $FlowFixMe
        type: groupedByAsset[key][0].type,
        // $FlowFixMe
        asset: groupedByAsset[key][0].asset,
        items: {
          // $FlowFixMe
          desc: Object.values(groupBy(groupedByAsset[key], scan => scan.part.id)).map(value => value[0].part),
          data: Object.values(groupBy(groupedByAsset[key], scan => scan.part.id)).map(value => ({
            // $FlowFixMe
            id: value[0].part.id,
            // $FlowFixMe
            count: value.length
          }))
        }
      }))
      resolve({
        dom: res
      })
    }
  })
}
//
// type Asset = {
//   id: string|number,
//   name: string
// }
//
// type DetailItems = {
//   desc: Part[],
//   data: Scan[]
// }
//
// type Detail = {
//   type: Type,
//   asset: Asset,
//   items: DetailItems
// }
//
// type DetailResponse = {
//   dom: Detail[]
// }

// function fetchDetail(endpoint?: string): Promise<*> {
//   return new Promise((resolve, reject) => {
//     const detailRes:DetailResponse = {
//       dom: []
//     }
//
//     for (let i = 0; i < 20; i++) {
//       const scans: Scan[] = []
//       for (let j = 0; j < 6; j++) {
//         scans.push({
//           id: j + 1,
//           count: ~~(rpoisson() * 100)
//         })
//       }
//
//       detailRes.dom.push({
//         type: {
//           id: ~~(Math.random() * 6) + 1,
//           name: ['MRI', 'CT', '超声', 'DR', '乳腺仪', '胃肠X光机器'][i % 6],
//         },
//         asset: {
//           id: i + 1,
//           name: 'asset' + i
//         },
//         items: {
//           desc: [
//             { id: 1, name: '头部'},
//             { id: 2, name: '胸部'},
//             { id: 3, name: '腹部'},
//             { id: 4, name: '脊柱'},
//             { id: 5, name: '血管照影'},
//             { id: 6, name: '大部位'}
//           ],
//           data: scans
//         }
//       })
//     }
//
//     resolve(detailRes)
//   })
// }

type Action = {
  type: string,
  [string]: any
}

type State = Map<string, any>

type Reducer = ( State, Action ) => State

type Reducers = {
  [string]: Reducer
}

type Effect = ( Action, typeof sagaEffects ) => Generator<mixed, void, any>

type Effects = {
  [string]: Effect
}

type Subscription = ({
  dispatch: {type: string} => any,
  location: any
}) => void

type Subscriptions = {
  [string]: Subscription
}

export default {
  namespace: 'scans',
  state: Immutable.Map({
    briefs: Immutable.List(),
    details: Immutable.List(),
    filters: Immutable.List()
  }),
  subscriptions: ({
    setup({ dispatch }) {
      dispatch({
        type: 'brief/get'
      })
      dispatch({
        type: 'detail/get'
      })
    }
  }: Subscriptions),
  effects: ({
    *['brief/get'](_, { put, select, call }){
      try {
        const briefRes = yield call(fetchBrief)
        yield put({
          type: 'brief/get/succeeded',
          payload: briefRes
        })
      } catch(err) {
        yield put({
          type: 'brief/get/failed',
          payload: err
        })
      }
    },
    *['detail/get'](_, { put, select, call }){
      try {
        const detailRes = yield call(fetchDetail)
        console.log(detailRes);
        yield put({
          type: 'detail/get/succeeded',
          payload: detailRes
        })
      } catch(err) {
        yield put({
          type: 'detail/get/failed',
          payload: err
        })
      }
    }
  }: Effects),
  reducers: ({
    ['brief/get'](state, { payload }) {
      return state.set('loading', true)
    },
    ['brief/get/succeeded'](state, { payload }) {
      return state.withMutations(state => {
        state
          .set('briefs', Immutable.fromJS(payload.brief))
          .set('loading', false)
      })
    },
    ['detail/get/succeeded'](state, { payload }) {
      return state.withMutations(state => {
        state.set('details', Immutable.fromJS(payload.dom))
      })
    },
    ['filters/toggle'](state, { payload }) {
      return state.update('filters', filters => {
        const index = filters.findIndex(filter => filter.get('key') === payload.key)
        if (index === -1) return filters.push(Immutable.fromJS(payload))
        if (filters.getIn([index, 'value']) === payload.value) return filters.delete(index)
        return filters.withMutations(filters => {
          filters.delete(index).push(Immutable.fromJS(payload))
        })
      })
    }
  }: Reducers)
}
