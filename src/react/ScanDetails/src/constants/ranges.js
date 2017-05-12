import moment from 'moment'

const now = moment()

export default {
  oneWeek: {
    text: '一周内',
    start: now.clone().subtract(7, 'days'),
    end: now
  },
  oneMonth: {
    text: '一月内',
    start: now.clone().subtract(1, 'month'),
    end: now
  },
  oneYear: {
    text: '一年内',
    start: now.clone().subtract(1, 'year'),
    end: now
  },
  currentMonth: {
    text: '本月',
    start: now.clone().startOf('month'),
    end: now.clone().endOf('month')
  },
  yearBeforeLast: {
    text: now.clone().startOf('year').subtract(2, 'year').year(),
    start: now.clone().startOf('year').subtract(2, 'year'),
    end: now.clone().endOf('year').subtract(2, 'year')
  },
  lastYear: {
    text: now.clone().startOf('year').subtract(1, 'year').year(),
    start: now.clone().startOf('year').subtract(1, 'year'),
    end: now.clone().endOf('year').subtract(1, 'year')
  },
  currentYear: {
    text: now.clone().startOf('year').year(),
    start: now.clone().startOf('year'),
    end: now.clone().endOf('year')
  },
  nextYear: {
    text: now.clone().startOf('year').add(1, 'year').year(),
    start: now.clone().startOf('year').add(1, 'year'),
    end: now.clone().endOf('year').add(1, 'year')
  },
  yearAfterNext: {
    text: now.clone().startOf('year').add(2, 'year').year(),
    start: now.clone().startOf('year').add(2, 'year'),
    end: now.clone().endOf('year').add(2, 'year')
  }
}
