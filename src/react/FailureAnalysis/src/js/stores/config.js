if (process.env.NODE_ENV === 'production') {
  module.exports = require('./config.prod').default
} else {
  module.exports = require('./config.dev').default
}
