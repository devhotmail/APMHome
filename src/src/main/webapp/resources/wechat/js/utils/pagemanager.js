window.pageManager = {
    _hash: null,
    _pageStack: [],
    _pageIndex: 0,
    woId: null,
    siteId: null,
    stepCost: [],
    msgTypes: {},
    init: function(pageId){
        var self = this;
        $(window).on('popstate', function() {
            if (location.hash === self._hash)return;
            var hashLocation = location.hash;
            if (hashLocation == '') {
                WeixinJSBridge.call('closeWindow');
            } else {
                self._hash = location.hash;
                var state = history.state || {};
                var page = location.hash.indexOf('#') === 0 ? location.hash : '#';
                if (state._pageIndex <= self._pageIndex ) {
                    self._back();
                } else {
                    self._go(page);
                }
            }
        });
        this.go('#'+pageId);
    },
    go: function(page) {
        location.hash = page;   
    },
    _go: function(page) {
        var self = this;
        this._pageIndex++;
        history.replaceState && history.replaceState({_pageIndex: this._pageIndex}, '', location.href);
        var html = $(page).html()+'<\/script>';
        var $html = $(html);
        $html.addClass('slideIn').on('animationend webkitAnimationEnd', function () {
            $html.removeClass('slideIn');
        });
        $('#container').append($html);
        $(window).scrollTop(0);
        //hidden previous one
        if (self._pageStack.length != 0){
            $(self._pageStack[self._pageStack.length-1].dom[0]).hide();
        }
        self._pageStack.push({dom: $html});
    },
    _back: function() {
        this._pageIndex--;
        var stack = this._pageStack.pop();
        if (!stack) {
            return;
        }
        stack.dom.addClass('slideOut').on('animationend webkitAnimationEnd', function () {
            stack.dom.remove();
        });
        if (this._pageStack.length != 0){
            $(this._pageStack[this._pageStack.length-1].dom[0]).show();
        }
    },
    formdata: function(array){
        var data = {};
        $.each(array, function(index, value){
            data[value.name] = value.value;
        });
        return data;
    }, 
    loadList : function(listId, url, data){
        $loadingToast.fadeIn(100);
        $loadingToast.find('.weui-toast__content').html('数据加载中...');
        $.get(WEB_ROOT+'web/'+url, data, function(ret) {
            if (ret && ret.length !== 0) {
                
                $loadingToast.fadeOut(100);
            } else {
                $loadingToast.fadeOut(100);
                $('#container').empty();
                $('#container').append($('#ts_no_data').html());
            }
        });
    }
}

