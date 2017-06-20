var fs = require('fs')
var path = require('path')
var childProcess = require('child_process')

var reactFolderPath = path.resolve(__dirname, './react')

fs.readdir(reactFolderPath, function (err, files) {
  if (err) console.error('readdir error is:' + err)

  var folders = files.filter(function (file) {
    return fs.statSync(path.join(reactFolderPath, file)).isDirectory()
  })

  let done = []
  folders.forEach(function (folder) {
    childProcess.exec('yarn install && npm run build:local', {
      cwd: path.join(reactFolderPath, folder)
    }, function (error, stdout, stderr) {
      console.log('current folder is: ' + folder)
      if (error) {
        console.error('exec error: ' + error)
        return
      }
      console.log('stdout: ' + stdout)
      console.error('stderr: ' + stderr)
      done.push(folder)
      console.log('Built Apps: ')
      console.log(done)
      if (done.length === folders.length) {
        console.log('build finished')
      }
    })
  })
})
