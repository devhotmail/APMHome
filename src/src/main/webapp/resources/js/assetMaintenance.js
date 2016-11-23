(function() {
  window.chartR3 = function() {
    $.extend(true/*recursive*/, this.cfg, {
      seriesDefaults: {
        pointLabels: {
          show: true,
          edgeTolerance: 10,
          hideZeros: true,
        },
        rendererOptions: {
          highlightMouseOver: true,
        }
      },
      legend: {
        show: true
      }
    });
  }

  window.r3 = function() {
  }

  window.r41 = function() {
  }

  window.r42 = function() {
  }

  window.r43 = function() {
  }

  window.r5 = function() {
  }
})();