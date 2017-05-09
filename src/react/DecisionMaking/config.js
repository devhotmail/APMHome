const path = require('path')

module.exports = {
  distDir: path.resolve(__dirname, './dist'),
  targetDir: path.resolve(__dirname, '../../src/main/webapp/react'),
  production: {
    env: {
      'NODE_ENV': JSON.stringify('production'),
      'API_HOST': JSON.stringify('/api')
    },
    publicPathPrefix: '/react'
  },
  development: {
    env: {
      'NODE_ENV': JSON.stringify('development'),
      'API_HOST': JSON.stringify('https://www.easy-mock.com/mock/590e9930f926ef14e269a377/api')
    },
    publicPathPrefix: '/geapm/react'
  },
  watch: {
    env: {
      'NODE_ENV': JSON.stringify('watch'),
      'API_HOST': JSON.stringify('/geapm/api')
    },
    publicPathPrefix: '/geapm/react'
  }
}
