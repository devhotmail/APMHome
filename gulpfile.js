'use strict';

const autoprefixer = require('gulp-autoprefixer');
const sass = require('gulp-sass');
const gulp = require('gulp');
const cssmin = require('gulp-cssmin');
const importOnce = require('node-sass-import-once');
const src = 'src/main/webapp/resources/sass/*.scss';
const dest = 'src/main/webapp/resources/css/';

// -------------------------------------
//   Task: Compile: Sass
// -------------------------------------

gulp.task('sass:compile', function() {
  return gulp.src([
    src,
  ])
  .pipe(sass({
    style: 'compressed',
    importer: importOnce,
    importOnce: {
      index: true,
      css: true,
      bower: true
    }
  })
  .on('error', sass.logError))
  .pipe(autoprefixer())
  .pipe(cssmin({
    advanced: false,
    aggressiveMerging: false,
    keepBreaks: true,
    keepSpecialComments:0
  }))
  .pipe(gulp.dest(dest));
});

// -------------------------------------
//   Task: Watch: Sass
// -------------------------------------

gulp.task('sass:watch', function() {
  gulp.watch(src, ['sass:compile']);
});

gulp.task('default', ['sass:compile', 'sass:watch']);