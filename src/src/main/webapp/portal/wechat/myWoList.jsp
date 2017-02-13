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
        <script src="http://libs.baidu.com/jquery/1.9.1/jquery.min.js"></script>
        <script src="${ctx}/resources/wechat/jweixin-1.0.0.js"></script>
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
            $(function(){
                $('#container').append($('#wolist').html());
            });
        </script>
        <script type="text/html" id="no_data">
            <div class="page">
                <div class="weui-msg">
                    <div class="weui-msg__icon-area"><i class="weui-icon-info weui-icon_msg"></i></div>
                    <div class="weui-msg__text-area">
                        <h2 class="weui-msg__title"></h2>
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
            //fetch data from server
            var $loadingToast = $('#loadingToast');
            $loadingToast.find('.weui-toast__content').html('数据加载中...');
            var $ui_list = $('#ui_list');
            var tmpl = '<div class="weui-cells">'+
                            '<div class="weui-cell">'+
                                '<div class="weui-cell__bd">'+
                                    '<h4 id="li_title"></h4>'+
                                '</div>'+
                                '<div id="li_ft" class="weui-cell__ft"></div>'+
                            '</div>'+
                            '<a class="weui-cell weui-cell_access" href="javascript:;">'+
                                '<div id="li_context" class="weui-cell__bd">'+
                                    '<p></p>'+
                                    '<p></p>'+
                                    '<p></p>'+
                                '</div>'+
                                '<div class="weui-cell__ft">'+
                                '</div>'+
                            '</a>'+
                        '</div>';
            $.ajax({
                url: WEB_ROOT+'web/wolistdata',
                type: 'get',
                contentType: 'application/json',
                success: function(ret) {
                    if (ret) {
                        $.each(ret, function(idx, value){
                            var $tmpl = $(tmpl);
                            $tmpl.find('#li_title').html('工单编号：'+ value['id']);
                            $tmpl.find('#li_ft').html(value['requestTime']);
                            var lcs = $tmpl.find('#li_context p');
                            $(lcs[0]).html('资产编号：'+value['assetId']);
                            $(lcs[1]).html('资产名称：'+value['assetName']);
                            $(lcs[2]).html('工单状态：'+value['currentStepName']);
                            $ui_list.append($tmpl);
                        });
                        $loadingToast.fadeOut(100);
                    } else {
                        $('#container').empty();
                        $('#container').append($('#no_data').html());
                    }
                }
            });
            
            //bind click event
            $('#ui_list').on('click', '.weui-cell_access', function(){
                alert('dddd');
            });
            
            
        </script>
    </body>
</html>
