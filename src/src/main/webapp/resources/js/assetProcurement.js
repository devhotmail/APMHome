(function() {
  var BODY_PART_COLORS = ["rgba(96, 165, 215, 1)", "rgba(246, 162, 70, 1)", "rgba(99, 187, 108, 1)", "rgba(238, 126, 175, 1)", "rgba(176, 143, 56, 1)"];
  var BODY_PART_COLORS_REVERTED = BODY_PART_COLORS.slice(0).reverse();
  var BASELINE_COLOR = 'rgba(229,92,0,.8)';
  var base_chart = {
    resetAxesOnResize: false,
    legend: {
      show: true,
      rendererOptions: {
        numberRows: 1
      },
      location: 'ne',
      renderer: $.jqplot.EnhancedPieLegendRenderer
    },
    highlighter: {
      show: true
    },
    axes: {
      xaxis: {
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
        barWidth: 10,
        animation: {
          speed: 500
        },
      }
    },
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
    $.extend(true/*recursive*/, this.cfg, base_chart);
  };
  window.detailSkin = function() {
    $.extend(true/*recursive*/, this.cfg, base_chart, {});
  };

})();