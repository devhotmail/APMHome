(function() {
  var base = {
    shadow: false,
    legend: {
      show: true,
      location: 'ne',
      renderer: $.jqplot.EnhancedPieLegendRenderer
    },
    animate: !$.jqplot.use_excanvas,
    grid: {
      background: 'transparent',
      borderColor: 'transparent',
      shadow: false
    },
    axes: {
      yaxis: {
        tickOptions: {
          show: true,
          textColor: 'rgb(36,35,38)'
        },
        rendererOptions: {
          drawBaseline: true,
        }
      },
      xaxis: {
        borderWidth: 0.0,
        borderColor: '3e464c',
        drawMajorGridlines: false,
        drawMajorTickMarks: true,
        tickOptions: {
          textColor: 'rgb(36,35,38)'
        }
      },
    },
    seriesDefaults: {
      shadow: false,
      lineWidth: 1,
      //renderer: $.jqplot.BarRenderer,
      pointLabels: {show: true},
      rendererOptions: {
        barWidth: 20,
        animation: {
          speed: 500
        },
        highlightMouseOver: false
      },
      markerOptions: {
        shadow: false,
        size: 1,
        style: 'circle'
      }
    },
    highlighter: {
      show: false
    }
  };

  window.deviceScan = function() {
    $.extend(true/*recursive*/, this.cfg, base, {
      seriesColors: ['rgba(93, 165, 218, 1)'],
    });
  }

  window.deviceExposure= function() {
    $.extend(true/*recursive*/, this.cfg, base, {
      seriesColors: ['rgba(222, 206, 62, 1)', 'rgba(178, 117, 177, 1)'],
    });
  }

  window.deviceProfit= function() {
    $.extend(true/*recursive*/, this.cfg, base, {
      seriesColors: ['rgba(96, 188, 103, 1)', 'rgba(250, 164, 58, 1)'],
    });
  }
})();
