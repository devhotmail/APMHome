const merge = require('webpack-merge')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const webpack = require('webpack')
const FriendlyErrorsPlugin = require('friendly-errors-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')

module.exports = merge(baseWebpackConfig, {
  devtool: '#cheap-module-eval-source-map',
  output: {
    path: path.resolve(__dirname, './bundle'),
    publicPath: 'dist',
    sourceMapFilename: '[name].map'
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
    new ExtractTextPlugin('[name].css'),
  ] 
})
