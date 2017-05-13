
export const ACT_UPDATE_PARAM = 'update/param/'
export function ParamUpdate(type, data){
  return { 
    type: ACT_UPDATE_PARAM + type,
    data: data
  }
}

export const ACT_UPDATE_META = 'update/meta/'
export function MetaUpdate(type = '', data) {
  return {
    type: ACT_UPDATE_META + type,
    data: data
  }
}