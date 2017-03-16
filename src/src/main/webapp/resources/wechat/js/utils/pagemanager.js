window.pageManager = {
    _pageStack: [],
    _pageIndex: 0,
    woId: null,
    siteId: null,
    stepCost: [],
    init: function(){
        var self = this;
        $(window).on('hashchange', function () {
            var state = history.state || {};
            var page = location.hash.indexOf('#') === 0 ? location.hash : '#';
            if (state._pageIndex <= self._pageIndex ) {
                self._back();
            } else {
                self._go(page);
            }
        });
        $(window).on('popstate', function() {
            var hashLocation = location.hash;
            if (hashLocation == '')
                WeixinJSBridge.call('closeWindow');
        });
        this.go('#wolist');
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
        var $ui_list = $('#'+listId);
        $ui_list.empty();
        var tmpl = $('#wo_li').html();
        $.ajax({
            url: WEB_ROOT+'web/'+url,
            type: 'get',
            data: data,
            contentType: 'application/json',
            success: function(ret) {
                if (ret && ret.length !=0) {
                    $.each(ret, function(idx, value){
                        var $tmpl = $(tmpl);
                        $tmpl.find('#li_title').html('工单编号：'+ value['id']).attr('woid', value['id']);
                        $tmpl.find('#li_ft').html(value['requestTime']);
                        var lcs = $tmpl.find('#li_context p');
                        $(lcs[0]).html('资产编号：'+value['assetId']);
                        $(lcs[1]).html('资产名称：'+value['assetName']);
                        $(lcs[2]).html('工单状态：'+value['currentStepName']);
                        $ui_list.append($tmpl);
                    });
                    $loadingToast.fadeOut(100);
                } else {
                    $loadingToast.fadeOut(100);
                    $('#container').empty();
                    $('#container').append($('#no_data').html());
                }
            }
        });
    }
}

