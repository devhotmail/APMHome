(function() {

  var CURRENCY = "%'.2f";
  var VIS_COLORS = ['rgba(152, 201, 241, 1)', 'rgba(57, 129, 232, 1)'];

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
    title: '',
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
        drawMajorGridlines: false,
        drawMajorTickMarks: true,
        showTickMarks: true,
        tickOptions: {
          textColor: 'rgb(36,35,38)',
          markSize: 20
        }
      },
    },
    seriesDefaults: {
      shadow: false,
      lineWidth: 1,
      pointLabels: {
        show: true,
        edgeTolerance: 10,
        hideZeros: true,
      },
      rendererOptions: {
        highlightMouseOver: true,
        barWidth: 20,
        barPadding: 1,
        barMargin:1,
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

  var base_bar_chart = {
    highlighter: {
      show: true,
      tooltipAxes: 'y',
      formatString: null,
      tooltipFormatString: '%s'
    },
    seriesDefaults: {
      useNegativeColors: false,
      pointLabels: {
        show: false
      },
      rendererOptions: {
        animation: {
          speed: 1500
        },
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

  function generateSeriesColorForTicks(ticks, rgba1, rgba2) {
    return ticks.map(function(timeTick) {
      var time = new Date(Date.parse(timeTick));
      var now = new Date(Date.now());
      if (
          (time.getYear() > now.getYear()) || 
          (time.getYear() == now.getYear() && time.getMonth() >= now.getMonth())
      ) {
        return rgba2;
      } else {
        return rgba1;
      }
    });
  }

  window.barMonthlyRevenue = function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      seriesColors: VIS_COLORS,
    });
  }

  window.barAnnualRevenue= function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      seriesColors: VIS_COLORS,
      series: [
        {
          pointLabels: {
            location: 'n',
          }
        }, {
          pointLabels: {
            location: 's',
          }
        }]
    });
  }

  window.pieAnnualRevenue= function() {
    $.extend(true/*recursive*/, this.cfg, base, {
      legend: {
        show: true,
        marginRight: -30,
        rendererOptions: {
          numberRows: 0
        },
        location: 'e',
        renderer: $.jqplot.EnhancedPieLegendRenderer
      },
      highlighter: {
        show: true,
        sizeAdjust: 3,
        tooltipAxes: 'y',
        formatString: null,
        tooltipFormatString: CURRENCY
      },
      series: [{
        renderer: $.jqplot.DonutRenderer,
        rendererOptions: {
          sliceMargin: 1,
          dataLabels: 'percent',
          highlightMouseOver: true,
          dataLabelCenterOn: true
        },
      }],
      seriesColors: [
        'rgba(96, 188, 103, 1)',
        'rgba(250, 164, 58, 1)',
        'rgba(178, 145, 46, 1)',
        'rgba(93, 165, 218, 1)',
        'rgba(178, 118, 178, 1)',
        'rgba(229,18,111, 1)',
        'rgba(157,114,42, 1)',
        'rgba(199,180,46,1)',
        'rgba(203,32,39, 1)',
        'rgba(0,54,110, 1)'
      ],
    });
  }
})();
