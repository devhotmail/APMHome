const path = require('path')
const merge = require('webpack-merge')
const webpack = require('webpack')
const ExtractTextPlugin = require('extract-text-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const CopyWebpackPlugin = require('copy-webpack-plugin')
const config = require('./webpack.config.base')

const GLOBALS = {
  'process.env': {
    'NODE_ENV': JSON.stringify('production'),
    LOCAL: JSON.parse(process.env.LOCAL || 'false')
  },
  __DEV__: JSON.stringify(JSON.parse(process.env.DEBUG || 'false')),
}
const pwd = path.resolve(__dirname, '../')
const root = path.basename(pwd, path.extname(pwd))
const isLocal = JSON.parse(process.env.LOCAL || 'false')

module.exports = merge(config, {
  devtool: 'cheap-module-source-map',
  output: {
    publicPath: isLocal ? '/geapm/react/' + root : '/react/' + root
  },
  entry: {
    app: path.join(__dirname, '../src/js/index'),
    vendor: ['babel-polyfill', 'react', 'react-dom', 'react-redux', 'react-router', 'react-router-redux', 'redux']
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: 'src/index.xhtml',
      filename: 'index.xhtml',
      appMountId: 'app',
      minify: {
        removeComments: true,
        collapseWhitespace: true,
        removeRedundantAttributes: true,
        useShortDoctype: true,
        removeEmptyAttributes: true,
        removeStyleLinkTypeAttributes: true,
        keepClosingSlash: true,
        minifyJS: true,
        minifyCSS: true,
        minifyURLs: true,
      },
      inject: false,
    }),
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
