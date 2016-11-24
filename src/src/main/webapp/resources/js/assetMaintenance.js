(function() {
  var base = {
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
    $.extend(true/*recursive*/, this.cfg, base, {
      shadow: false,
      seriesColors: ['rgba(241, 124, 176, 1)', 'rgba(178, 145, 46, 1)', 'rgba(229, 17, 111, 1)'],
      legend: {
        renderer: $.jqplot.EnhancedLegendRenderer,
        show: true,
        rendererOptions: {
          numberRows: 1
        },
        location: 'n',
        placement: 'outsideGrid'
      },
      seriesDefaults: {
        pointLabels: {
          show: true,
          location: 'w',
          formatString: '%d 分钟',
          escapeHTML: false,
        },
        rendererOptions: {
          barWidth: 30,
          highlightMouseOver: false,
        }
      },
      highlighter: {
        show: false
      },
      axes: {
        xaxis: {
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
  };

  window.maintenanceE62 = window.maintenanceE64 = window.maintenanceE66 = window.maintenanceE41 = window.maintenanceE42 = window.maintenanceE43 = function() {
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
          barWidth: 10,
          highlightMouseOver: true,
        }
      },
    });
  };
})();