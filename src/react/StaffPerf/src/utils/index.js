/* @flow */
import type { NodeT, cursorT } from '#/types'

export function getSum(arr: Array<number>) {
  return arr.reduce((a, b) => a + b, 0)
}

export function debounce(func, wait = 500, immediate) {
  let timeout
  return function() {
    const args = arguments
    const later = () => {
      timeout = null
      if (!immediate) func.apply(this, args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

export const getRadian = (angle: number) => angle * Math.PI / 180

export const getAngle = (radian: number) => radian * 180 / Math.PI

export const add = (a: number, b: number): number => a + b

export function round (value: number, precision:number = 1): number {
  var multiplier = Math.pow(10, precision)
  return Math.round(value * multiplier) / multiplier
}

export const getCursor = (node: NodeT): cursorT => {
  const { depth, data: { id }} = node
  return [ id, depth ]
}

export const isSameCursor = (a: cursorT, b: cursorT): boolean => {
  return Array.isArray(a) && Array.isArray(b) && a[0] === b[0] && a[1] === b[1]
}

export const isFocusNode = (node: NodeT, cursor: cursorT): boolean => {
  return isSameCursor(getCursor(node), cursor)
}
