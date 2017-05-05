// @flow
import * as Immutable from 'immutable'
import type { Map } from 'immutable'
import * as sagaEffects from 'redux-saga/effects'
import { rpoisson } from 'randgen'
import { groupBy, pickBy } from 'lodash'
import axios from 'axios'
import { randRange } from '#/utils'
import { COLORS, PAGE_SIZE } from '#/constants'

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
    if (!groupByKey) {
      const groupedByAsset = groupBy(filteredScans, scan => scan.asset.id)
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
    } else {
      const groupedBySequence = groupBy(filteredScans, scan => scan.sequence.id)
      const res = Object.keys(groupedBySequence).map(sequenceId => ({
        // $FlowFixMe
        type: groupedBySequence[sequenceId][0].type,
        // $FlowFixMe
        asset: groupedBySequence[sequenceId][0].asset,
        // $FlowFixMe
        sequence: groupedBySequence[sequenceId][0].sequence,
        // $FlowFixMe
        part: groupedBySequence[sequenceId][0].part,
        items: {
          // $FlowFixMe
          desc: [groupedBySequence[sequenceId][0].part],
          data: [{
            // $FlowFixMe
            id: groupedBySequence[sequenceId][0].part.id,
            count: groupedBySequence[sequenceId].length
          }]
        }
      }))
      resolve({
        dom: res
      })
    }
  })
}

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

function* getDetails(actionType, params = {}, { put, call, select }) {
  try {
    const scans = yield select(({ scans }) => scans)
    const from = scans.getIn(['range', 'from'])
    const to = scans.getIn(['range', 'to'])
    let type = scans.get('filters').find(filter => filter.get('key') === 'type')
    if (type) type = type.get('value')
    let part = scans.get('filters').find(filter => filter.get('key') === 'part')
    if (part) part = part.get('value')
    const dept = scans.get('deptSelected')
    const { groupby, pageNum, pageSize } = params
    const { data } = yield call(
      axios.get,
      process.env.NODE_ENV === 'production' ? '/api/scan/detail' : '/geapm/api/scan/detail',
      {
        params: pickBy({
          from,
          to,
          groupby,
          type,
          part,
          dept,
          limit: pageSize !== undefined ? pageSize + 1 : undefined,
          start: pageNum !== undefined ? pageSize * pageNum : undefined
        })
      }
    )
    yield put({
      type: `${actionType}/succeeded`,
      payload: data.detail,
      meta: {
        pageNum,
        pageSize
      }
    })
  } catch(err) {
    yield put({
      type: `${actionType}/failed`,
      payload: err
    })
  }
}

export default {
  namespace: 'scans',
  state: Immutable.Map({
    parts: Immutable.Map(),
    briefs: Immutable.List(),
    details: Immutable.List(),
    detailsByStep: Immutable.List(),
    filters: Immutable.List(),
    range: Immutable.Map({
      from: '2016-01-01',
      to: '2017-01-01'
    }),
    detailsCurPage: 0,
    detailsByStepCurPage: 0,
    pageSize: 15,
    depts: Immutable.fromJS([{"id":5,"parent_id":2,"site_id":2,"hospital_id":2,"name":"超声室","name_en":"cariology Dept"},{"id":6,"parent_id":2,"site_id":2,"hospital_id":2,"name":"设备科","name_en":"Asset Dept"},{"id":4,"parent_id":2,"site_id":2,"hospital_id":2,"name":"放射科","name_en":""}])
  }),
  subscriptions: ({
    setup({ dispatch }) {
      dispatch({
        type: 'brief/get',
        payload: {
          from: '2016-01-01',
          to: '2017-01-01'
        }
      })
      // dispatch({
      //   type: 'detail/get',
      //   payload: {
      //     from: '2016-01-01',
      //     to: '2017-01-01',
      //     groupby: 'asset'
      //   }
      // })
      // dispatch({
      //   type: 'detailByStep/get',
      //   payload: {
      //     from: '2016-01-01',
      //     to: '2017-01-01',
      //     groupby: 'step',
      //     type: 2
      //   }
      // })
      // dispatch({
      //   type: 'depts/get'
      // })
    }
  }: Subscriptions),
  effects: ({
    // *['depts/get']({ payload }, { call, put }) {
    //   try {
    //     const { data } = yield call(
    //       axios.get,
    //       process.env.NODE_ENV === 'production' ? '/api/org/all' : '/geapm/api/org/all'
    //     )
    //     yield put({
    //       type: 'depts/get/succeeded',
    //       payload: data
    //     })
    //   } catch(err) {
    //     yield put({
    //       type: 'depts/get/failed',
    //       payload: err
    //     })
    //   }
    // },
    *['brief/get']({ payload }, { call, put }) {
      try {
        const { data } = yield call(
          axios.get,
          process.env.NODE_ENV === 'production' ? '/api/scan/brief' : '/geapm/api/scan/brief',
          { params: payload }
        )
        yield put({
          type: 'brief/get/succeeded',
          payload: data.brief
        })
      } catch(err) {
        yield put({
          type: 'brief/get/failed',
          payload: err
        })
      }
    },
    *['detail/get']({ payload }, effects){
      yield* getDetails(
        'detail/get',
        payload,
        effects
      )
    },
    *['detailByStep/get']({ payload }, effects){
      yield* getDetails(
        'detailByStep/get',
        payload,
        effects
      )
    },
    *['detail/page/change']({ payload }, effects) {
      const curPage = yield effects.select(({scans}) => scans.get('detailsCurPage'))
      yield* getDetails(
        'detail/get',
        {
          groupby: 'asset',
          pageNum: curPage + payload,
          pageSize: 15
        },
        effects
      )
    },
    *['detailByStep/page/change']({ payload }, effects) {
      const curPage = yield effects.select(({scans}) => scans.get('detailsByStepCurPage'))
      yield* getDetails(
        'detailByStep/get',
        {
          // from: '2016-01-01',
          // to: '2017-01-01',
          groupby: 'step',
          pageNum: curPage + payload,
          pageSize: 15
        },
        effects
      )
    },
    *['range/change'](_, effects) {
      yield effects.put({
        type: 'reset'
      })
      yield* getDetails(
        'detail/get',
        {
          groupby: 'asset',
          pageNum: 0,
          pageSize: PAGE_SIZE
        },
        effects
      )
      yield* getDetails(
        'detailByStep/get',
        {
          groupby: 'step',
          pageNum: 0,
          pageSize: PAGE_SIZE
        },
        effects
      )
    },
    *['dept/set'](_, effects) {
      yield effects.put({
        type: 'reset'
      })
      yield* getDetails(
        'detail/get',
        {
          groupby: 'asset',
          pageNum: 0,
          pageSize: PAGE_SIZE
        },
        effects
      )
      yield* getDetails(
        'detailByStep/get',
        {
          groupby: 'step',
          pageNum: 0,
          pageSize: PAGE_SIZE
        },
        effects
      )
    },
    *['filters/toggle'](_, effects) {
      yield effects.put({
        type: 'reset'
      })
      yield* getDetails(
        'detail/get',
        {
          groupby: 'asset',
          pageNum: 0,
          pageSize: PAGE_SIZE
        },
        effects
      )
      yield* getDetails(
        'detailByStep/get',
        {
          groupby: 'step',
          pageNum: 0,
          pageSize: PAGE_SIZE
        },
        effects
      )
    }
    // addWatcher: [ function* (effects) {
    //   const payload = yield effects.takeLatest(
    //     [
    //       'scans/range/change',
    //       'scans/filters/toggle'
    //     ],
    //     function* (action) {
    //       yield* getDetails(
    //         'detail/get',
    //         {
    //           groupby: 'asset',
    //           pageNum: 0,
    //           pageSize: 15
    //         },
    //         effects
    //       )
    //       yield* getDetails(
    //         'detailByStep/get',
    //         {
    //           groupby: 'step',
    //           pageNum: 0,
    //           pageSize: 15
    //         },
    //         effects
    //       )
    //     }
    //   )
    // }, { type: 'watcher'}]
  }: Effects),
  reducers: ({
    ['dept/set'](state, { payload }) {
      return state.withMutations(state => {
        state
          .set('deptSelected', payload)
          .set('detailsCurPage', 0)
          .set('detailsByStepCurPage', 0)
      })
    },
    ['range/change'](state, { payload }) {
      return state.withMutations(state => {
        state
          .set('range', Immutable.fromJS(payload))
          .set('detailsCurPage', 0).set('detailsByStepCurPage', 0)
      })
    },
    ['brief/get'](state, { payload }) {
      return state.set('loading', true)
    },
    ['brief/get/succeeded'](state, { payload }) {
      return state.withMutations(state => {
        const briefs = Immutable.fromJS(payload)
        const parts = briefs
          .flatMap(brief => brief.getIn(['items', 'desc']))
          .reduce((prev, cur) => prev.set(cur.get('id'), cur), Immutable.Map())
          .mapEntries(([k, v], index) => [k, v.set('color', COLORS[index])])

        state
          .set('briefs', briefs)
          .set('parts', parts)
          .set('loading', false)
      })
    },
    ['detail/get/succeeded'](state, { payload, meta }) {
      return state.withMutations(state => {
        state.set('details', Immutable.fromJS(payload))
        if (meta.pageNum !== undefined) state.set('detailsCurPage', meta.pageNum)
      })
    },
    ['detailByStep/get/succeeded'](state, { payload, meta }) {
      return state.withMutations(state => {
        state.set('detailsByStep', Immutable.fromJS(payload))
        if (meta.pageNum !== undefined) state.set('detailsByStepCurPage', meta.pageNum)
      })
    },
    // ['detail/page/change'](state, { payload }) {
    //   return state.update('detailsCurPage', v => v + payload)
    // },
    // ['detailByStep/page/change'](state, { payload }) {
    //   return state.update('detailsCurPage', v => v + payload)
    // },
    ['filters/toggle'](state, { payload }) {
      return state.update('filters', filters => {
        const index = filters.findIndex(filter => filter.get('key') === payload.key)
        if (index === -1) return filters.push(Immutable.fromJS(payload))
        if (filters.getIn([index, 'value']) === payload.value) return filters.delete(index)
        return filters.withMutations(filters => {
          filters.delete(index).push(Immutable.fromJS(payload))
        })
      }).set('detailsCurPage', 0).set('detailsByStepCurPage', 0)
    },
    ['reset'](state) {
      return state.withMutations(state => {
        state
          .set('detailsCurPage', 0)
          .set('detailsByStepCurPage', 0)
      })
    }
  }: Reducers)
}
