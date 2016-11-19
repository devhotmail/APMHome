function skinBar() {
  $.extend(true/*recursive*/, this.cfg, {
    shadow: false,
    title: '',
    height: 120,
    seriesColors: ['rgba(93, 165, 218, 1)'],
    animate: !$.jqplot.use_excanvas,
    grid: {
      background: 'transparent',
      borderColor: 'transparent',
      shadow: false
    },
    axes: {
      yaxis: {
        padMax: 1.2,
        tickOptions: {
          show: false,
          formatString: '%d'
        },
        rendererOptions: {
          drawBaseline: false,
        }
      },
      xaxis: {
        borderWidth: 0.0,
        borderColor: '3e464c',
        drawMajorGridlines: false,
        drawMajorTickMarks: false,
        tickOptions: {
          textColor: '#fff'
        }
      },
    },
    seriesDefaults: {
      shadow: false,
      lineWidth: 1,
      renderer: $.jqplot.BarRenderer,
      pointLabels: {
        show: true,
        hideZeros: true
      },
      rendererOptions: {
        highlightMouseOver: false,
        barWidth: 20,
        animation: {
          speed: 500
        },
      },
      markerOptions: {
        shadow: false,
        size: 1,
        style: 'circle'
      }
    },
    series: [{
      show: false
    }, {
      show: true
    }, {
      show: false
    }],
    highlighter: {
      show: false
    }
  });

  setTimeout(function() { this.plot.replot({resetAxes: true}); }.bind(this), 0);
}
