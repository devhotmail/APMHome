const webpack = require('webpack')
const merge = require('webpack-merge')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const ExtractTextPlugin = require('extract-text-webpack-plugin')
const FriendlyErrorsPlugin = require('friendly-errors-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const utils = require('./utils')
const config = require('../config')

const publicPath = utils.getProdPublicPath(config.watch.commonPrefix)

module.exports = merge(baseWebpackConfig, {
  devtool: '#inline-cheap-module-source-map',
  entry: {
    vendor: [
      'babel-polyfill'
    ],
    app: './src/index.js'
  },
  output: {
    filename: '[name].js',
    publicPath,
    sourceMapFilename: '[name].map'
  },
  module: {
    rules: [
      {
        test: /\.html$/,
        use: [
          {
            loader: 'html-imports-loader/react',
            options: {
              emitFile: true
            }
          }
        ],
        include: /bower_components/
      },
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
          use:[
            'css-loader',
            'postcss-loader'
          ]
        }),
      },
      {
        test: /\.s[ca]ss$/,
        loader: ExtractTextPlugin.extract({
          fallback: 'style-loader',
          use: [
            {
              loader: 'css-loader',
              options: {
                module: true,
                importLoaders: 1,
                localIdentName: '[local]__[hash:base64:5]'
              }
            },
            'sass-loader',
            'postcss-loader'
          ]
        })
      },
      {
        test: /\.less$/,
        loader: ExtractTextPlugin.extract({
          fallback: 'style-loader',
          use: [
            {
              loader: 'css-loader',
              options: {
                importLoaders: 1
              }
            },
            {
              loader: 'less-loader',
              options: {
                modifyVars: require('./theme')
              }
            }
          ]
        }),
        include: /node_modules/
      },
      {
        test: /\.(jpg|jpeg|png|gif|ico|svg)$/,
        use: [
          {
            loader:'url-loader',
            options: {
              limit: 10000,
              name: '/assets/[name].[ext]'
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
      'process.env': config.development.env
    }),
    new HtmlWebpackPlugin({
      template: './public/index.xhtml',
      filename: 'index.xhtml',
      appMountId: 'root',
      inject: false,
      chunksSortMode: (c1, c2) => {
        const orders = ['common', 'vendor', 'app']
        const o1 = orders.indexOf(c1.names[0])
        const o2 = orders.indexOf(c2.names[0])
        return o1 - o2
      }
    }),
    new webpack.WatchIgnorePlugin([
      /node_modules/
    ]),
    // Put all css code in this file
    new ExtractTextPlugin('[name].css'),
    new FriendlyErrorsPlugin()
  ]
})
