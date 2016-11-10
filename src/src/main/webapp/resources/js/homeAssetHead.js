function skinBar() {
  $.extend(true/*recursive*/, this.cfg, {
    shadow: false,
    title: '',
    seriesColors: ['rgba(93, 165, 218, 1)'],
    grid: {
      background: 'rgba(124, 126, 128, 1)',
      borderColor: 'transparent',
      shadow: false
    },
    axes: {
      yaxis: {
        tickOptions: {
          show: false
        },
        rendererOptions: {
          drawBaseline: false,
        }
      },
      xaxis: {
        borderWidth: 0.0,
        borderColor: '3e464c',
        drawMajorGridlines: false,
        drawMajorTickMarks: false,
        tickOptions: {
          textColor: '#fff'
        }
      },
    },
    seriesDefaults: {
      shadow: false,
      lineWidth: 1,
      renderer: $.jqplot.BarRenderer,
      rendererOptions: {
        barWidth: 20
      },
      markerOptions: {
        shadow: false,
        size: 1,
        style: 'circle'
      }
    }
  });
}
