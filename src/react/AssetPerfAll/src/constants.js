import moment from 'moment'

export const COLORS = ['#8fd3c7', '#cbb862', '#ba82bb', '#9eb7f2', '#82b1d1', '#b8bb83', '#9d9cbe', '#99b992']
export const BACKGROUND_COLORS = ['#d7e5e3', '#e4e0cf', '#e1d5e1', '#dbe0ec', '#d5dfe5', '#e0e1d5', '#dbdae1', '#dae0d8']
export const ROOT_COLOR = "#77a3e7"
export const ROOT_BACKGROUND_COLOR = "#d3dce9"

export const PAGE_SIZE = 7

export const API_HOST = process.env.NODE_ENV === 'production' ? '/api' : '/geapm/api'

export function disabledDate(current) {
  // can not select days after today
  // and can not select days before three years ago
  return current && (current.valueOf() > now || current < moment(now).subtract(3, 'year'))
}


export const now = new Date()
export const currentYear = now.getFullYear()
export const dateFormat = 'YYYY-MM-DD'


const momentNow = moment()

export const ranges = {
  oneWeek: {
    text: '一周内',
    start: momentNow.clone().subtract(7, 'days'),
    end: momentNow
  },
  oneMonth: {
    text: '一月内',
    start: momentNow.clone().subtract(1, 'month'),
    end: momentNow
  },
  oneYear: {
    text: '一年内',
    start: momentNow.clone().subtract(1, 'year'),
    end: momentNow
  },
  currentMonth: {
    text: '本月',
    start: momentNow.clone().startOf('month'),
    end: momentNow.clone().endOf('month')
  },
  yearBeforeLast: {
    text: momentNow.clone().startOf('year').subtract(2, 'year').year(),
    start: momentNow.clone().startOf('year').subtract(2, 'year'),
    end: momentNow.clone().endOf('year').subtract(2, 'year')
  },
  lastYear: {
    text: momentNow.clone().startOf('year').subtract(1, 'year').year(),
    start: momentNow.clone().startOf('year').subtract(1, 'year'),
    end: momentNow.clone().endOf('year').subtract(1, 'year')
  },
  currentYear: {
    text: momentNow.clone().startOf('year').year(),
    start: momentNow.clone().startOf('year'),
    end: momentNow.clone().endOf('year')
  },
  nextYear: {
    text: momentNow.clone().startOf('year').add(1, 'year').year(),
    start: momentNow.clone().startOf('year').add(1, 'year'),
    end: momentNow.clone().endOf('year').add(1, 'year')
  },
  yearAfterNext: {
    text: momentNow.clone().startOf('year').add(2, 'year').year(),
    start: momentNow.clone().startOf('year').add(2, 'year'),
    end: momentNow.clone().endOf('year').add(2, 'year')
  }
}
