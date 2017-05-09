*['data/get/1'] ({ payload: params }, { put, call, select }) {
  try {
    const data = params.groupby === 'dept'
      ? require('#/mock/deptData').default
      : require('#/mock/typeData').default

    yield put({
      type: 'query/update',
      payload: params
    })

    yield put({
      type: 'data/get/succeed',
      payload: data
    })

    yield put({
      type: 'focus/cursor/reset'
    })

    yield put({
      type: 'nodeList/data/get',
      payload: data
    })
  } catch (err) {
    yield put({
      type: 'data/get/failed',
      payload: err
    })
  }
}