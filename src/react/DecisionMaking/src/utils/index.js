/* @flow */
type User = {
  id: number;
  name: string;
}

export function rename(person: User):number {
  return person.id + 2
}


export const add = (a: number, b: number): number => a + b

export function round (value, precision = 0) {
  var multiplier = Math.pow(10, precision)
  return Math.round(value * multiplier) / multiplier
}
