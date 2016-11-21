(function() {

  // TODO: label ist from database
  var DEVICE_LIST = ['MRI', 'CT', 'DR', '心血管照影', '乳腺仪', '胃肠X逛机', '彩超', 'GE骨密度测定仪', '超声刀', '电子胃肠镜', '胶囊内窥镜', '电子鼻咽喉镜'];
  var DEPT_LIST = ['心导管室', '超声诊断科', '心超室', '放射科', '肿瘤中心', '呼吸内科', '消化内科', '血液科', '心胸外科', '神经内科'];
  var SERIES_LABEL = ['收入', '利润'];
  var CURRENCY = "%'.2f";

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
    legend: {
      labels: SERIES_LABEL,
    },
    seriesDefaults: {
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

  window.barMonthlyRevenue = function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      seriesColors: ['rgba(96, 189, 103, 1)', 'rgba(250, 164, 58, 1)'],
    });
  }

  function generateSeriesColorForTicks(ticks, rgba1, rgba2) {
    return ticks.map(function(timeTick) {
      if (Date.parse(timeTick) < Date.now()) {
        return rgba1;
      } else {
        return rgba2;
      }
    });
  }

  window.barMonthlyForecast = function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      // Provide a custom seriesColors array to override the default colors.
      seriesDefaults: {
        pointLabels: {
          show: false,
        },
        rendererOptions: {
          varyBarColor: true,
          barWidth: 15
        }
      },
      seriesColors: ['rgba(96, 189, 103, 1)', 'rgba(250, 164, 58, 1)'],
      series: [{
        // income
        seriesColors: generateSeriesColorForTicks(this.cfg.ticks, 'rgba(96, 189, 103, 1)', 'rgba(96, 189, 103, .4)'),
      }, {
        // revenue
        seriesColors: generateSeriesColorForTicks(this.cfg.ticks, 'rgba(250, 164, 58, 1)', 'rgba(250, 164, 58, .4)')
      }],
      axes: {
        xaxis: {
          tickOptions: {
            labelPosition: 'middle',
            angle: -90
          }
        }
      }
    });
  }

  window.barAnnualRevenue= function() {
    $.extend(true/*recursive*/, this.cfg, base, base_bar_chart, {
      seriesColors: ['rgba(96, 189, 103, 1)', 'rgba(250, 164, 58, 1)'],
      axes: {
        xaxis: {
          ticks: DEVICE_LIST,
          tickOptions: {
            labelPosition: 'middle',
            angle: -90
          },
        }
      },
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
        labels: DEPT_LIST,
        location: 'e',
        renderer: $.jqplot.EnhancedPieLegendRenderer
      },
      series: [{
        renderer: $.jqplot.pieRenderer,
        rendererOptions: {
          sliceMargin: 5,
          dataLabels: 'value',
          highlightMouseOver: true,
          dataLabelPositionFactor: 1.2,
          dataLabelCenterOn: true,
          dataLabelFormatString: CURRENCY
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
