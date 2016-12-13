(function() {
  var VIS_COLORS = ["rgba(96, 165, 215, 1)", "rgba(246, 162, 70, 1)", "rgba(99, 187, 108, 1)", "rgba(238, 126, 175, 1)", "rgba(176, 143, 56, 1)"];
  var BASELINE_COLOR = 'rgb(229,92,0)';

  var base_chart = {
    title: '',
    animate: !$.jqplot.use_excanvas,
    grid: {
      background: '#ffffff',
      borderColor: 'transparent',
      drawGridlines: true,
      shadow: false
    },
    legend: {
      show: true,
      rendererOptions: {
        numberRows: 1
      },
      location: 'ne'
    },
    resetAxesOnResize: false,
    highlighter: {
      show: true
    },
    axes: {
      xaxis: {
        renderer: $.jqplot.CategoryAxisRenderer,
        tickOptions: {
          labelPosition: 'middle',
          angle: -90
        }
      }
    },
    seriesDefaults: {
      pointLabels: {
        show: false
      },
      rendererOptions: {
        highlightMouseOver: true,
        smooth: false,
        barWidth: 20,
        animation: {
          speed: 500
        },
      }
    },
    seriesColors: VIS_COLORS,
    canvasOverlay: {
      show: true,
      objects: [
        {
          horizontalLine: {
            name: 'baseline',
            y: 100,
            lineWidth: 3,
            yOffset: 0,
            color: BASELINE_COLOR,
            shadow: false
          }
        }
      ]
    }
  };

  window.briefSkin = function() {
    $.extend(true/*recursive*/, this.cfg, base_chart, {
      highlighter: {
        show: false
      },
      height: 150,
      axes: {
        xaxis: {
          tickOptions: {
            show: false
          }
        }
      }
    });
  };
  window.detailSkin = function() {
    var cfg = {
      height: 250,
    };
    // set opacity for series color
    if (this.cfg.widgetVar === 'predictBarChart') {
      cfg.seriesColors = VIS_COLORS.map(function(rgba) {
        return rgba.replace(/1\)$/, '.7)');
      });
    }
    $.extend(true/*recursive*/, this.cfg, base_chart, cfg);
  };
})();