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
        <title>VoiceRecord</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="${ctx}/resources/wechat/weui.min.css"/>
        <script src="http://libs.baidu.com/jquery/1.9.1/jquery.min.js"></script>
        <script src="${ctx}/resources/wechat/jweixin-1.0.0.js"></script>
        <style>
            .voice-btn {
                color: black;
                line-height: 30px;
                background-color: #cccccc;
                margin-top: 0!important;
            }
        </style>
        <script>var WEB_ROOT = '${ctx}/';</script>
    </head>
    <body>
        <div class="page">
            <div class="page__bd">
                <form id="assetForm">
                    <div class="weui-cells weui-cells_form">
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label class="weui-label">Voice</label></div>
                            <div class="weui-cell__bd">
                                <button class="weui-btn voice-btn" type="button" id="record">点击 说话</button>
                                <button class="weui-btn voice-btn" type="button" id="play">播放</button>
                            </div>
                            <div class="weui-cell__ft">
                                <i class="weui-icon-cancel" id="cancel"></i>
                            </div>
                        </div>
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label class="weui-label">Play Voice</label></div>
                            <div class="weui-cell__bd">
                                <button class="weui-btn voice-btn" type="button" id="replay">播放</button>
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
                        jsApiList: ['startRecord','stopRecord','onVoiceRecordEnd', 'playVoice',
                            'stopVoice','onVoicePlayEnd','uploadVoice','downloadVoice'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                    });
                    
                    var localId = null;
                    var recording = false;
                    var playing = false;
                    var serverId = null;
                    $('#record').click(function(){
                        if (recording) {
                            recording = false;
                            $(this).html('点击 说话');
                            $('#play').show();
                            $('#cancel').show();
                            $('#record').hide();
                            var self = this;
                            wx.stopRecord({
                                success: function(res) {
                                    localId = res.localId;
                                    recording = false;
                                    $(self).html('点击 说话');
                                    $('#play').show();
                                    $('#cancel').show();
                                    $('#record').hide();
                                }
                            });
                            wx.onVoiceRecordEnd({
                                complete: function (res) {
                                    localId = res.localId; 
                                    recording = false;
                                    $(self).html('点击 说话');
                                    $('#play').show();
                                    $('#cancel').show();
                                    $('#record').hide();
                                }
                            });
                        } else {
                            recording = true;
                            $(this).html('点击 结束');
                            wx.startRecord();
                        } 
                    });
                    $('#play').click(function(){
                        if (playing) {
                            playing = false;
                            $(this).html('播放');
                            wx.stopVoice({
                                localId: localId
                            });
                        } else {
                            playing = true;
                            $(this).html('停止');
                            wx.playVoice({
                                localId: localId
                            });
                            var self = this;
                            wx.onVoicePlayEnd({
                                success: function (res) {
                                    localId = res.localId; // 返回音频的本地ID
                                    playing = false;
                                    $(self).html('播放');
                                }
                            });
                        }
                    });
                    $('#cancel').click(function(){
                        $('#play').hide();
                        $('#record').show();
                        $('#cancel').hide();
                        localId = null;
                    });
                    $('#play').hide();
                    $('#cancel').hide();
                    
                    //提交工单
                    $('#submit').click(function(){
                        upload();
                    });
                    
                    
                    function upload() {
                        wx.uploadVoice({
                            localId: localId, // 需要上传的音频的本地ID，由stopRecord接口获得
                            isShowProgressTips: 1, // 默认为1，显示进度提示
                            success: function (res) {
                                serverId = res.serverId; // 返回音频的服务器端ID
                                if (serverId) {
                                    $.ajax({
                                        url: WEB_ROOT+'web/savevoice',
                                        type: 'post',
                                        contentType: 'application/json',
                                        data: serverId,
                                        success: function(ret) {

                                        }
                                    });
                                }
                            }
                        });
                    }
                    
                    var relocalId = null;
                    var replaying = false;
                    $.ajax({
                        url: WEB_ROOT+'web/media',
                        type: 'get',
                        contentType: 'application/json',
                        success: function(ret) {
                            wx.downloadVoice({
                                serverId: ret, // 需要下载的音频的服务器端ID，由uploadVoice接口获得
                                isShowProgressTips: 1, // 默认为1，显示进度提示
                                success: function (res) {
                                    relocalId = res.localId; // 返回音频的本地ID
                                }
                            });
                        }
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
                                    playing = false;
                                    $(self).html('播放');
                                }
                            });
                        }
                    });
                    
                });
        </script>
    </body>
</html>
