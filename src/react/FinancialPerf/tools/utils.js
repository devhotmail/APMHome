const path = require('path')
const vfs = require('vinyl-fs')

const config = require('../config')

function getProdPublicPath(prefix) {
  const dirName = getRootDir()
  return `${prefix}/${dirName}`
}

function getRootDir() {
  const pwd = path.resolve(__dirname, '../')
  return path.basename(pwd, path.extname(pwd))
}

function symlink(distDir, targetDir) {
  const dirName = getRootDir()

  vfs.src(distDir, {followSymlinks: false, base: 'dist'})
  .pipe(vfs.symlink(path.join(targetDir, dirName)))
  .on('end', () => {
    console.log(`🚀 Symlinked: ${distDir} -> ${targetDir} 🚀`)
  })
}


module.exports = {
  getRootDir,
  getProdPublicPath,
  symlink
}
