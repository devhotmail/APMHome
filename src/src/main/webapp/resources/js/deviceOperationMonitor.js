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
  function verticalBarDisplayValues(data) {
    var sum = data.reduce(function(sum, serie) {
      var val = serie[0];
      return {
        total: val + sum.total,
        num: val !== 0 ? sum.num + 1 : sum.num
      };
    }, {total: 0, num: 0});

    var avg = sum.total === 0 ? 1 : sum.total / sum.num;
    var min = sum.total === 0 ? avg : sum.total / 10;

    return data.map(function(serie) {
      var val = serie[0];
      if (val === 0) {
        serie[0] = min;
      } else if (val < min) {
        serie[0] = min;
      }
      return serie;
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

  function verticalDisplayPointLabels(data) {
    return data.map(function(serie, i) {
      var val = serie[0];
      return {
        pointLabels: {
          labels: [parseInt(val)]
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
    var total = seriesSum(data);
    var series = displayPointLabels.call(this);
    var colors = displayColors(data);
    data = barDisplayValues(data);
    var max = seriesSum(data);
    this.cfg.data = data;
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
          max: max,
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
    var total = seriesSum(data);
    var series = verticalDisplayPointLabels(data);
    data = verticalBarDisplayValues(data);
    var max = seriesSum(data);

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
            return $.jqplot.sprintf(format, val) + (val ? ('<br />' + Math.round(val / total * 100) + '%') : '');
          }
        },
        rendererOptions: {
          highlightMouseOver: false,
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
          max: max,
          min: 0,
          tickOptions: {
            show: false,
          },
          rendererOptions: {
            drawBaseline: true
          }
        },
      },
      series: series,
      seriesColors: BODY_PART_COLORS_REVERTED
    };
  }

  window.bottomLeftBarSkin =
  window.bottomRightBarSkin =
  window.bottomBarSkin= function() 
  {
    var _this = this;
    var data = this.cfg.data;
    $.extend(true/*recursive*/, this.cfg, verticalStackConfig(data));
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