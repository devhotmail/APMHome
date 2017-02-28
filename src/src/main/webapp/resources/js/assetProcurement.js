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
      show: true,
      tooltipAxes: 'y',
      formatString: null,
      tooltipFormatString: '%s'
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

  function skinSeriesColors() {
    // set opacity for prediction chart
    if (/predict-/.test(this.cfg.widgetVar)) {
      console.log(this.cfg.widgetVar);
      this.cfg.seriesColors = VIS_COLORS.map(function(rgba) {
        return rgba.replace(/1\)$/, '.5)');
      });
    }
  }

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
          },
          rendererOptions: {
            drawBaseline: false
          }
        }
      }
    });
    skinSeriesColors.call(this);
  };
  window.detailSkin = function() {
    $.extend(true/*recursive*/, this.cfg, base_chart, {
      height: 250,
    });
    skinSeriesColors.call(this);
  };

  $(function() {
    var TAB_SEL = '.device-brief-list a.device-block';
    var CONTENT_SEL = '.device-block-wrapper';
    $(TAB_SEL).on('click', function() {
      var target_id = $(this).data('toggle');
      var $tab = $(this);
      $(TAB_SEL).not(this).removeClass('active');
      $tab.toggleClass('active');
      $(CONTENT_SEL).each(function() {
        $(this).toggleClass('active', this.id === target_id && $tab.hasClass('active'));
      });
    });
  });
})();