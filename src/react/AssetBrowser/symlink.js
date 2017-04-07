const path = require('path')
const fs = require('fs')
const ensureDir = require('ensure-dir') 
const vfs = require('vinyl-fs')

const pwd = __dirname
const dirName = path.basename(pwd, path.extname(pwd))

const distDir = path.resolve(pwd, './dist')
const targetDir = path.resolve(pwd, `../../src/main/webapp/react/${dirName}/`)

ensureDir(targetDir).then(() => {
  fs.symlink(distDir, targetDir, () => {
    console.log(`🚀 Symlinked: ${distDir} -> ${targetDir} 🚀`)
  })
})
