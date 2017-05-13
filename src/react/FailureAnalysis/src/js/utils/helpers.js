/* eslint-disable */

import _ from 'lodash'
import COLORS from 'utils/colors'
// import randomName from 'random-name'
import { log } from 'utils/logger'
import SID from 'shortid'

export function TODO() { throw Error('TODO') }

export function Random(...args) {

  if (args[0] === undefined) {
    return Math.random()
  }
  if (args.length === 1) {
    return Math.random() * args[0]
  } 
  if (args.length > 1) {
    return Math.random() * (args[1] - args[0]) + args[0]
  }

}

export function RandomInt(...args) {
  return Math.round(Random(...args))
}

export function RandomDivide(count) {
  let arr = ArrayGen(count)(_ => Random())
  let sum = _.sum(arr)
  return arr.map(_ => _ / sum)
}

export function ToPrecentage(number, decimal = 2) {
  return (number * 100).toFixed(decimal) + '%'
}

// export function GenerateTeethData(count, mode, stripCount, colors = []) {

//   const colours = _.values(COLORS)
//   const modes = ['spokerib', 'layer', 'bar']
//   mode = modes.find(_ => _ === mode)

//   function getRandomStrips(count = 2) {
//     let weights
//     if (mode === 'spokerib') {
//       weights = RandomDivide(count)
//     } else if (mode === 'bar') {
//       weights = ArrayGen(count)(Random)
//     } else { // layer
//       weights = RandomDivide(count).map(_ => _ + Random(-.2, .2))
//     }
//     return ArrayGen(count)((_, i) => {
//       let c = colors.length === 0 ? colours : colors
//       return {
//         color: c[RandomInt(c.length - 1)],
//         weight: weights[i]
//       }
//     })
//   }
//   let result = ArrayGen(count)(_ => ({
//       mode: mode || modes[RandomInt(2)],
//       label: randomName.first(),
//       id: SID.generate(),
//       strips: getRandomStrips(stripCount || RandomInt(1, 4))
//     }))
//   log(result)
//   return result
// }

// function ArrayGen(times) {
//   return function (iteratee) {
//     let t = times
//     let arr = Array(t)
//     for (let i = 0; i < t; i++) {
//       arr[i] = iteratee(undefined, i)
//     }
//     return arr
//   }
// }
/**
 * 3x3 Grid coordinate system
 * Defination:
 *   Horizontal coords: L(eft), R(ight), HC(horizontal center)
 *   Vertical coords: T(op), B(ottom), VC(vertical center)
 * 
 * The `parse` function take in a string contains coord chars (case-insensitive), and
 * returns a valid array of H-coords & V-coords. The second parameter indicates strict
 * or loose (default) mode, when in strict, it yeilds error when meets dirty string
 * 
 * Example: 
 *  'lt' or 'tl' or 'ascst^&*l' => ['L', 'T']
 */
export class JiuGongGe {

  static parse(str, { strict, valueMap } = { strict: false }) {
    let result = [,,]
    for (let i = 0; i < str.length; i++) {
      if (_.every(result, e => e !== undefined)) {
        if (!strict) {
          return valueMap ? result.map(_ => valueMap[_]) : result
        } else {
          throw Error('Extra tailing chars: ' + str.substr(i))
        }
      }
      let char = str[i].toUpperCase()
      switch (char) {
        case 'V':
        case 'H':
          let next = str[i + 1].toUpperCase()
          if (next !== 'C') {
            if (strict) throw Error('Invalid char after V/H: ', next)
          } else {
            result[char === 'V' ? 1 : 0] = char + 'C'
            i++
          }
          break
        case 'L':
        case 'R':
          result[0] = char
          break
        case 'T':
        case 'B':
          result[1] = char
          break
        default:
          if (strict) throw Error('Invalid char: ' + char)
      }
    }
    return valueMap ? result.map(_ => valueMap[_]) : result
  }
  
}