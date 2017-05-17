import _ from 'lodash'

const initContext = function() {
  let ctx = document.querySelector('#user-context') || {}
  let fields = ctx.elements || []
  let ctxState = {}
  _.forEach(fields, f => {
    let value = f.value
    if (!Number.isNaN(+value)) {
      value = +value
    } else {
      try {
        value = JSON.parse(value)
      } catch(e) { /* do nothing */}
    }
    ctxState[f.id] = value
  })
  
  // if(!('id' in ctxState)) {
  //   throw new Error('Failed to initialize user context on this page!')
  // }
  return ctxState
}

export default (state) => {
  if (state === undefined) {
    return initContext()
  } 
  return state
}