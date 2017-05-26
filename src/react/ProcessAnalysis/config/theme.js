const path = require('path')
const config = require('./index')

const theme = {
  '@btn-border-radius-base': '14px',
  '@border-color-base': '#bababa',
  '@border-color-split': '#bababa',
  '@btn-primary-bg': '#5b84d7'
}

const { NODE_ENV, LOCAL } = process.env
if (NODE_ENV === 'production') {
  const commonPrefix = LOCAL === 'true' ?  '/geapm' : '/'
  const iconUrl = path.join(commonPrefix, 'resources/fonts/anticon/iconfont')

  Object.assign(theme, {
    '@icon-url': JSON.stringify(iconUrl)
  })
}

module.exports = theme