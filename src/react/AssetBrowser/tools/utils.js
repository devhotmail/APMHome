const path = require('path')
const vfs = require('vinyl-fs')

const config = require('../config')

function getRootDir() {
  const pwd = path.resolve(__dirname, '../')
  return path.basename(pwd, path.extname(pwd))
}

function getProdPublicPath() {
  const dirName = getRootDir()
  return `${config.publicPathPrefix}/${dirName}`
}

function symlink() {
  const distDir = config.distDir
  const dirName = getRootDir()
  const targetDir = path.resolve(__dirname, `../../../src/main/webapp/react/${dirName}/`)

  vfs.src(distDir, {followSymlinks: false, base: 'dist'})
  .pipe(vfs.symlink(targetDir))
  .on('end', () => {
    console.log(`🚀 Symlinked: ${distDir} -> ${targetDir} 🚀`)
  })
}


module.exports = {
  getRootDir,
  getProdPublicPath,
  symlink
}
