(function() {

  // TODO: label ist from database
  var CURRENCY = "%'.2f";

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

  window.deviceStat = function() {
    var _this = this;
    var data = this.cfg.data;
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
          show: false
        }
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
      seriesColors: ['rgba(241, 124, 176, 1)', 'rgba(178, 145, 46, 1)', 'rgba(229, 17, 111, 1)']
    });
  }

  window.deviceScan = function() x
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      legend: {
        labels: ['扫描量'],
      },
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
      legend: {
        labels: ['扫描量'],
      },
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
      legend: {
        labels: ['使用','等待'],
      },
      seriesColors: ['rgba(178, 145, 46, 1)', 'rgba(241, 124, 176, 1)'],
    });
  }

  window.deviceDT = function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      legend: {
        labels: ['停机率','基准停机率'],
      },
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

})();