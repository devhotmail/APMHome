const path = require('path')
const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')

module.exports = {
  devtool: 'source-map',
  entry: {
    app: './src/index.js'
  },
  output: {
    filename: '[name].js',
    path: path.resolve(__dirname, './dist'),
    publicPath: '/',
    sourceMapFilename: '[name].map'
  },
  resolve: {
    extensions: ['.js', '.jsx'],
    modules: [
      'node_modules'
    ],
    alias: {
      '#': path.resolve(__dirname, './src')
    }
  }
}
