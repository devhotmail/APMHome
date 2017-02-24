<%-- 
    Document   : woCreate  
    Created on : 2017-1-17, 14:30:39
    Author     : 212595360
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0"/>
        <title>我的工单</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="${ctx}/resources/wechat/weui.min.css"/>
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>
        <script src="${ctx}/resources/wechat/jweixin-1.0.0.js"></script>
        <style>
            .page{
                position: absolute;
                top: 0; bottom: 0; left: 0; right: 0;
                opacity: 1;
            }
            @keyframes slideIn {
                from {
                    transform: translate3d(100%, 0, 0);
                    opacity: 0;
                }
                to {
                    transform: translate3d(0, 0, 0);
                    opacity: 1;
                }
            }

            @keyframes slideOut {
                from {
                    transform: translate3d(0, 0, 0);
                    opacity: 1;
                }
                to {
                    transform: translate3d(100%, 0, 0);
                    opacity: 0;
                }
            }
            .page.slideIn {
                animation: slideIn .2s forwards;
            }

            .page.slideOut {
                animation: slideOut .2s forwards;
            }

            .wo_container {
                width: 100%;
                margin: 10px auto;
            }
            .progressbar {
                counter-reset: step;
            }
            .progressbar li {
                list-style-type: none;
                width: 16%;
                float: left;
                font-size: 12px;
                position: relative;
                text-align: center;
                text-transform: uppercase;
                color: #7d7d7d;
            }
            .progressbar li:before {
                width: 30px;
                height: 30px;
                content: counter(step);
                counter-increment: step;
                line-height: 30px;
                border: 2px solid #7d7d7d;
                display: block;
                text-align: center;
                margin: 0 auto 10px auto;
                border-radius: 50%;
                background-color: white;
            }
            .progressbar li:after {
                width: 100%;
                height: 2px;
                content: '';
                position: absolute;
                background-color: #7d7d7d;
                top: 15px;
                left: -50%;
                z-index: -1;
            }
            .progressbar li:first-child:after {
                content: none;
            }
            .progressbar li.active {
                color: #1AAD19;
            }
            .progressbar li.active:before {
                border-color: #1AAD19;
            }
            .progressbar li.active + li:after {
                background-color: #1AAD19;
            }
        </style>
        <script>var WEB_ROOT = '${ctx}/';</script>
    </head>
    <body style="background-color:#f8f8f8">
        <div id="container" class="container"></div>
        <div id="loadingToast" style="display:none;">
            <div class="weui-mask_transparent"></div>
            <div class="weui-toast">
                <i class="weui-loading weui-icon_toast"></i>
                <p class="weui-toast__content"></p>
            </div>
        </div>
        <div class="js_dialog" id="jsdialog" style="display: none;">
            <div class="weui-mask"></div>
            <div class="weui-dialog">
                <div class="weui-dialog__bd"></div>
                <div class="weui-dialog__ft">
                    <a href="javascript:;" class="weui-dialog__btn weui-dialog__btn_primary"></a>
                </div>
            </div>
        </div>
        <script type="text/javascript">
            var $loadingToast = $('#loadingToast');
            var $js_dialog = $('#jsdialog');
            $js_dialog.on('click', '.weui-dialog__btn', function(){
                $(this).parents('.js_dialog').fadeOut(200);
                if ($(this).html() === '确定') {
                    history.back();
                    pageManager.loadList();
                }
            });
            
            $(function(){
                var appId = '${appId}';
                var timestamp = '${timestamp}';
                var nonceStr = '${nonceStr}';
                var signature = '${signature}';
                wx.config({
                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: appId, // 必填，公众号的唯一标识
                    timestamp: timestamp, // 必填，生成签名的时间戳
                    nonceStr: nonceStr, // 必填，生成签名的随机串
                    signature: signature,// 必填，签名，见附录1
                    jsApiList: ['chooseImage','uploadImage'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });

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
                    }
                }
                pageManager.init();

            });
        </script>
        <script type="text/html" id="no_data">
            <div class="page">
                <div class="weui-msg">
                    <div class="weui-msg__icon-area"><i class="weui-icon-info weui-icon_msg"></i></div>
                    <div class="weui-msg__text-area">
                        <h2 class="weui-msg__title">暂时没有需要处理的工单</h2>
                    </div>
                    <div class="weui-msg__opr-area">
                        <p class="weui-btn-area">
                            <a href="javascript:WeixinJSBridge.call('closeWindow');" class="weui-btn weui-btn_primary">返回菜单</a>
                        </p>
                    </div>
                </div>
            </div>
        </script>
        <script type="text/html" id="wolist">
            <div class="page">
                <div id="ui_list" class="page__bd page__bd_spacing">
                </div>
            </div>
            <script type="text/javascript">
                $(function(){
                    //fetch data from server
                    pageManager.loadList = function(){
                        $loadingToast.fadeIn(100);
                        $loadingToast.find('.weui-toast__content').html('数据加载中...');
                        var $ui_list = $('#ui_list');
                        $ui_list.empty();
                        var tmpl = $('#wo_li').html();
                        $.ajax({
                            url: WEB_ROOT+'web/wolistdata',
                            type: 'get',
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
                    pageManager.loadList();

                    //bind click event
                    $('#ui_list').on('click', '.weui-cell_access', function(){
                        $loadingToast.show();
                        $loadingToast.find('.weui-toast__content').html('数据加载中...');
                        pageManager.woId = $(this).parent().find('#li_title').attr('woid');
                        pageManager.stepCost = [];
                        pageManager.go('#woDetail');
                    });
                });
        </script>
        <script type="text/html" id="wo_li">
            <div class="weui-cells">
                <div class="weui-cell">
                    <div class="weui-cell__bd">
                        <h4 id="li_title"></h4>
                    </div>
                    <div id="li_ft" class="weui-cell__ft"></div>
                </div>
                <a class="weui-cell weui-cell_access" href="javascript:;">
                    <div id="li_context" class="weui-cell__bd">
                        <p></p>
                        <p></p>
                        <p></p>
                    </div>
                    <div class="weui-cell__ft">
                    </div>
                </a>
            </div>
        </script>
        <script type="text/html" id="woDetail">
            <div class="page">
                <div class="page__hd" style="height:63px;">
                    <div class="wo_container">
                        <ul class="progressbar">
                            <li>报修</li>
                            <li>审核</li>
                            <li>派工</li>
                            <li>领工</li>
                            <li>维修</li>
                            <li>关单</li>
                        </ul>
                    </div>
                </div>
                <div class="page__bd ">
                    <div class="weui-gallery" id="gallery">
                        <span class="weui-gallery__img" id="galleryImg"></span>
                        <div class="weui-gallery__opr">
                            <a href="javascript:" class="weui-gallery__del">
                                <i class="weui-icon-delete weui-icon_gallery-delete"></i>
                            </a>
                        </div>
                    </div>
                    <div id="dialogs">
                        <div class="js_dialog" id="iosDialog2" style="display: none;">
                            <div class="weui-mask"></div>
                            <div class="weui-dialog">
                                <div class="weui-dialog__bd">只能上传一张照片</div>
                                <div class="weui-dialog__ft">
                                    <a href="javascript:;" class="weui-dialog__btn weui-dialog__btn_primary">知道了</a>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <form id="woForm">
                        <div class="weui-form-preview">
                            <div class="weui-form-preview__bd">
                                <div class="weui-form-preview__item">
                                    <label class="weui-form-preview__label">工单名称</label>
                                    <span class="weui-form-preview__value" id="name"/>
                                </div>
                                <div class="weui-form-preview__item">
                                    <label class="weui-form-preview__label">资产名称</label>
                                    <span class="weui-form-preview__value" id="assetName"></span>
                                    <input id="closeReason" type="hidden" name="closeReason"/><!--用来存放图片serverId -->
                                    <input id="id" type="hidden" name="id"/>
                                </div>
                                <div class="weui-form-preview__item">
                                    <label class="weui-form-preview__label">负责人</label>
                                    <span class="weui-form-preview__value" id="caseOwnerName"></span>
                                </div>
                                <div class="weui-form-preview__item">
                                    <label class="weui-form-preview__label">报修人</label>
                                    <span class="weui-form-preview__value" id="requestorName"></span>
                                </div>
                                <div class="weui-form-preview__item">
                                    <label class="weui-form-preview__label">报修时间</label>
                                    <span class="weui-form-preview__value" id="requestTime"></span>
                                </div>
                                <div class="weui-form-preview__item">
                                    <label class="weui-form-preview__label">创建者</label>
                                    <span class="weui-form-preview__value" id="creatorName"></span>
                                </div>
                                <div class="weui-form-preview__item">
                                    <label class="weui-form-preview__label">当前处理人</label>
                                    <span class="weui-form-preview__value" id="currentPersonName"></span>
                                </div>
                                <div class="weui-form-preview__item">
                                    <label class="weui-form-preview__label">故障现象</label>
                                    <span class="weui-form-preview__value" id="requestReason"></span>
                                </div>
                            </div>
                            <a class="weui-cell weui-cell_access" href="javascript:;" data-page="#stepDetail">
                                <div class="weui-cell__bd">
                                    <p style="color: #999;">工单步骤</p>
                                </div>
                                <div class="weui-cell__ft">
                                </div>
                            </a>
                        </div>
                        <div class="weui-cells weui-cells_form">
                            <a class="weui-cell weui-cell_access" href="javascript:;" data-page="#step_cost">
                                <div class="weui-cell__bd">
                                    <p>资源费用</p>
                                </div>
                                <div class="weui-cell__ft" style="font-size:16px">增加</div>
                            </a>
                            <div class="weui-cell" style="padding-top:0; padding-bottom: 0">
                                <div class="weui-cells costList" style="margin-top: 0;">
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="currentPersonId" class="weui-label">下步处理人*</label></div>
                                <div class="weui-cell__bd">
                                    <div class="weui-cell weui-cell_select">
                                        <div class="weui-cell__bd">
                                            <select class="weui-select" name="currentPersonId" id="currentPersonId" required="required">
                                                <option value="">请选择...</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="weui-cell__ft">
                                    <i class="weui-icon-warn"></i>
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="casePriority" class="weui-label">紧急度*</label></div>
                                <div class="weui-cell__bd">
                                    <div class="weui-cell weui-cell_select">
                                        <div class="weui-cell__bd">
                                            <select class="weui-select" name="casePriority" required="required" id="casePriority" >
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="weui-cell__ft">
                                    <i class="weui-icon-warn"></i>
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">故障类别</label></div>
                                <div class="weui-cell__bd">
                                    <div class="weui-cell weui-cell_select">
                                        <div class="weui-cell__bd">
                                            <select class="weui-select" name="caseType" id="caseType">
                                                <option value="">请选择...</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">故障子类别</label></div>
                                <div class="weui-cell__bd">
                                    <div class="weui-cell weui-cell_select">
                                        <div class="weui-cell__bd">
                                            <select class="weui-select" name="caseSubType" id="caseSubType">
                                                <option value="">请选择...</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="weui-cell weui-cell_switch">
                                <div class="weui-cell__bd">内部工单?*</div>
                                <div class="weui-cell__ft">
                                    <input id="isInternal" name="isInternal" class="weui-switch" type="checkbox" required="required"/>
                                </div>
                                <div class="weui-cell__ft">
                                    <i class="weui-icon-warn"></i>
                                </div>
                            </div>

                            <!--图片 -->
                            <div class="weui-cell">
                                <div class="weui-cell__bd">
                                    <div class="weui-uploader">
                                        <div class="weui-uploader__hd">
                                            <p class="weui-uploader__title">图片上传</p>
                                        </div>
                                        <div class="weui-uploader__bd">
                                            <ul class="weui-uploader__files" id="uploaderFiles">
                                            </ul>
                                            <div class="weui-uploader__input-box">
                                                <input id="uploaderInput" class="weui-uploader__input" type="file" accept="image/*" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="weui-cell"><label class="weui-label">备注</label></div>
                            <div class="weui-cell">
                                <div class="weui-cell__bd">
                                    <textarea name="comments" class="weui-textarea" placeholder="请输入备注" rows="3"></textarea>
                                </div>
                            </div>

                        </div>
                    </form>

                    <div class="weui-form-preview__ft">
                        <a href="javascript:;" class="weui-form-preview__btn weui-btn_default" id="transfer">
                            <p class="weui-grid__label">转单</p>
                        </a>
                        <a href="javascript:;" class="weui-form-preview__btn weui-btn_default" id="closewo">
                            <p class="weui-grid__label">关单</p>
                        </a>
                        <a href="javascript:;" class="weui-form-preview__btn weui-btn_primary" id="submit">
                            <p class="weui-grid__label">完成</p>
                        </a>
                    </div>
                </div>
            </div>

            <script type="text/javascript">
                $(function(){
                    var tmpl = '<li class="weui-uploader__file" style="background-image:url(#url#)"></li>',
                        $gallery = $("#gallery"), $galleryImg = $("#galleryImg"),
                        $uploaderInput = $("#uploaderInput"),
                        $uploaderFiles = $("#uploaderFiles");
                    $uploaderInput.on("click", function(e){
                        if($uploaderFiles.children().length === 1){
                            $('#iosDialog2').fadeIn(200);
                            return false;
                        }
                        upload();
                        return false;
                    });
                    $uploaderFiles.on("click", "li", function(){
                        $galleryImg.attr("style", this.getAttribute("style"));
                        $gallery.fadeIn(100);
                    });
                    $gallery.on("click", function(){
                        $gallery.fadeOut(100);
                    });
                    $('#dialogs').on('click', '.weui-dialog__btn', function(){
                        $(this).parents('.js_dialog').fadeOut(200);
                    });
                    $('.weui-gallery__del').on('click', function(){
                        $uploaderFiles.children().remove();
                    });
                    function upload() {
                        wx.chooseImage({
                            count: 1, // 默认9
                            sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
                            sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
                            success: function (res) {
                                var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                                $uploaderFiles.append($(tmpl.replace('#url#', localIds)));
                                wx.uploadImage({
                                    localId: localIds[0], // 需要上传的图片的本地ID，由chooseImage接口获得
                                    isShowProgressTips: 1, // 默认为1，显示进度提示
                                    success: function (res) {
                                        var serverId = res.serverId; // 返回图片的服务器端ID
                                        $('#closeReason').val(serverId);
                                    }
                                });
                            }
                        });
                    }
                    
                    //初始化下拉框
                    initData('casePriority', 'casePriority');
                    initData('caseType', 'caseType');
                    initData('caseSubType', 'caseSubType');
                    function initData(keyId, msgType, defaultSelected) {
                        $.ajax({
                            type: "GET",
                            async: false,
                            url: WEB_ROOT+"web/getmsg",
                            data: {'msgType': msgType},
                            success:function(ret) {
                                if (ret) {
                                    $.each(ret, function(idx, val){
                                        if (defaultSelected && val.msgKey == defaultSelected) {
                                            $('#'+keyId).append($('<option value="'+val.msgKey+'" selected="selected">'+val.valueZh+'</option>'));
                                        } else {
                                            $('#'+keyId).append($('<option value="'+val.msgKey+'">'+val.valueZh+'</option>'));
                                        }
                                    });
                                }
                            }
                        });
                    }

                    //人员下拉框
                    initPersonData('currentPersonId', 'getcurrentperson');
                    function initPersonData(keyId, msgType) {
                        $.ajax({
                            type: "GET",
                            async: false,
                            url: WEB_ROOT+"web/" +msgType,
                            success:function(ret) {
                                if (ret) {
                                    $.each(ret, function(idx, val){
                                        $('#'+keyId).append($('<option value="'+val.id+'">'+val.name+'</option>'));
                                    });
                                }
                            }
                        });
                    }

                    $.ajax({
                        url: WEB_ROOT+'web/wodetail',
                        type: 'get',
                        data: {id: pageManager.woId},
                        contentType: 'application/json',
                        success: function(ret) {
                            if (ret) {
                                var step = ret.currentStepId;
                                pageManager.siteId = ret.siteId;
                                $.each($('.progressbar').children(), function(idx, val){
                                    if (idx < step) {
                                        $(val).addClass('active');
                                    }
                                });
                                if (step >= 6) {
                                    $('#submit').hide();
                                    $('#closewo').removeClass('weui-btn_default').addClass('weui-btn_primary');
                                }
                                //set value
                                setJsonValue(ret);
                                $loadingToast.fadeOut(100);
                            } else {

                            }
                        }
                    });

                    function setJsonValue(obj) {
                        if (!obj) return;
                        $.each(obj, function(idx, val){
                            var $idx = $('#'+idx);
                            if ($idx.length == 0) return;
                            if ('datetime-local' == $idx.attr('type')) {
                                val = val.replace(' ', 'T');
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

                    //bind click event
                    $('#woForm').on('click', '.weui-cell_access', function(){
                        $loadingToast.show();
                        $loadingToast.find('.weui-toast__content').html('数据保存中...');
                        pageManager.go($(this).data('page'));
                    });

                    //提交工单
                    $('#submit').click(function(){finishWo('save');});
                    $('#transfer').click(function(){finishWo('transfer');});
                    $('#closewo').click(function(){finishWo('close');});
                    
                    function finishWo(type) {
                        var array = $('#woForm').serializeArray();
                        var formdata = pageManager.formdata(array);
                        if(formdata['isInternal'] === 'on') {
                            formdata['isInternal'] = true;
                        } else {
                            formdata['isInternal'] = false;
                        }
                        formdata.type = type;
                        formdata.stepDetails = pageManager.stepCost;
//                        formdata['requestTime'] = formdata['requestTime'].replace('T', ' ');
//                        var flag = formValidate();
//                        if (!flag) return;
                        var $loadingToast = $('#loadingToast');
                        $.ajax({
                            url: WEB_ROOT+'web/finishwo',
                            type: 'post',
                            contentType: 'application/json',
                            data: JSON.stringify(formdata),
                            success: function(ret) {
                                $loadingToast.fadeOut(100);
                                if (ret == 'success') {
                                    $js_dialog.find('.weui-dialog__bd').html('数据保存成功');
                                    $js_dialog.find('.weui-dialog__ft .weui-dialog__btn').html('确定');
                                } else {
                                    $js_dialog.find('.weui-dialog__bd').html('数据保存失败');
                                    $js_dialog.find('.weui-dialog__ft .weui-dialog__btn').html('取消');
                                }
                                $js_dialog.fadeIn(200);
                            },
                            error: function(ret){
                                $loadingToast.fadeOut(100);
                                $js_dialog.find('.weui-dialog__bd').html('数据保存失败');
                                $js_dialog.find('.weui-dialog__ft .weui-dialog__btn').html('取消');
                                $js_dialog.fadeIn(200);
                            }
                        });
                        if ($loadingToast.css('display') != 'none') return;
                        $loadingToast.fadeIn(100);
                    }

                });
        </script>

        <script type="text/html" id="stepDetail">
            <div class="page">
                <div id="step_list" class="page__bd page__bd_spacing">
                </div>
            </div>
            <script type="text/javascript">
                $(function () {
                    var tmpl = $('#wostepdetail').html();
                    var $step_list = $('#step_list');
                    $.ajax({
                        url: WEB_ROOT+'web/wostepdetail',
                        type: 'get',
                        data: {id: pageManager.woId},
                        contentType: 'application/json',
                        success: function(ret) {
                            if (ret && ret.length != 0) {
                                $.each(ret, function(idx, val){
                                    var $tmpl = $(tmpl);
                                    $tmpl.find('h4').html(idx+1 +'.'+ val.stepName);
                                    $tmpl.find('p').html('责任人：'+val.ownerName);
                                    var $spans = $tmpl.find('.weui-cell__bd span');
                                    $($spans[0]).html(val.startTime);
                                    $($spans[1]).html(val.endTime);
                                    $($spans[2]).html(val.description);
                                    $step_list.append($tmpl);
                                    //show pic

                                    //find cost list
                                    $.ajax({
                                        url: WEB_ROOT+'web/detailcost',
                                        type: 'get',
                                        data: {id: val.id},
                                        contentType: 'application/json',
                                        success: function(ret) {
                                            if (ret && ret.length != 0) {
                                                var costtmpl = '<div class="weui-cell">'+
                                                    '<div class="weui-cell__bd">'+
                                                    '<div><label>内部工时(小时)：</label><span></span></div>'+
                                                    '<div><label>备件：</label><span></span></div>'+
                                                    '<div><label>数量：</label><span></span></div>'+
                                                    '<div><label>单价(元)：</label><span></span></div>'+
                                                    '<div><label>其他费用(元)：</label><span></span></div>'+
                                                    '</div>'+
                                                    '</div>';
                                                $.each(ret, function(idx, val){
                                                    var $costtmpl = $(costtmpl);
                                                    var $spans = $costtmpl.find('span');
                                                    $($spans[0]).html(val.manHours);
                                                    $($spans[1]).html(val.accessory);
                                                    $($spans[2]).html(val.accessoryQuantity);
                                                    $($spans[3]).html(val.accessoryPrice);
                                                    $($spans[4]).html(val.otherExpense);
                                                    $tmpl.find('.cost_ui').append($costtmpl);
                                                });
                                            }
                                        }
                                    });
                                });
                                $loadingToast.fadeOut(100);
                            } else {

                            }
                        }
                    });
                });
        </script>
        <script type="text/html" id="wostepdetail">
            <div class="weui-cells">
                <div class="weui-cell">
                    <div class="weui-cell__bd">
                        <h4></h4>
                    </div>
                    <div class="weui-cell__ft">
                        <p></p>
                    </div>
                </div>
                <div class="weui-cell">
                    <div class="weui-cell__bd">
                        <div><label>开始时间：</label><span></span></div>
                        <div><label>结束时间：</label><span></span></div>
                        <div><label>处理描述：</label><span style="word-break:break-all"></span></div>
                        <div class="weui-cells cost_ui">
                        </div>
                    </div>
                </div>
                <div class="weui-cell">
                    <img style="width:100%" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAC4AAAAuCAMAAABgZ9sFAAAAVFBMVEXx8fHMzMzr6+vn5+fv7+/t7e3d3d2+vr7W1tbHx8eysrKdnZ3p6enk5OTR0dG7u7u3t7ejo6PY2Njh4eHf39/T09PExMSvr6+goKCqqqqnp6e4uLgcLY/OAAAAnklEQVRIx+3RSRLDIAxE0QYhAbGZPNu5/z0zrXHiqiz5W72FqhqtVuuXAl3iOV7iPV/iSsAqZa9BS7YOmMXnNNX4TWGxRMn3R6SxRNgy0bzXOW8EBO8SAClsPdB3psqlvG+Lw7ONXg/pTld52BjgSSkA3PV2OOemjIDcZQWgVvONw60q7sIpR38EnHPSMDQ4MjDjLPozhAkGrVbr/z0ANjAF4AcbXmYAAAAASUVORK5CYII=">
                </div>
            </div>
        </script>

        <script type="text/html" id="step_cost">
            <div class="page">
                <form id="costForm">
                    <div class="weui-cells weui-cells_form">
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label class="weui-label">工时(小时)</label></div>
                            <div class="weui-cell__bd">
                                <input class="weui-input" id="manHours" name="manHours" type="text" pattern="[0-9]"/>
                            </div>
                        </div>
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label class="weui-label">备件</label></div>
                            <div class="weui-cell__bd">
                                <input id="accessory" name="accessory" class="weui-input" type="text"/>
                            </div>
                        </div>
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label class="weui-label">数量</label></div>
                            <div class="weui-cell__bd">
                                <input id="accessoryQuantity" name="accessoryQuantity" class="weui-input" type="text"/>
                            </div>
                        </div>
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label for="requestorName" class="weui-label">单价(元)</label></div>
                            <div class="weui-cell__bd">
                                <input id="accessoryPrice" name="accessoryPrice" class="weui-input" type="text"/>
                            </div>
                        </div>
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label for="requestorName" class="weui-label">其他费用(元)</label></div>
                            <div class="weui-cell__bd">
                                <input id="otherExpense" name="otherExpense" class="weui-input" type="text"/>
                            </div>
                        </div>
                    </div>
                </form>
                <div class="weui-btn-area">
                    <a class="weui-btn weui-btn_primary" href="javascript:" id="costSubmit">提交</a>
                    <div style="height:10px"></div>
                </div>
            </div>

            <script type="text/javascript">
                $(function(){
                    $loadingToast.fadeOut(100);
                    $('.costList').on('click', '.weui-icon-cancel', function(){
                        var $costList = $(this).parent().parent();
                        pageManager.stepCost.slice($costList.data('id'), 1);
                        $costList.remove();
                    });
                    $('#costSubmit').click(function(){
                        var array = $('#costForm').serializeArray();
                        var costData = pageManager.formdata(array);
                        costData.siteId = pageManager.siteId;
                        pageManager.stepCost.push(costData);
                        insertCostList(costData);
                        history.back();
                    });

                    function insertCostList(value){
                        var $costList = $('.costList');
                        var index = $costList.children().length;
                        var tempui = '<div class="weui-cell">'+
                                '<div class="weui-cell__bd"><p>' +
                                '内部工时(小时)：'+value.manHours
                                +',  备件：'+value.accessory
                                +',  数量：'+value.accessoryQuantity
                                +',  单价(元)：'+value.accessoryPrice
                                +',  其他费用(元)：'+value.otherExpense +
                                '</p></div>'+
                                '<div class="weui-cell__ft"><i class="weui-icon-cancel"></i></div></div>';
                        $costList.append($(tempui).attr('data-id', index));
                    }
                });
        </script>

    </body>
</html>
