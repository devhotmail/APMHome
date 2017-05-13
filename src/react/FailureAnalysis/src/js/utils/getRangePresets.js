import moment from 'moment'

const now = moment()

export const Ranges = {
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
    end: now
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

const defaultPresets = Ranges

export default (configs) => {
  let rangePresets = []
  if (!Array.isArray(configs) || (Array.isArray(configs) && !configs.length)) {
    rangePresets = Object.keys(defaultPresets).map(key => defaultPresets[key])
  } else {
    // configs filter duplicated items
    rangePresets = configs.filter((config, i, array) => {
      return i === array.indexOf(config)
    }).map(function (config) {
      if (typeof config === 'string' && defaultPresets[config]) {
        return defaultPresets[config]
      }

      let key = config.key
      if (!key) return

      let displayText = config.displayText
      let startDateTime = config.startDateTime
      let endDateTime = config.endDateTime

      // startDateTime < endDateTime
      if (
        displayText
        && moment.isMoment(startDateTime)
        && moment.isMoment(endDateTime)
        && startDateTime < endDateTime
      ) {
        return config
      }

      if (defaultPresets[key]) return defaultPresets[key]
    }).filter(n => n)
  }

  return rangePresets
}

export function DisabledDate(current) {
  // can not select days after today
  // and can not select days before three years ago
  return current && (current.valueOf() > now || current < moment(now).subtract(3, 'year'))
}