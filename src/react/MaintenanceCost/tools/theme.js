const path = require('path')
const config = require('../config')

const theme = {
  '@btn-border-radius-base': '14px',
  '@border-color-base': '#bababa',
  '@border-color-split': '#bababa',
  '@btn-primary-bg': '#5b84d7'
}

const { NODE_ENV } = process.env
if (~['watch', 'production'].indexOf(NODE_ENV)) {
  const { commonPrefix = '/' } = config[process.env.NODE_ENV]

  const iconUrl = path.join(commonPrefix, 'resources/fonts/anticon/iconfont')

  Object.assign(theme, {
    '@icon-url': JSON.stringify(iconUrl)
  })
}

module.exports = theme
