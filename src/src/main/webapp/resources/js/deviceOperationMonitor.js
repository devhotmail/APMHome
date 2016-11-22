(function() {

  function seriesSum(data) {
    return data.reduce(function(sum, serie) {
      var val = serie[0];
      return sum + (typeof val === 'number' ? val : serie.reduce(function(serieSum, slot) {
                return serieSum + slot[0];
              }, 0)
          );
    }, 0);  
  }

  function verticalStackConfig(data) {
    var deviceSum = seriesSum(data);
    var lastVal = 0;
    return {
      legend: {
        rendererOptions: {
          seriesToggle: false,
        }
      },
      animate: true,
      seriesDefaults: {
        pointLabels: {
          show: true,
          location: 's',
          formatString: '%d 个',
          escapeHTML: false,
          formatter: function(format, val) {
            var newVal = val - lastVal;
            lastVal = val;
            val = newVal;
            return $.jqplot.sprintf(format, val) + '<br />' + Math.round(val / deviceSum * 100) + '%';
          }
        },
        rendererOptions: {
          barWidth: 60,
          animation: {
            speed: 500
          }
        }
      },
      grid: {
        background: 'transparent',
        borderColor: 'transparent',
        drawBorder: false,
        shadow: false
      },
      axesDefault: {
        drawMajorGridlines: false,
        drawMajorTickMarks: false,
        pad: 0,
        tickOptions: {
          show: false,
        },
      },
      axes: {
        xaxis: {
          pad: 0,
          max: deviceSum,
          rendererOptions: {}
        },
        yaxis: {
          tickOptions: {
            show: false,
          },
          rendererOptions: {
            drawBaseline: true
          }
        },
      },
      seriesColors: BODY_PART_COLORS
    };
  }

  var base = {
    shadow: false,
    title: '',
    animate: !$.jqplot.use_excanvas,
    grid: {
      background: 'transparent',
      borderColor: 'transparent',
      shadow: false
    },
    axes: {
      xaxis: {
        drawMajorGridlines: false,
        tickOptions: {
          textColor: 'rgb(36,35,38)',
          markSize: 10
        }
      },
      yaxis: {
        tickOptions: {
          show: true,
          textColor: 'rgb(36,35,38)'
        },
        rendererOptions: {
          drawBaseline: true
        }
      }
    },

    seriesDefaults: {
      shadow: false,
      lineWidth: 1,
      rendererOptions: {
        highlightMouseOver: true,
        barWidth: 20,
        barPadding: 1,
        barMargin: 1,
        animation: {
          speed: 1500
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

  var BODY_PART_COLORS = ['rgba(176, 143, 56, 1)', 'rgba(238, 126, 175, 1)', 'rgba(99, 187, 108, 1)', 'rgba(246, 162, 70, 1)', 'rgba(96, 165, 215, 1)'];

  window.skinHeaderBar = function() {
    var deviceSum = seriesSum(this.cfg.data);
    $.extend(true/*recursive*/, this.cfg, {
      animate: false,
      legend: {
        show: true,
        rendererOptions: {
          numberRows: 1,
        },
        location: 'n',
        placement: 'outsideGrid',
      },
      seriesDefaults: {
        pointLabels: {
          show: true,
          location: 'w',
          formatString: '%d 个',
          escapeHTML: false,
          formatter: function(format, val) {
            return $.jqplot.sprintf(format, val) + '<br />' + Math.round(val / deviceSum * 100) + '%';
          }
        },
        rendererOptions: {
          barWidth: 50,
          animation: {
            speed: 1500
          },
        }
      },
      height: 50,
      grid: {
        background: 'transparent',
        borderColor: 'transparent',
        drawBorder: false,
        shadow: false
      },
      axesDefault: {
        tickOptions: {
          show: false,
        },
      },
      axes: {
        xaxis: {
          max: deviceSum,
          drawMajorGridlines: false,
          drawMajorTickMarks: false,
          showTickMarks: false,
          pad: 0,
          rendererOptions: {
            drawBaseline: false
          }
        },
        yaxis: {
          tickOptions: {
            show: false,
          },
          rendererOptions: {
            drawBaseline: false,
          }
        },
      },
      seriesColors: BODY_PART_COLORS
    });
  }

  window.tabAreaSkin = function() {
    $.extend(true/*recursive*/, this.cfg, {
      shadow: false,
      animate: false,
      grid: {
        background: 'transparent',
        borderColor: 'transparent',
        shadow: false
      },
      legend: {
        show: true,
        rendererOptions: {
          numberRows: 1,
        },
        location: 'n',
        placement: 'outsideGrid',
      },
      axes: {
        xaxis: {
          drawMajorGridlines: false,
          drawMajorTickMarks: false
        },
      },
      seriesDefaults: {
        shadow: false,
        pointLabels: {
          show: false,
        },
        rendererOptions: {
          animation: {
            speed: 800
          },
        }
      },
      seriesColors: BODY_PART_COLORS
    });
  }

  window.tabBarSkin = function() {
    $.extend(true/*recursive*/, this.cfg, verticalStackConfig(this.cfg.data));
  }

  window.selectedBarSkin = function() {
    $.extend(true/*recursive*/, this.cfg, verticalStackConfig(this.cfg.data));
  }

  // Responsive charts
  $(window).resize(function() {
    var widgets = PrimeFaces.widgets;
    ['headerBar','tabBar', 'selectedBar'].forEach(function(key) {
      var _chart = widgets[key].plot;
      _chart.replot({resetAxes: true});
    });
  });

})();