(function() {
  var BODY_PART_COLORS = ["rgba(96, 165, 215, 1)", "rgba(246, 162, 70, 1)", "rgba(99, 187, 108, 1)", "rgba(238, 126, 175, 1)", "rgba(176, 143, 56, 1)"];
  var BODY_PART_COLORS_REVERTED = BODY_PART_COLORS.slice(0).reverse();
  var VIS_COLOR_BLANK = 'rgba(153, 153, 163, .5)';

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
      return item[0] ? BODY_PART_COLORS[i] : VIS_COLOR_BLANK;
    });
  }

  function flipLegendRows($el) {
    $el.find('.jqplot-table-legend tbody').each(function(elem, index) {
      var arr = $.makeArray($("tr", this).detach());
      arr.reverse();
      $(this).append(arr);
    });
  }

  var base = {
    shadow: false,
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
        highlightMouseOver: false,
        barWidth: 20,
        barPadding: 1,
        barMargin: 1,
        animation: {
          speed: 1500
        }
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

  window.topBarAllSkin = window.topBarSkin = function() {
    var data = this.cfg.data;
    var series = displayPointLabels.call(this);
    var colors = displayColors(data);
    // special placeholder value
    data = barDisplayValues(data);
    this.cfg.data = data;
    var total = seriesSum(data);

    var _this = this;
    _this.lastVal = 0;
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
      resetAxesOnResize: false,
      seriesDefaults: {
        pointLabels: {
          show: true,
          location: 'w',
          formatString: '%d 个',
          escapeHTML: false,
          formatter: function(format, val) {
            val = val[1];
            return $.jqplot.sprintf(format, val) + '<br />' + (val ? (Math.round(val / total * 100) + '%') : '&nbsp;');
          }
        },
        rendererOptions: {
          highlightMouseOver: false,
          barWidth: 40,
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
          //pad: 0,
          max: total,
          min: 0,
          drawMajorGridlines: false,
          drawMajorTickMarks: false,
          showTickMarks: false,
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
      series: series,
      seriesColors: colors
    });
    _this.jq.on('jqplotPreReplot', function() {
      _this.lastVal = 0;
    });
  }

  window.middleBarSkin = window.tabAreaSkin = function() {
    $.extend(true/*recursive*/, this.cfg, base, {
      shadow: false,
      animate: false,
      grid: {
        background: 'transparent',
        borderColor: 'transparent',
        shadow: false
      },
      highlighter: {
        show: true,
        sizeAdjust: 3,
        tooltipAxes: 'y',
        formatString: null,
        tooltipFormatString: '%d'
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
      seriesColors: BODY_PART_COLORS_REVERTED
    });
  }

  function verticalStackConfig(data) {
    var deviceSum = seriesSum(data);
    var _this = this;
    return {
      resetAxesOnResize: false,
      legend: {
        rendererOptions: {
          seriesToggle: false,
        }
      },
      animate: false,
      seriesDefaults: {
        pointLabels: {
          show: true,
          location: 's',
          formatString: '%d 个',
          escapeHTML: false,
          formatter: function(format, val) {
            var newVal = val - (_this.lastVal || 0);
            _this.lastVal = val;
            val = newVal;
            if (val < 1) {
              val = 0;
            }
            return $.jqplot.sprintf(format, val) + (val? ('<br />' + Math.round(val / deviceSum * 100) + '%') : '');
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
        tickOptions: {
          show: false,
        },
      },
      axes: {
        xaxis: {
          pad: 0
        },
        yaxis: {
          max: deviceSum,
          min: 0,
          tickOptions: {
            show: false,
          },
          rendererOptions: {
            drawBaseline: true
          }
        },
      },
      seriesColors: BODY_PART_COLORS_REVERTED
    };
  }

  window.bottomDrBarSkin =
  window.bottomXrayBarSkin =
  window.bottomCtBarSkin =
  window.bottomLeftBarSkin =
  window.bottomMrBarSkin =
  window.bottomRightBarSkin = function() {
    var _this = this;
    _this.lastVal = 0;
    var data = this.cfg.data;
    // special placeholder value
    this.cfg.data = data.map(function(slot) {
      if (slot[0] === 0) {
        slot[0] = .9;
      }
      return slot;
    });

    $.extend(true/*recursive*/, this.cfg, verticalStackConfig.call(this, _this.cfg.data));
    _this.jq.on('jqplotPreReplot', function() {
      _this.lastVal = 0;
    });
    _this.jq.on('jqplotPostReplot', function() {
      flipLegendRows(_this.jq);
    });
    setTimeout(function() {
      flipLegendRows(_this.jq);
    }, 0);
  };

  $(window).resize(function() {
    if ('deviceStat' in PrimeFaces.widgets) {
      var plot1 = PrimeFaces.widgets.deviceStat.plot;
      plot1.replot({resetAxes: false});
    }
  });

})();