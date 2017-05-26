const merge = require('webpack-merge')
const webpack = require('webpack')
const config = require('./webpack.config.base')
const path = require('path')


const GLOBALS = {
  'process.env': {
    'NODE_ENV': JSON.stringify('development')
  },
  __DEV__: JSON.stringify(JSON.parse(process.env.DEBUG || 'true'))
}

module.exports = merge(config, {
  cache: true,
  devtool: 'cheap-module-eval-source-map',
  entry: {
    app: [
      'webpack/hot/dev-server',
      'react-hot-loader/patch',
      path.join(__dirname, '../src/js/index')
    ],
    vendor: ['react', 'react-dom', 'react-redux', 'react-router', 'react-router-redux', 'redux'],
  },
  plugins: [
    new webpack.HotModuleReplacementPlugin(),
    new webpack.DefinePlugin(GLOBALS)
  ],
  module: {
    loaders: [
      // Sass
      {
        test: /\.scss$/,
        include: [
          path.resolve(__dirname, '../src/css')
        ],
        loaders: [
          'style-loader',
          'css-loader',
          'postcss-loader',
          { loader: 'sass-loader', query: { outputStyle: 'expanded' } }
        ]
      },
    ]
  },
  devServer: {
    noInfo: true,
    overlay: true,
    hot: true,
    publicPath: config.output.publicPath,
    stats: {
      colors: true
    },
    contentBase: path.join(__dirname, "../src"),
    watchContentBase: true,
    historyApiFallback: true,
    port: 8888,
  }
})
