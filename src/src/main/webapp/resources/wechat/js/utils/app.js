$(function () {
    var app = window.app = {};
    app.initSelectData = initSelectData;
    app.toggleJsdialog = toggleJsdialog;
    app.fullListItem = fullListItem;
    app.activeProgressBar = activeProgressBar;
    app.initUserSelect = initUserSelect;
    app.setFormJsonValue = setFormJsonValue;
    app.initWechatTime = initWechatTime;
    app.intCasePriority = intCasePriority;
    
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
                    pageManager.msgTypes[msgType] = {};
                    $.each(ret, function(idx, val){
                        pageManager.msgTypes[msgType][val.msgKey] = val.valueZh;
                        if (defaultSelected && val.msgKey == defaultSelected) {
                            $('#'+keyId).append($('<option value="'+val.msgKey+'" selected="selected">'+val.valueZh+'</option>'));
                        } else {
                            $('#'+keyId).append($('<option value="'+val.msgKey+'">'+val.valueZh+'</option>'));
                        }
                    });
                }
            }
        );
    }
    
    function intCasePriority() {
        $.ajax({
            url: WEB_ROOT+"web/getmsg",
            data: {'msgType': 'casePriority'},
            async: false,
            success: function(ret) {
                pageManager.msgTypes['casePriority'] = {};
                $.each(ret, function(idx, val){
                    pageManager.msgTypes['casePriority'][val.msgKey] = val.valueZh;
                });
            }
        });
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
            $tmpl.find('.ftitle').html(value.ftitle).css('color',value.ftitleColor);
            var parentEl = $tmpl.find('.show-data');
            $.each(value.data, function(i, v){
                parentEl.append('<p>'+v+'</p>');
            });
            if (value.rater === 0) {
                $tmpl.find('.reportview').html('未评分');
            }
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
    
    function initUserSelect(keyId, msgType) {
        $.ajax({
            type: "GET",
            url: WEB_ROOT+"web/" +msgType,
            success:function(ret) {
                if (ret) {
                    $('#'+keyId).append($('<option value="">请选择...</option>'));
                    $.each(ret, function(idx, val){
                        $('#'+keyId).append($('<option value="'+val.id+'">'+val.name+'</option>'));
                    });
                }
            }
        });
    }
    
    function setFormJsonValue(obj) {
        if (!obj) return;
        $.each(obj, function(idx, val){
            var $idx = $('#'+idx);
            if ($idx.length == 0) return;
            if ('datetime-local' == $idx.attr('type')) {
                if (!val) {
//                    var datetime = new Date();
//                    var month = datetime.getMonth()+1;
//                    var date = datetime.getDate();
//                    var hours = datetime.getHours();
//                    var mins = datetime.getMinutes();
//                    var secs = datetime.getSeconds();
//                    val = datetime.getFullYear()+'-'+(month>9?month:'0'+month)+'-'+(date>9?date:'0'+date)
//                            +'T'+(hours>9?hours:'0'+hours)+':'+(mins>9?mins:'0'+mins) + ':'+(secs>9?secs:'0'+secs);
                } else {
                    val = val.replace(' ', 'T');
                }
                $idx.val(val);
            } else if ('checkbox' == $idx.attr('type')) {
                if (val) {
                    $idx.attr('checked', 'checked');
                }
            } else if ('span' == $idx[0].localName) {
                if (val) {
                    $idx.html(val);
                }
            } else {
                $idx.val(val);
            }
        });
    }
    
    function initWechatTime() {
        var datetime = new Date();
        var month = datetime.getMonth()+1;
        var date = datetime.getDate();
        var hours = datetime.getHours();
        var mins = datetime.getMinutes();
        var secs = datetime.getSeconds();
        return datetime.getFullYear()+'-'+(month>9?month:'0'+month)+'-'+(date>9?date:'0'+date)
                +'T'+(hours>9?hours:'0'+hours)+':'+(mins>9?mins:'0'+mins) + ':'+(secs>9?secs:'0'+secs);
    }
    
});