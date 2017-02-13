'use strict';

const path = require('path');
const autoprefixer = require('gulp-autoprefixer');
const stylemod = require('gulp-style-modules');
const sass = require('gulp-sass');
const gulp = require('gulp');
const cssmin = require('gulp-cssmin');
const importOnce = require('node-sass-import-once');
const src = 'src/main/webapp/resources/sass/*.scss';
const src_comp = 'src/main/webapp/resources/elements/**/*.scss';
const dest = 'src/main/webapp/resources/css/';

// -------------------------------------
//   Task: Compile: Sass
// -------------------------------------

gulp.task('sass:compile:css', function() {
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

gulp.task('sass:compile:module', function() {

  var fileName = function(file) {
    return path.basename(file.path, path.extname(file.path));
  };

  return gulp.src([
    src_comp
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
  .pipe(stylemod({
    // All files will be named 'styles.html'
    filename: function(file) {
      var name = fileName(file) + '-styles';
      return name;
    },
    // Use '-css' suffix instead of '-styles' for module ids
    moduleId: function(file) {
      return fileName(file) + '-styles';
    }
  }))
  .pipe(gulp.dest(function(file) {
    return file.base;
  }));
});

// -------------------------------------
//   Task: Watch: Sass
// -------------------------------------

gulp.task('sass:watch', function() {
  gulp.watch(src, ['sass:compile:css']);
  gulp.watch(src_comp, ['sass:compile:module']);
});

gulp.task('default', ['sass:compile:css', 'sass:compile:module','sass:watch']);