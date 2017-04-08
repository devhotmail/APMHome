const express = require('express')

const PORT = process.env.VCAP_APP_PORT || process.env.PORT || 8118
const isDev = process.env.NODE_ENV === 'development'

const app = express()

if (isDev) require('./tools/dev-module')(app)

app.use(require('connect-history-api-fallback')())
if (!isDev) app.use(express.static('./dist'))

module.exports = app.listen(PORT, function (err) {
  if (err) {
    console.log(err)
    return
  }
  console.log('Listening at http://localhost:' + PORT + '\n')
})
