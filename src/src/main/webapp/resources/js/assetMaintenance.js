(function() {
  var VIS_COLORS = [
    'rgba(96, 165, 215, 1)',
    'rgba(247, 162, 69, 1)',
    'rgba(99, 187, 108, 1)',
    'rgba(238, 126, 175, 1)',
    'rgba(176, 143, 57, 1)',
    'rgba(222, 206, 64, 1)'
  ];

  var VIS_COLOR_BLANK = 'rgba(153, 153, 163, .5)';

  var base = {
    resetAxesOnResize: false,
    shadow: false,
    grid: {
      background: 'transparent',
      borderColor: 'transparent',
      shadow: false
    },
    seriesDefaults: {
      shadow: false,
      rendererOptions: {
        highlightMouseOver: true
      }
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
        drawMajorGridlines: false,
        drawMajorTickMarks: true,
        showTickMarks: true
      },
    },
  };

  var base_pie_chart = {
    axes: {
      xaxis: {
        drawMajorGridlines: false,
        tickOptions: {
          textColor: 'rgb(36,35,38)',
          markSize: 10
        }
      }
    },
    highlighter: {
      show: true,
      sizeAdjust: 3,
    },
    legend: {
      show: true,
      rendererOptions: {
        numberRows: 1
      },
      location: 'ne',
      renderer: $.jqplot.EnhancedPieLegendRenderer
    },
    seriesDefaults: {
      pointLabels: {
        show: true,
        edgeTolerance: 10,
        hideZeros: true,
      },
      rendererOptions: {
        highlightMouseOver: true,
        barWidth: 20,
        animation: {
          speed: 500
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
    var min = sum.total === 0 ? avg : sum.total / 15;

    return data.map(function(slot) {
      var item = slot[0];
      if (item[0] === 0) {
        item[0] = min;
      } else if (item[0] < min) {
        item[0] = min;
      }
      return slot;
    });
  }

  function pieDisplayValues(data) {
    return [data[0].map(function(item) {
      if (item[1] === 0) {
        item[1] = 1;
      }
      return item;
    })];
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

  window.maintenanceE2 = function() {
    $.extend(true/*recursive*/, this.cfg, base, {
      animate: !$.jqplot.use_excanvas,
      seriesDefaults: {
        rendererOptions: {
          barWidth: 20,
          animation: {
            speed: 1500
          }
        }
      },
      highlighter: {
        tooltipAxes: 'y',
        formatString: null,
        tooltipFormatString: '%d 次'
      },
      legend: {
        labels: ['扫描量'],
      },
      seriesColors: ['rgba(93, 165, 218, 1)']
    });
  }

  window.maintenanceE3 = function() {
    var _this = this;
    _this.lastVal = 0;
    var data = this.cfg.data;
    //data = [[[111, 1]], [[93, 1]], [[1, 1]], [[95, 1]], [[115, 1]], [[103, 1]]]
    var series = displayPointLabels.call(this);
    var colors = displayColors(data);
    // special placeholder value
    data = barDisplayValues(data);
    this.cfg.data = data;
    var total = seriesSum(data);
    $.extend(true/*recursive*/, this.cfg, base, {
      shadow: false,
      seriesColors: colors,
      legend: {
        show: false,
      },
      seriesDefaults: {
        pointLabels: {
          show: true,
          location: 'w',
          formatString: '%d 分钟',
          escapeHTML: false,
          formatter: function(format, val) {
            var minute = val[1];
            if (minute) {
              if (minute > 60) {
                // minute > 60 => hour
                return $.jqplot.sprintf('%s: %.1f 小时', val[0], minute / 60);
              } else {
                // minute > 60 => hour
                return $.jqplot.sprintf('%s: %d 分钟', val[0], minute);
              }
            }
            else {
              return $.jqplot.sprintf('%s: 无数据', val[0]);
            }
          }
        },
        rendererOptions: {
          barWidth: 30,
          highlightMouseOver: false,
        },
      },
      series: series,
      highlighter: {
        show: false
      },
      axes: {
        xaxis: {
          max: total,
          pad: 0,
          min: 0,
          drawMajorGridlines: false,
          drawMajorTickMarks: false,
          tickOptions: {
            show: false,
          },
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
      }
    });

    _this.jq.on('jqplotPreReplot', function() {
      _this.lastVal = 0;
    });
  };

  window.maintenanceE62 = window.maintenanceE64 = window.maintenanceE66 =
  window.maintenanceE41 = window.maintenanceE42 = window.maintenanceE43 = function() {
    var data = this.cfg.data;
    // special placeholder value
    data = pieDisplayValues(data);
    this.cfg.data = data;
    $.extend(true/*recursive*/, this.cfg, base, base_pie_chart, {
      highlighter: {
        tooltipAxes: 'x',
        formatString: null,
        tooltipFormatString: '%s'
      },
      legend: {
        show: false,
      },
      series: [
        {
          renderer: $.jqplot.pieRenderer,
          rendererOptions: {
            sliceMargin: 5,
            dataLabels: 'percent',
            highlightMouseOver: true,
          },
        }],
      seriesColors: [
        'rgba(192, 240, 249, 1)',
        'rgba(117, 208, 227, 1)',
        'rgba(45, 191, 221, 1)',
        'rgba(33, 147, 169, 1)'
      ],
    });
  };

  window.maintenanceE51 = window.maintenanceE52 = window.maintenanceE53 = function() {
    $.extend(true/*recursive*/, this.cfg, base, {
      highlighter: {
        tooltipAxes: 'y',
        formatString: null,
        tooltipFormatString: '%s'
      },
      legend: {
        show: false
      },
      seriesColors: ['rgba(93, 165, 218, 1)'],
      seriesDefaults: {
        rendererOptions: {
          barWidth: 20,
          highlightMouseOver: true,
        }
      },
    });
  };
})();