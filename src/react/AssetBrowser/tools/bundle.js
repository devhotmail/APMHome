const path = require('path')
const ora = require('ora')
const cliSpinners = require('cli-spinners')
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

const spinner = ora({
  color: 'green',
  text: ` Building for ${mode} mode...`,
  spinner: cliSpinners.bouncingBar
})

spinner.start()

rimraf(config.distDir, err => {
  if (err) throw err

  compiler.run((err, stats) => {
    if (err) {
      throw err
      spinner.fail(chalk.red(`Build failed for Project ${pkg.name}.\n`))
    }
    spinner.succeed(chalk.green(`Build complete for Project ${pkg.name}.\n`))
    process.stdout.write(statsOutput(stats) + '\n\n')
    utils.symlink()
  })

  if (isWatchMode) {
    compiler.watch({
      aggregateTimeout: 300,
      poll: 1000  
    }, (err, stats) => {
      if (err) throw err
      console.log(chalk.bgGreen(chalk.black(` Files updated and you can reload it manually.\n`)))
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
