// @flow

export function randRange(min:number, max:number):number {
  return min + (max - min) * Math.random()
}

export function valueToCoordinate(count: number, countRange: [number, number], coordinateRange: [number, number]): number {
  return count / countRange[1] * (coordinateRange[1] - coordinateRange[0])
}

export function getRange(arr: number[] | IndexedIterable<number>):[number, number] {
  return arr.reduce((prev, cur) => {
    if (cur > prev[1]) prev[1] = cur
    if (cur < prev[0]) prev[0] = cur
    return prev
  }, [+Infinity, -Infinity])
}
