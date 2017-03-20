$(function () {
    var app = window.app = {};
    app.initSelectData = initSelectData;
    app.toggleJsdialog = toggleJsdialog;
    app.fullListItem = fullListItem;
    app.activeProgressBar = activeProgressBar;
    
    /////////////////////////
    /**
     * 
     * @param {type} keyId   select id
     * @param {type} msgType   the type stored in db
     * @param {type} defaultSelected    defalut value
     * @returns {undefined}
     */
    function initSelectData(keyId, msgType, defaultSelected) {
        $.get(WEB_ROOT+"web/getmsg",
            {'msgType': msgType},
            function(ret) {
                if (ret) {
                    $.each(ret, function(idx, val){
                        if (defaultSelected && val.msgKey === defaultSelected) {
                            $('#'+keyId).append($('<option value="'+val.msgKey+'" selected="selected">'+val.valueZh+'</option>'));
                        } else {
                            $('#'+keyId).append($('<option value="'+val.msgKey+'">'+val.valueZh+'</option>'));
                        }
                    });
                }
            }
        );
    }
    
    function toggleJsdialog(showOrHide) {
        if (showOrHide) {
            $('#jsdialog');
        }
    }
    /**
     * 
     * @param {type} elId  element's id
     * @param {type} datas [{title:'工单编号: 1111', ftitle: '2017-03-17 12:30:34', data : ['资产编号：222','资产名称：...','']}, {...}]
     * @returns {undefined}
     */
    function fullListItem(elId, datas) {
        var $ui_list = $('#'+elId);
        $ui_list.empty();
        var tmpl = $('#ts_listItem').html();
        $.each(datas, function(idx, value){
            var $tmpl = $(tmpl);
            $tmpl.find('h4').html(value.title);
            $tmpl.find('.ftitle').html(value.ftitle);
            var parentEl = $tmpl.find('.show-data');
            $.each(value.data, function(i, v){
                parentEl.append('<p>'+v+'</p>');
            });
            $ui_list.append($tmpl);
        });
    }
    
    /**
     * 
     * @param {type} step 进行到的步骤
     * @returns {undefined}
     */
    function activeProgressBar(step) {
        $.each($('.progressbar').children(), function(idx, val){
            if (idx < step) {
                $(val).addClass('active');
            }
        });
    }
    
});