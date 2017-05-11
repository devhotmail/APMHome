export ranges from './ranges'

export const margin = 20
export const stackHeight = 120
export const pageSize = 15
export const defaultPage = 1

export const now = new Date()
export const currentYear = now.getFullYear()
export const dateFormat = 'YYYY-MM-DD'

export const ORDER = 'order'
export const HOUR = 'hour'
export const RATE = 'rate'

export const colorSet = {
  [HOUR]: [
    'rgb(106,180,166)',
    'rgb(123,190,178)',
    'rgb(135,203,190)',
    'rgb(154,201,192)'
  ],
  [ORDER]: [
    'rgb(187, 129, 184)',
    'rgb(137, 96, 137)'
  ],
  [RATE]: [
    'rgb(214, 194, 94)'
  ]
}