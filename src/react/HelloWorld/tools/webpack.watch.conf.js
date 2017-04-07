const path = require('path')
const webpack = require('webpack')
const merge = require('webpack-merge')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const ExtractTextPlugin = require('extract-text-webpack-plugin')
const FriendlyErrorsPlugin = require('friendly-errors-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')

const utils = require('./utils')

const publicPath = utils.getProdPublicPath()

module.exports = merge(baseWebpackConfig, {
  devtool: false,
  output: {
    filename: '[name].js',
    publicPath,
    sourceMapFilename: '[name].map'
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: [
          'babel-loader'
        ]
      },
      {
        test: /\.css$/,
        exclude: /node_modules/,
        loader: ExtractTextPlugin.extract({
          fallback: 'style-loader',
          use: 'css-loader',
        }),
      },
      {
        test: /\.(jpg|jpeg|png|gif|ico|svg)$/,
        use: [
          {
            loader:'url-loader',
            options: {
              limit: 10000,
              name: 'assets/[name].[ext]'
            }
          }
        ]
      }
    ]
  },  
  plugins: [
    new webpack.NamedModulesPlugin(),
    new webpack.LoaderOptionsPlugin({
      minimize: true,
      debug: false
    }),
    new webpack.DefinePlugin({
      'process.env': {'NODE_ENV': '"development"'}
    }),    
    new webpack.NamedModulesPlugin(),
    new HtmlWebpackPlugin({
      template: './public/index.xhtml',
      filename: 'index.xhtml',
      appMountId: 'root',
      inject: false
    }),
    // Put all css code in this file
    new ExtractTextPlugin('[name].css'),
    new FriendlyErrorsPlugin()
  ]
})
