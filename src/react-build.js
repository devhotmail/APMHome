var fs = require('fs')
var path = require('path')
var childProcess = require('child_process')

var reactFolderPath = path.resolve(__dirname, './react')

fs.readdir(reactFolderPath, function(err, files) {
  var folders = files.filter(function(file) {
    return fs.statSync(path.join(reactFolderPath, file)).isDirectory()
  })

  folders.forEach(function(folder) {
    childProcess.exec('yarn install && npm run build', {
      cwd: path.join(reactFolderPath, folder)
    }, function(error, stdout, stderr) {
      if (error) {
        console.error('exec error: ' + error)
        return
      }
      console.log('stdout: ' + stdout)
      console.log('stderr: ' + stderr)
    })
  })

})
