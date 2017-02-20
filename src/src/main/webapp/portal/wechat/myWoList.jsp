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
                opacity: 1; background: white;
            }
            @-webkit-keyframes slideIn {
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
    <body>
        <div id="container" class="container"></div>
        <div id="loadingToast" style="display:none;">
            <div class="weui-mask_transparent"></div>
            <div class="weui-toast">
                <i class="weui-loading weui-icon_toast"></i>
                <p class="weui-toast__content"></p>
            </div>
        </div>
        <script type="text/javascript">
            var $loadingToast = $('#loadingToast');
            $(function(){
//                $('#container').append($('#wolist').html());
                
                window.pageManager = {
                    _pageStack: [],
                    _pageIndex: 0,
                    woId: null,
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
                        this.go('#wolist');
                    },
                    go: function(page) {
                        location.hash = page;   
                    },
                    _go: function(page) {
                        this._pageIndex++;
                        history.replaceState && history.replaceState({_pageIndex: this._pageIndex}, '', location.href);
                        var html = $(page).html()+'<\/script>';
                        var $html = $(html);
                        $html.addClass('slideIn');
                        $('#container').append($html);
                        this._pageStack.push({dom: $html});
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
                    $loadingToast.fadeIn(100);
                    $loadingToast.find('.weui-toast__content').html('数据加载中...');
                    var $ui_list = $('#ui_list');
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

                    //bind click event
                    $('#ui_list').on('click', '.weui-cell_access', function(){
                        $loadingToast.show();
                        $loadingToast.find('.weui-toast__content').html('数据加载中...');
                        pageManager.woId = $(this).parent().find('#li_title').attr('woid');
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
                <div class="page__hd" style="height:50px;">
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
                <div class="page__bd">
                    <div class="weui-gallery" id="gallery">
                        <span class="weui-gallery__img" id="galleryImg"></span>
                        <div class="weui-gallery__opr">
                            <a href="javascript:" class="weui-gallery__del">
                                <i class="weui-icon-delete weui-icon_gallery-delete"></i>
                            </a>
                        </div>
                    </div>

                    <form id="woForm">
                        <div class="weui-cells weui-cells_form">
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">工单名称</label></div>
                                <div class="weui-cell__bd">
                                    <input class="weui-input" id="name" name="name" type="text" readonly="readonly"/>
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">资产名称</label></div>
                                <div class="weui-cell__bd">
                                    <input id="assetName" name="assetName" class="weui-input" type="text" readonly="readonly"/>
                                    <input id="closeReason" type="hidden" name="closeReason"/><!--用来存放图片serverId -->
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">负责人</label></div>
                                <div class="weui-cell__bd">
                                    <input id="caseOwnerName" name="caseOwnerName" readonly="readonly" class="weui-input" type="text"/>
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="requestorName" class="weui-label">报修人</label></div>
                                <div class="weui-cell__bd">
                                    <input id="requestorName" name="requestorName" readonly="readonly" class="weui-input" type="text"/>
                                </div>
                            </div>

                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="requestTime" class="weui-label">报修时间</label></div>
                                <div class="weui-cell__bd">
                                    <input id="requestTime" name="requestTime" class="weui-input" type="datetime-local" readonly="readonly"/>
                                </div>
                            </div>

                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="creatorName" class="weui-label">创建者</label></div>
                                <div class="weui-cell__bd">
                                    <input id="creatorName" name="creatorName" readonly="readonly" class="weui-input" type="text" value="${creatorName}"/>
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="currentPersonName" class="weui-label">当前处理人</label></div>
                                <div class="weui-cell__bd">
                                    <input id="currentPersonName" name="currentPersonName" readonly="readonly" class="weui-input" type="text"/>
                                </div>
                            </div>
                            <div class="weui-cell"><label for="requestReason" class="weui-label">故障现象</label></div>
                            <div class="weui-cell">
                                <div class="weui-cell__bd">
                                    <textarea name="requestReason" id="requestReason" class="weui-textarea" readonly="readonly" rows="3"></textarea>
                                </div>
                            </div>
                            <a class="weui-cell weui-cell_access" href="javascript:;">
                                <div class="weui-cell__bd">
                                    <p>工单步骤</p>
                                </div>
                                <div class="weui-cell__ft">
                                </div>
                            </a>

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

                    <div class="weui-btn-area">
                        <a class="weui-btn weui-btn_primary" href="javascript:" id="submit">提交</a>
                    </div>
                </div>
            </div>

            <script type="text/javascript">
                $(function(){
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
                                $.each($('.progressbar').children(), function(idx, val){
                                    if (idx < step) {
                                        $(val).addClass('active');
                                    }
                                });
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
                            if ('datetime-local' == $idx.attr('type')) {
                                val = val.replace(' ', 'T');
                                $idx.val(val);
                            } else if ('checkbox' == $idx.attr('type')) {
                                if (val) {
                                    $idx.attr('checked', 'checked');
                                }
                            } else {
                                $idx.val(val);
                            }
                        });
                    }

                    //bind click event
                    $('#woForm').on('click', '.weui-cell_access', function(){
                        $loadingToast.show();
                        $loadingToast.find('.weui-toast__content').html('数据加载中...');
                        pageManager.go('#stepDetail');
                    });

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
                                        success: function(ret) {debugger;
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

    </body>
</html>
