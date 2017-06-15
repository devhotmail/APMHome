'use strict';

const path = require('path');
const symlink = require('gulp-symlink');
const sequence = require('gulp-sequence');
const postcss = require('gulp-postcss');
const stylemod = require('gulp-style-modules');
const del = require('del')
const sass = require('gulp-sass');
const gulp = require('gulp');
const cssmin = require('gulp-cssmin');
const importOnce = require('node-sass-import-once');
const iconfont = require('gulp-iconfont');
const iconfontCss = require('gulp-iconfont-css');

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
  .pipe(postcss([
    require('autoprefixer')({
      browsers: 'last 50 versions'
    }),
    require('postcss-viewport-units')()
  ]))
  .pipe(cssmin({
    advanced: false,
    aggressiveMerging: false,
    keepBreaks: true,
    keepSpecialComments:0
  }))
  .pipe(gulp.dest('public/css'));
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
  .pipe(postcss([
    require('autoprefixer')(),
    require('postcss-viewport-units')()
  ]))
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
//   Task: Custom Icon Fonts
// -------------------------------------
const fontName = 'DewIcon';
gulp.task('iconfont', function() {
  return gulp.src([
    'public/assets/*.svg'
  ])
  .pipe(iconfontCss({
    fontName,
    path: 'public/sass/_icons-template.scss',
    targetPath: '../sass/dewIcons.scss',
    fontPath: '../fonts/',
    cssClass: 'dewicon'
  }))
  .pipe(iconfont({
    fontName,
    prependUnicode: true,
    formats: ['ttf', 'eot', 'woff', 'woff2', 'svg'],
    // timestamp: Math.round(Date.now() / 1000)
  }))
  .pipe(gulp.dest('public/fonts/'));
});

// -------------------------------------
//   Task: Watch: Sass
// -------------------------------------

gulp.task('sass:watch', function() {
  gulp.watch(src, ['sass:compile:css']);
  gulp.watch(src_comp, ['sass:compile:module']);
});
gulp.task('css', ['sass:compile:css', 'sass:compile:module']);
gulp.task('clean', function clean() {
  return del(dest);
});
gulp.task('bundle', ['clean'], require('./task.bundle')(gulp, 'dist'));
gulp.task('source', ['clean'], function() {
  return gulp.src('public').pipe(symlink(dest));
});
gulp.task('dist', ['bundle'], function() {
  return gulp.src([
    'dist/public/**/*'
  ], {base: 'dist/public'})
  .pipe(gulp.dest(dest))
  .on('end', function() {
    return del(['dist']);
  });
});

gulp.task('default', sequence('css', 'source', 'sass:watch'));
gulp.task('build', sequence('css', 'dist'));