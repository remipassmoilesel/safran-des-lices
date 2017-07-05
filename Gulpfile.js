var gulp = require('gulp');
var sass = require('gulp-sass');

var styleRoot = 'src/main/resources/static/assets/';

// convert sass to css
gulp.task('styles', function () {
    gulp.src(styleRoot + 'sass/**/*.scss')
        .pipe(sass().on('error', sass.logError))
        .pipe(gulp.dest(styleRoot + 'css'));
});

// watch sass files
gulp.task('default', ['styles'], function () {
    gulp.watch(styleRoot + 'sass/**/*.scss', ['styles']);
});
