/* @flow */
type User = {
  id: number;
  name: string;
}

export function rename(person: User):number {
  return person.id + 2
}

export const add = (a: number, b: number): number => a + b

export function mockRoot (root) {
  return {
    ...root,
    id: null,
    name: '全部设备',
    isRoot: true
  }
}

export function round (value: number, precision:number = 1): number {
  var multiplier = Math.pow(10, precision)
  return Math.round(value * multiplier) / multiplier
}
