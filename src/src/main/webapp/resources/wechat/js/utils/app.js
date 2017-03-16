$(function () {
    var app = window.app = {};
    app.initSelectData = initSelectData;
    app.toggleJsdialog = toggleJsdialog;
    
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

});