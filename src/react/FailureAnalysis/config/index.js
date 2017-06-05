var path = require('path')
var assign = require('es6-object-assign').assign

var env = process.env.NODE_ENV === 'development' ? 
           require('./dev') : require('./prod')

var base = {
  distDir: path.resolve(__dirname, '../build'),
  targetDir: path.resolve(__dirname, '../../../src/main/webapp/react'),
  production: {
    env: {
      'API_HOST': JSON.stringify('/api')
    },
    commonPrefix: '/'
  },
  development: {
    env: {
      'API_HOST': JSON.stringify('https://www.easy-mock.com/mock/590e9930f926ef14e269a377/api')
    },
    commonPrefix: '/'
  },
  watch: {
    env: {
      'NODE_ENV': JSON.stringify('watch'),
      'API_HOST': JSON.stringify('https://www.easy-mock.com/mock/590e9930f926ef14e269a377/api')
    },
    commonPrefix: '/geapm'
  }
}

module.exports = assign(base, env)
