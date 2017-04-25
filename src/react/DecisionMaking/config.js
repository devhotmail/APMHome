const path = require('path')

module.exports = {
  useFlowRuntime: true,
  distDir: path.resolve(__dirname, './dist'),
  targetDir: path.resolve(__dirname, '../../src/main/webapp/react'),
  production: {
    env: {
      'NODE_ENV': JSON.stringify('production'),
      'API_HOST': JSON.stringify('/api')    
    },
    publicPathPrefix: '/react',
  },
  development: {
    env: {
      'NODE_ENV': JSON.stringify('development'),
      'API_HOST': JSON.stringify('/geapm/api')    
    },
    publicPathPrefix: '/geapm/react'
  }
}
