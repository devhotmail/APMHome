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

export function getBlength(str) {
  for (var i = str.length, n = 0; i--;) {
    n += str.charCodeAt(i) > 255 ? 2 : 1;
  }
  return n;
}

export function trimString(str, len, endstr) {
  var len = +len,
    endstr = typeof(endstr) == 'undefined' ? "..." : endstr.toString(),
    endstrBl = getBlength(endstr);

  function n2(a) {
    var n = a / 2 | 0;
    return (n > 0 ? n : 1)
  }//用于二分法查找
  if (!(str + "").length || !len || len <= 0) {
    return "";
  }
  if (len < endstrBl) {
    endstr = "";
    endstrBl = 0;
  }
  var lenS = len - endstrBl,
    _lenS = 0,
    _strl = 0;
  while (_strl <= lenS) {
    var _lenS1 = n2(lenS - _strl),
      addn = getBlength(str.substr(_lenS, _lenS1));
    if (addn == 0) {
      return str;
    }
    _strl += addn
    _lenS += _lenS1
  }
  if (str.length - _lenS > endstrBl || getBlength(str.substring(_lenS - 1)) > endstrBl) {
    return str.substr(0, _lenS - 1) + endstr
  } else {
    return str;
  }
}


export const add = (a: number, b: number): number => a + b

export function round (value: number, precision:number = 1): number {
  var multiplier = Math.pow(10, precision)
  return Math.round(value * multiplier) / multiplier
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
