'use strict';

const path = require('path');
const symlink = require('gulp-symlink');
const autoprefixer = require('gulp-autoprefixer');
const stylemod = require('gulp-style-modules');
const del = require('del')
const sass = require('gulp-sass');
const gulp = require('gulp');
const cssmin = require('gulp-cssmin');
const importOnce = require('node-sass-import-once');
const src = 'public/sass/*.scss';
const src_comp = 'public/elements/**/*.scss';
const dest = 'src/main/webapp/resources';

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
  .pipe(gulp.dest(path.join(dest, 'css')));
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
    includePaths: ['public/sass'],
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

gulp.task('default', ['sass:compile:css', 'sass:compile:module', 'build']);
gulp.task('watch', ['default', 'sass:watch']);
gulp.task('clean', function clean() {
  return del(dest);
});
gulp.task('bundle', ['clean'], require('./task.bundle')(gulp, 'dist'));
gulp.task('dev', ['clean'], function symlink() {
  return gulp.src('public').pipe(symlink(dest));
});
gulp.task('build', ['bundle'], function bundle() {
  return gulp.src([
    'dist/public/**/*'
  ], {base: 'dist/public'})
  .pipe(gulp.dest(dest))
  .on('end', function() {
    return del(['dist']);
  });
});
