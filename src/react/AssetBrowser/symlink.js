const path = require('path')
const vfs = require('vinyl-fs')

const pwd = process.cwd()
const dirName = path.basename(pwd, path.extname(pwd))

vfs.src(['./dist/**'])
.pipe(vfs.dest(`../../src/main/webapp/react/${dirName}/`))
