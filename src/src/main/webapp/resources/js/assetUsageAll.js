(function() {

  // TODO: label ist from database
  var CURRENCY = "%'.2f";
  var VIS_COLORS = [
    'rgba(241, 124, 176, 1)',
    'rgba(178, 145, 46, 1)',
    'rgba(229, 17, 111, 1)'
  ];
  var VIS_COLOR_BLANK = 'rgba(153, 153, 163, 1)';

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

  var base_bar_chart = {
    resetAxesOnResize: false,
    height: 300,
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
          angle: -75
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
  };

  function seriesSum(data) {
    return data.reduce(function(sum, serie) {
      var val = serie[0];
      return sum + (typeof val === 'number' ? val : serie.reduce(function(serieSum, slot) {
            return serieSum + slot[0];
          }, 0)
        );
    }, 0);
  }

  function barDisplayValues(data) {
    var sum = data.reduce(function(sum, serie) {
      var item = serie[0];
      var val = (typeof val === 'number' ? val : serie.reduce(function(serieSum, slot) {
        return serieSum + slot[0];
      }, 0) );

      return {
        total: val + sum.total,
        num: val !== 0 ? sum.num + 1 : sum.num
      };
    }, {total: 0, num: 0});

    var avg = sum.total === 0 ? 1 : sum.total / sum.num;
    var min = sum.total === 0 ? avg : sum.total / 20;

    return data.map(function(slot) {
      var item = slot[0];
      if (item[0] === 0) {
        item[0] = avg;
      } else if (item[0] < min) {
        item[0] = min;
      }
      return slot;
    });
  }

  function displayPointLabels() {
    var data = this.cfg.data;
    var labels = this.cfg.series.map(function(serie) {
      return serie.label;
    });

    return data.map(function(slot, i) {
      var item = slot[0];
      return {
        pointLabels: {
          labels: [[labels[i], parseInt(item[0]) || '']]
        }
      };
    });
  }

  function displayColors(data) {
    return data.map(function(slot, i) {
      var item = slot[0];
      return item[0] ? VIS_COLORS[i] : VIS_COLOR_BLANK;
    });
  }

  window.deviceStat = function() {
    var _this = this;
    var data = this.cfg.data;
    //data = [[[332937.2227777777, 1]], [[6658, 1]], [[27663.943888888898, 1]]];
    //data = [[[332937, 1]], [[0, 1]], [[27663, 1]]];
    var colors = displayColors(data);
    // special placeholder value
    data = barDisplayValues(data);
    this.cfg.data = data;
    var total = seriesSum(data);
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      legend: {
        show: false
      },
      highlighter: {
        show: false
      },
      height: 50,
      animate: !$.jqplot.use_excanvas,
      grid: {
        background: 'transparent',
        borderColor: 'transparent',
        drawBorder: false,
        shadow: false
      },
      seriesDefaults: {
        rendererOptions: {
          barWidth: 10,
          highlightMouseOver: false,
        },
        pointLabels: {
          show: true,
          location: 'w',
          edgeTolerance: 0,
          hideZeros: false,
        },
      },
      axesDefault: {
        tickOptions: {
          show: false,
        },
      },
      axes: {
        xaxis: {
          drawMajorGridlines: false,
          drawMajorTickMarks: false,
          showTickMarks: false,
          pad: 0,
          min: 0,
          max: total,
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
      seriesColors: colors
    });
  }

  window.deviceScan = function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      highlighter: {
        show: true,
        tooltipAxes: 'y',
        formatString: null,
        tooltipFormatString: '%d 次'
      },
      seriesColors: ['rgba(93, 165, 218, 1)'],
    });
  }

  window.deviceExpo = function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      highlighter: {
        show: true,
        tooltipAxes: 'y',
        formatString: null,
        tooltipFormatString: '%f 小时'
      },
      series: [
        {}, {
          showLine: false,
          markerOptions: {
            style: "dash",
            size: 20
          }
        }],
      seriesColors: ['rgba(222, 206, 62, 1)', 'rgba(178, 118, 178, 1)'],
    });
  }

  window.deviceUsage = function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      stackSeries: true,
      highlighter: {
        show: true,
        tooltipAxes: 'y',
        formatString: null,
        tooltipFormatString: '%f 小时'
      },
      seriesColors: ['rgba(240, 126, 110, 1)', 'rgba(237, 221, 70, 1)', 'rgba(241, 124, 176, 1)'],
    });
  }

  window.deviceDT = function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      highlighter: {
        show: true,
        tooltipAxes: 'y',
        formatString: null,
        tooltipFormatString: '%f 小时'
      },
      series: [
        {}, {
          showLine: false,
          markerOptions: {
            style: "dash",
            size: 20
          }
        }],
      seriesColors: ['rgba(229, 17, 111, 1)', 'rgba(178, 118, 178, 1)'],
    });
  }

  $(window).resize(function() {
    if('deviceStat' in PrimeFaces.widgets){
      var plot1 = PrimeFaces.widgets.deviceStat.plot;
      plot1.replot({resetAxes: false});
    }
  });

})();