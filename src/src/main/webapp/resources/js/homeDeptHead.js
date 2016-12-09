(function() {

  var CURRENCY = "%'.0f";

  var base = {
    shadow: false,
    legend: {
      show: true,
      rendererOptions: {
        numberRows: 1
      },
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
          textColor: 'rgb(36,35,38)',
          formatString: CURRENCY
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
      pointLabels: {
        show: false
      },
      rendererOptions: {
        barWidth: 20,
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

  var base_bar_chart = {
    highlighter: {
      show: true,
      sizeAdjust: 3,
      tooltipAxes: 'y',
      formatString: null
    },
    seriesDefaults: {
      rendererOptions: {
        animation: {
          speed: 1500
        },
      }
    },
    axes: {
      xaxis: {
        tickOptions: {
          labelPosition: 'middle',
          angle: -75
        }
      }
    },
    series: [
      {
        pointLabels: {
          location: 'n',
          ypadding: 10
        }
      }, {
        pointLabels: {
          location: 's',
          ypadding: 10
        }
      }]
  };

  window.deviceScan = function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      seriesColors: ['rgba(93, 165, 218, 1)'],
    });
  }

  window.deviceExposure= function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      series: [{}, {
        showLine: false,
        markerOptions: {
          style: "dash",
          size: 20
        }
      }],
      seriesColors: ['rgba(222, 206, 62, 1)', 'rgba(178, 117, 177, 1)'],
    });
  }

  window.deviceProfit= function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      seriesColors: ['rgba(96, 188, 103, 1)', 'rgba(250, 164, 58, 1)'],
    });
  }
})();
