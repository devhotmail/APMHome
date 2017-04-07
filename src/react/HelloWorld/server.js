const express = require('express')

const PORT = process.env.VCAP_APP_PORT || process.env.PORT || 8128

const app = express()

app.use(require('connect-history-api-fallback')())
app.use(express.static('./dist'))

module.exports = app.listen(PORT, function (err) {
  if (err) {
    console.log(err)
    return
  }
  console.log('Listening at http://localhost:' + PORT + '\n')
})
