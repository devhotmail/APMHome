import moment from 'moment'

export ranges from './ranges'

export const now = new Date()
export const currentYear = now.getFullYear()
export const dateFormat = 'YYYY-MM-DD'

export const COLORS = [
  '#6ab4a6',
  '#d5c165',
  '#ba82b7',
  '#83b2d1',
  '#99ba93',
  '#ba9cbb',
  '#829fd1',
  '#93bab3',
  '#9aa5b5'
]

export const PAGE_SIZE = 15

export function disabledDate(current) {
  // can not select days after today
  // and can not select days before three years ago
  return current && (current.valueOf() > now || current < moment(now).subtract(3, 'year'))
}
