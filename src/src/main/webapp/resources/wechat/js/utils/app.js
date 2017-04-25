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
    app.initFaultType = initFaultType;
    app.renderImgs = renderImgs;
    app.mediaShow = mediaShow;
    /*glnote*/
    function initFaultType(keyId,astypeId,defaultSelected){
        $.get(WEB_ROOT+"web/assetfaulttype/"+astypeId,
            function(ret) {
                if (ret) {
                    pageManager.msgTypes[astypeId] = {};
                    $.each(ret, function(idx, val){
                        if (defaultSelected && val.id == defaultSelected) {
                            $('#'+keyId).append($('<option value="'+val.id+'" selected="selected">'+val.faultName+'</option>'));
                        } else {
                            $('#'+keyId).append($('<option value="'+val.id+'">'+val.faultName+'</option>'));
                        }
                    });
                }
            }
        );
    }


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
    function activeProgressBar(step, feedbackRating) {
        $('.progressbar').children().removeClass('active activeone');
        $.each($('.progressbar').children(), function(idx, val){
            if (idx < step) {
                $(val).addClass('active');
            }
            if (idx === step-1 && feedbackRating === 0){
                $(val).addClass('activeone');
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
    
    function renderImgs() {
        var $gallery = $('.my-gallery');
        $.get(WEB_ROOT+'web/wo_img_list', {woId: pageManager.woId}, function(ret){
            if (ret && ret.length > 0){
                $.each(ret, function(idx, val){
                    var $img = $('<figure style="display: inline; padding-right: 8px;"><a href="" data-size="800x600"><img  height="80px" width="80px" /></a></figure>');
                    $img.find('img').attr('src', WEB_ROOT+'web/image/'+val.photoId);
                    $gallery.append($img);
                });
            }
        });
    }
    
    function mediaShow() {
        $('#voiceBackUp').click(function(){
            pageManager.go('ts_voice_backup');
            $('audio').attr('src', WEB_ROOT+'web/audio/'+pageManager.voice);
        });

        var relocalId = null;
        var replaying = false;
        $.get(WEB_ROOT+'web/media',{serverId : pageManager.voice}, function(ret) {
                wx.downloadVoice({
                    serverId: ret, // 需要下载的音频的服务器端ID，由uploadVoice接口获得
                    isShowProgressTips: 1, // 默认为1，显示进度提示
                    success: function (res) {
                        relocalId = res.localId; // 返回音频的本地ID
                    }
                });
            });
        $('#replay').click(function(){
            if (replaying) {
                replaying = false;
                $(this).html('播放');
                wx.stopVoice({
                    localId: relocalId
                });
            } else {
                replaying = true;
                $(this).html('停止');
                wx.playVoice({
                    localId: relocalId
                });
                var self = this;
                wx.onVoicePlayEnd({
                    success: function (res) {
                        replaying = false;
                        $(self).html('播放');
                    }
                });
            }
        });
    }

});