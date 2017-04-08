const path = require('path')
const ora = require('ora')
const chalk = require('chalk')
const webpack = require('webpack')
const rimraf = require('rimraf')
const utils = require('./utils')
const config = require('../config')
const pkg = require('../package.json')

const isWatchMode = process.env.NODE_ENV === 'watch'
if (!isWatchMode) process.env.NODE_ENV = 'production'

const mode = isWatchMode ? 'watch' : 'prod'

const webpackConfig = require(`./webpack.${mode}.conf`)

const compiler = webpack(webpackConfig)

const spinner = ora(`building for ${mode} mode...`)
spinner.start()

rimraf(config.distDir, err => {
  if (err) throw err

  compiler.run((err, stats) => {
    spinner.stop()
    if (err) throw err
    process.stdout.write(statsOutput(stats) + '\n\n')

    console.log(chalk.cyan(`  Build complete for Project ${pkg.name}.\n`))
    utils.symlink()
  })

  if (isWatchMode) {
    compiler.watch({
      aggregateTimeout: 300,
      poll: 1000  
    }, (err, stats) => {
      process.stdout.write(statsOutput(stats) + '\n\n')
    })
  }
})

function statsOutput(stats) {
  return stats.toString({
    colors: true,
    modules: false,
    children: false,
    chunks: false,
    chunkModules: false
  })
}
