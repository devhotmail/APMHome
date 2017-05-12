import { ranges } from '#/constants'

const defaultPresets = ranges

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
