const path = require('path')

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
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: [
          {
            loader: 'babel-loader',
            options: {
              cacheDirectory: process.env.NODE_ENV !== 'production'
            }
          }
        ]
      },
      {
        test: /\.(png|jpe?g|gif|ico|svg)(\?.*)?$/,
        loader: 'url-loader',
        options: {
          limit: 10000,
          name: 'assets/[name].[hash].[ext]'
        },
        exclude: /assets\/icons/
      },
      {
        test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
        loader: 'url-loader',
        options: {
          limit: 10000,
          name: 'assets/[name].[hash].[ext]'
        }
      }
    ]
  }   
}
