const path = require('path')

module.exports = {
  distDir: path.resolve(__dirname, './dist'),
  targetDir: path.resolve(__dirname, '../../src/main/webapp/react'),
  production: {
    env: {
      'NODE_ENV': JSON.stringify('production'),
      'API_HOST': JSON.stringify('/api'),
      'BASE_HREF': JSON.stringify('/dm.xhtml')
    },
    publicPathPrefix: '/react',
  },
  development: {
    env: {
      'NODE_ENV': JSON.stringify('development'),
      'API_HOST': JSON.stringify('/geapm/api'),
      'BASE_HREF': JSON.stringify('/')
    },
    publicPathPrefix: '/geapm/react'
  },
  watch: {
    env: {
      'NODE_ENV': JSON.stringify('watch'),
      'API_HOST': JSON.stringify('/geapm/api'),
      'BASE_HREF': JSON.stringify('/geapm/dm.xhtml')
    },
    publicPathPrefix: '/geapm/react'
  }
}
