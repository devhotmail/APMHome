function scheduleSkin() {


}

function updateTopPanel(array) {
    for (var i = 0; i < 5; i++) {
        $('.event-count.event-type-' + (i + 1)).text(array[i]);
    }
}
