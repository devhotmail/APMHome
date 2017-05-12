const path = require('path')
const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')

const config = require('../config')

module.exports = {
  devtool: 'source-map',
  output: {
    filename: '[name].js',
    path: config.distDir,
    publicPath: '/',
    sourceMapFilename: '[name].map'
  },
  resolve: {
    extensions: ['.js', '.jsx'],
    modules: [
      'node_modules'
    ],
    alias: {
      '#': path.resolve(__dirname, '../src')
    }
  }
}
