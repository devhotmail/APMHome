const del = require('del');
const gulp = require('gulp');
//const gulpIgnore = require('gulp-ignore');
const mergeStream = require('merge-stream');
const polymerBuild = require('polymer-build');
const polymerJson = require('./public/polymer.json');
const polymerProject = new polymerBuild.PolymerProject(polymerJson);

/**
 * Waits for the given ReadableStream
 */
function waitFor(stream) {
  return new Promise((resolve, reject) => {
    stream.on('end', resolve);
    stream.on('error', reject);
  });
}

module.exports = function(gulp, buildDirectory) {
  return function build(cb) {

    // List of files to copy to dist
    const dist_globs = []
    .concat(polymerJson.entrypoint)
    .concat(polymerJson.shell)
    .concat(polymerJson.fragments)
    .concat(polymerJson.includeDependencies);

    // Okay, so first thing we do is clear the build directory
    console.log(`Deleting ${buildDirectory} directory...`);
    del([buildDirectory])
    .then(() => {
      let sourcesStream = polymerProject.sources();
      let dependenciesStream = polymerProject.dependencies();
      // Okay, now let's merge them into a single build stream
      let buildStream = mergeStream(sourcesStream, dependenciesStream)
      .once('data', () => {
        console.log('Analyzing build dependencies...');
      });

      // If you want bundling, pass the stream to polymerProject.bundler.
      // This will bundle dependencies into your fragments so you can lazy
      // load them.
      buildStream
      .pipe(polymerProject.bundler)
      //.pipe(gulpIgnore.include(dist_globs))
      .pipe(gulp.dest(buildDirectory)).on('end', cb);
    });
  };
};