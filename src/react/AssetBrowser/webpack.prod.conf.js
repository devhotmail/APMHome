const path = require('path')
const webpack = require('webpack')
const merge = require('webpack-merge')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const ExtractTextPlugin = require('extract-text-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')

const pwd = __dirname
const dirName = path.basename(pwd, path.extname(pwd))

const publicPath = `/geapm/react/${dirName}`

const webpackConfig = merge(baseWebpackConfig, {
  devtool: false,
  output: {
    filename: '[name].[hash].js',
    path: path.resolve(__dirname, './dist'),
    publicPath: publicPath,
    sourceMapFilename: '[name].[hash].map'
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
              name: 'assets/[name].[hash].[ext]'
            }
          }
        ]
      }
    ]
  },  
  plugins: [
    new webpack.NamedModulesPlugin(),
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify('production'),
      },
    }),
    new webpack.LoaderOptionsPlugin({
      minimize: true,
      debug: false
    }),
    new webpack.NamedModulesPlugin(),
    new HtmlWebpackPlugin({
      template: './public/index.xhtml',
      filename: 'index.xhtml',
      appMountId: 'root',
      inject: false
    }),
    new HtmlWebpackPlugin({
      template: './public/index.html',
      filename: 'index.html',
      inject: true
    }),    
    new webpack.optimize.UglifyJsPlugin({
      beautify: false,
      mangle: {
        screw_ie8: true,
        keep_fnames: true
      },
      compress: {
        screw_ie8: true
      },
      comments: false
    }),
    // Put all css code in this file
    new ExtractTextPlugin('[name].[hash].css')
  ]
})

const bundleAnalyzerReport = false

if (bundleAnalyzerReport) {
  var BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin
  webpackConfig.plugins.push(new BundleAnalyzerPlugin())
}

module.exports = webpackConfig
