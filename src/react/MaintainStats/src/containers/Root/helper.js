import { quality, completion } from '#/constants'

const purple = '#b781b4'
const prasinous = '#6ab6a6'
const gray = '#b7b7b7'

export function formatData (data) {
  return data.map(n => {
    const { key, val } = n

    const completePercent = val[completion].completed / val[completion].all
    const qualityPercent = val[quality].repair / val[quality].all

    return {
      id: key.id,
      name: key.name,
      origin: data,
      [completion]: [
        {
          color: purple,
          value: completePercent
        },
        {
          color: gray,
          value: 1 - completePercent
        }
      ],
      [quality]: [
        {
          color: prasinous,
          value: qualityPercent
        },
        {
          color: gray,
          value: 1 - qualityPercent
        }     
      ]
    }
  })
}
