
const path = require('path')
const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const CopyWebpackPlugin = require('copy-webpack-plugin')
// const autoprefixer = require('autoprefixer')

module.exports = {
  output: {
    filename: 'assets/js/[name].js',
    path: path.resolve(__dirname, '../build'),
    publicPath: '/'
  },
  resolve: {
    modules: [
      'node_modules',
    ],
    alias: {
      src: path.join(__dirname, '../src'),
      js: path.join(__dirname, '../src/js'),
      sagas: path.join(__dirname, '../src/js/sagas'),
      components: path.join(__dirname, '../src/js/components'),
      containers: path.join(__dirname, '../src/js/containers'),
      actions: path.join(__dirname, '../src/js/actions'),
      reducers: path.join(__dirname, '../src/js/reducers'),
      stores: path.join(__dirname, '../src/js/stores'),
      routes: path.join(__dirname, '../src/js/routes'),
      utils: path.join(__dirname, '../src/js/utils'),
      converters: path.join(__dirname, '../src/js/converters'),
      mocks: path.join(__dirname, '../src/js/mocks'),
      styles: path.join(__dirname, '../src/styles'),
      services: path.join(__dirname, '../src/js/services'),
      config: path.join(__dirname, '../config/index'),
    },
    extensions: ['.js', '.jsx', '.json', '.scss', 'sass', '.css', '.html']
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: 'src/index.html',
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
      inject: true,
    }),
    new webpack.ProvidePlugin({
      'fetch': 'imports?this=>global!exports?global.fetch!whatwg-fetch'  // fetch API
    }),
    // Shared code
    new webpack.optimize.CommonsChunkPlugin({
      name: 'vendor',
      filename: 'assets/js/vendor.bundle.js',
      minChunks: Infinity
    }),
    new CopyWebpackPlugin([{
      from: 'src/assets',
      to: 'assets'
    }])
  ],
  module: {
    loaders: [
      // JavaScript / ES6
      {
        test: /\.jsx?$/,
        include: path.resolve(__dirname, '../src/js'),
        loader: 'babel-loader'
      },
      // Images
      // Inline base64 URLs for <=8k images, direct URLs for the rest
      {
        test: /\.(png|jpg|jpeg|gif|svg)$/,
        loader: 'url',
        query: {
          limit: 8192,
          name: 'images/[name].[ext]?[hash]'
        }
      },
      // Fonts
      {
        test: /\.(woff|woff2|ttf|eot)(\?v=\d+\.\d+\.\d+)?$/,
        loader: 'url',
        query: {
          limit: 8192,
          name: 'fonts/[name].[ext]?[hash]'
        }
      },
      {
        test: /\.s[ca]ss$/,
        use: [
          'style-loader',
          'css-loader',
          'sass-loader',
          'postcss-loader'
        ]
      },
      {
        test: /\.html$/,
        loader: 'html-loader',
      },
      {
        test: /\.less$/,
        use: [
          'style-loader',
          'css-loader',
          { 
            loader : 'less-loader',
            options: {
              modifyVars: require('./theme.js')
            }
          }
        ]
      }
    ]
  },
}