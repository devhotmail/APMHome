export type cursorT = Array<string | number, number>

export type NodeT = {
  depth: number,
  height: number,
  r: number,
  value: number,
  x: number,
  y: number,
  data: Object,
  parent
}

export type NodeListT = Array<NodeT>

export type ConfigT = {
  config: Array<Object>,
  focus: Object,
  depths: Array<number>,
  setFocus: Function
}
