const path = require('path')
const merge = require('webpack-merge')
const webpack = require('webpack')
const ExtractTextPlugin = require('extract-text-webpack-plugin')
const CopyWebpackPlugin = require('copy-webpack-plugin')
const config = require('./webpack.config.base')

const GLOBALS = {
  'process.env': {
    'NODE_ENV': JSON.stringify('production')
  },
  __DEV__: JSON.stringify(JSON.parse(process.env.DEBUG || 'false'))
}

module.exports = merge(config, {
  devtool: 'cheap-module-source-map',
  entry: {
    app: path.join(__dirname, '../src/js/index'),
    vendor: ['react', 'react-dom', 'react-redux', 'react-router', 'react-router-redux', 'redux']
  },
  plugins: [
    new CopyWebpackPlugin([
      {
        from: path.join(__dirname, '../src/assets/img'),
        to: 'images'
      }
    ]),
    new webpack.NoEmitOnErrorsPlugin(),
    new webpack.DefinePlugin(GLOBALS),
    new webpack.optimize.UglifyJsPlugin({
      compress: {
        warnings: false,
        'screw_ie8': true
      },
      output: {
        comments: false
      },
      sourceMap: false
    }),
    new webpack.LoaderOptionsPlugin({
      minimize: true,
      debug: false
    }),
    new ExtractTextPlugin({
      filename: 'assets/css/app.css',
      allChunks: true
    })
  ],
  module: {
    noParse: /\.min\.js$/,
    loaders: [
      // Sass
      {
        test: /\.scss$/,
        include: [
          path.resolve(__dirname, '../src/css'),
        ],
        loader: ExtractTextPlugin.extract({
          fallbackLoader: 'style-loader',
          loader: [
            { loader: 'css-loader', query: { sourceMap: true } },
            'postcss-loader',
            { loader: 'sass-loader', query: { outputStyle: 'compressed' } }
          ]
        })
      },
      {
        test: /\.css$/,
        loader: ExtractTextPlugin.extract({
          fallbackLoader: 'style-loader',
          loader: ['css-loader', 'postcss-loader']
        })
      }
    ]
  },
})
