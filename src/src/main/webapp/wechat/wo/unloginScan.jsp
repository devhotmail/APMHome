<%-- 
    Document   :    扫码报修
    Created on : 2017-3-15, 13:10:33
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
        <title>报修</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/weui.min.css"/>
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/wo/woprogress.css"/>
        <script src="${ctx}/resources/wechat/js/utils/jquery-3.1.1.min.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/jweixin-1.2.0.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/wechatsdk.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/pagemanager.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/app.js"></script>
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/photoswipe.css"/>
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/default-skin.css"/>
        <script src="${ctx}/resources/wechat/js/photoswipe.js"></script>
        <script src="${ctx}/resources/wechat/js/photoswipe-ui-default.min.js"></script>
        <script>
            var WEB_ROOT = '${ctx}/';
        </script>
    </head>
    <body style="background-color:#f8f8f8">
        <div id="container" class="container">
            <div class="page">
                <div class="page__bd">
                    <div class="weui-cells__title">资产二维码</div>
                    <div class="weui-cells">
                        <div class="weui-cell">
                            <div class="weui-cell__bd">
                                <input name="qrCode" id="qrCode" class="weui-textarea" placeholder="请输入二维码"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="weui-btn-area">
                    <a class="weui-btn weui-btn_primary" href="javascript:" id="findAsset">查询</a>
                </div>
            </div>
        </div>
            
        <script type="text/javascript">
            $(function(){
                wechatSDK.setAppId('${appId}');
                wechatSDK.setSignature('${timestamp}', '${nonceStr}', '${signature}');
                wechatSDK.init();
                
                wechatSDK.scanQRCode(1,function(result){
                    if (result.length > 16) {
                        result = result.substr(result.length-16);
                    }
                    findAsset(result);
                });
                
                $('#findAsset').click(function(){
                    if (!$('#qrCode').val()) return;
                    findAsset($('#qrCode').val());
                });
                
                function findAsset(qrCode) {
                    $.ajax({
                        url:WEB_ROOT+"web/unloginAssetInfo", 
                        data: {'qrCode': qrCode}, 
                        beforeSend: function( xhr ) { 
                            xhr.setRequestHeader('X-Requested-With', {toString: function(){ return ''; }});
                        },
                        success: function(ret){
                            if (ret && ret.assetId) {
                                $('#container').empty();
                                pageManager.assetId = ret.assetId;
                                pageManager.workOrder = ret;
                                pageManager.init('ts_scanAsset');
                            } else {
                                $('#container').empty();
                                pageManager.init('ts_asset_notfound');
                            }
                        }
                    });
                }
                
            });
        </script>
        <script type="text/html" id="ts_scanAsset">
            <div class="page">
                <div class="page__bd">
                    
                    <div class="weui-cells__title">资产基本信息</div>
                    <div class="weui-form-preview">
                        <div class="weui-form-preview__bd">
                            <div class="weui-form-preview__item">
                                <label class="weui-form-preview__label">资产名称</label>
                                <span class="weui-form-preview__value" id="assetNameInfo"></span>
                                <input id="assetIdInfo" type="hidden"/>
                            </div>
                            <div class="weui-form-preview__item">
                                <label class="weui-form-preview__label">供应商</label>
                                <span class="weui-form-preview__value" id="supplierInfo"></span>
                            </div>
                            <div class="weui-form-preview__item">
                                <label class="weui-form-preview__label">类型</label>
                                <span class="weui-form-preview__value" id="assetGroupInfo"></span>
                            </div>
                        </div>
                    </div>
                    
                    <div class="weui-gallery" id="gallery">
                        <span class="weui-gallery__img" id="galleryImg"></span>
                        <div class="weui-gallery__opr">
                            <a href="javascript:" class="weui-gallery__del">
                                <i class="weui-icon-delete weui-icon_gallery-delete"></i>
                            </a>
                        </div>
                    </div>

                    <form id="woForm">
                        <div class="weui-cells__title">选择</div>
                        <div class="weui-cells">
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="casePriority" class="weui-label">紧急程度</label></div>
                                <div class="weui-cell__bd">
                                    <div class="weui-cell weui-cell_select">
                                        <div class="weui-cell__bd">
                                            <select class="weui-select" name="casePriority" id="casePriority" >
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="assetStatus" class="weui-label">资产状态</label></div>
                                <div class="weui-cell__bd">
                                    <div class="weui-cell weui-cell_select">
                                        <div class="weui-cell__bd">
                                            <select class="weui-select" name="assetStatus" id="assetStatus" >
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="confirmedDownTime" class="weui-label">停机时间</label></div>
                                <div class="weui-cell__bd">
                                    <input id="confirmedDownTime" name="confirmedDownTime" class="weui-input" type="datetime-local"/>
                                </div>
                            </div>
                        </div>

                        <!--图片 -->
                        <div class="weui-cells__title">故障图片上传<div class="weui-uploader__info" style="float: right;"><span id="countnum">0</span>/5</div></div>
                        <div class="weui-cells">
                            <div class="weui-cell">
                                <div class="weui-cell__bd">
                                    <div class="weui-uploader">
                                        <div class="weui-uploader__bd">
                                            <ul class="weui-uploader__files" id="uploaderFiles">
                                            </ul>
                                            <div class="weui-uploader__input-box">
                                                <input id="uploaderInput" class="weui-uploader__input" type="file" accept="image/*" />
                                                <input type="hidden" id="deleteImg">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!--语音-->
                        <div class="weui-cells__title">故障语音描述</div>
                        <div class="weui-cells">
                            <div class="weui-cell">
                                <div class="weui-cell__bd">
                                    <a class="weui-btn weui-btn_plain-primary" type="button" id="record">点击 开始录音</a>
                                    <a class="weui-btn weui-btn_plain-primary" type="button" id="play" style="margin-top:0">播放</a>
                                </div>
                                <div class="weui-cell__ft">
                                    <i class="weui-icon-cancel" id="cancel"></i>
                                </div>
                            </div>
                        </div>
                        <!--故障现象-->
                        <div class="weui-cells__title">故障文字描述</div>
                        <div class="weui-cells">
                            <div class="weui-cell">
                                <div class="weui-cell__bd">
                                    <textarea name="requestReasonInfo" id="requestReasonInfo" class="weui-textarea" placeholder="语音和文字至少一项有内容" rows="3"></textarea>
                                </div>
                            </div>
                        </div>
                        
                        <div class="weui-cells__title">用户信息</div>
                        <div class="weui-cells">
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="userName" class="weui-label">用户姓名*</label></div>
                                <div class="weui-cell__bd">
                                    <input id="userName" name="userName" class="weui-input" type="text"/>
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label for="telephone" class="weui-label">联系电话*</label></div>
                                <div class="weui-cell__bd">
                                    <input id="telephone" name="telephone" class="weui-input" type="tel"/>
                                </div>
                            </div>
                        </div>
                        
                    </form>
                    
                    <div id="errorMsg" style="color:red;text-align: center;"></div>
                    <div class="weui-btn-area">
                        <a class="weui-btn weui-btn_primary" href="javascript:" id="submit">提交</a>
                    </div>
                    <div style="height:20px;"></div>
                </div>
            </div>
            <script>
                $(function(){
                    //init casePriority
                    app.initSelectData('casePriority', 'casePriority', '${casePriority}');
                    app.initSelectData('assetStatus', 'assetStatus', 3);
                    pageManager.serverIds = [];
                    
                    $('#assetIdInfo').val(pageManager.workOrder.assetId);
                    $('#assetNameInfo').html(pageManager.workOrder.assetName);
                    $('#supplierInfo').html(pageManager.workOrder.supplier);
                    $('#assetGroupInfo').html(pageManager.workOrder.assetGroup);
//                    $('#assetStatus').html(pageManager.workOrder.assetStatus);
                    
                    //图片功能开始
                    var tmpl = '<li class="weui-uploader__file" style="background-image:url(#url#)"></li>',
                        $gallery = $("#gallery"), $galleryImg = $("#galleryImg"),
                        $uploaderInput = $("#uploaderInput"),
                        $uploaderFiles = $("#uploaderFiles");
                    $uploaderInput.on("click", function(e){
                        if($uploaderFiles.children().length >= 5){
                            $('#iosDialog2').fadeIn(200);
                            return false;
                        }
                        wechatSDK.chooseImages(5-$uploaderFiles.children().length, upload);
                        return false;
                    });
                    $uploaderFiles.on("click", "li", function(){
                        $('#deleteImg').val($(this).data('serid'));
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
                        var serid = $('#deleteImg').val();
                        $uploaderFiles.find('[data-serid='+serid+']').remove();
                        pageManager.serverIds.splice(pageManager.serverIds.indexOf(serid),1);
                        $('#countnum').html($uploaderFiles.children().length);
                    });
                    function upload(res) {
                        function uploadImg(res, j) {
                            if (j>res.length-1) return;
                            if($uploaderFiles.children().length < 5){
                                wx.uploadImage({
                                    localId: res[j],
                                    isShowProgressTips: 1,
                                    success: function (rest) {
                                        pageManager.serverIds.push(rest.serverId);
                                        if (window.__wxjs_is_wkwebview){
                                            wx.getLocalImgData({
                                                localId: res[j],
                                                success: function(ress){
                                                    var localData = ress.localData.replace('jgp', 'jpeg');
                                                    $uploaderFiles.append($(tmpl.replace('#url#', localData)).attr('data-serid', rest.serverId));
                                                    $('#countnum').html($uploaderFiles.children().length);
                                                }
                                            });
                                        } else {
                                            $uploaderFiles.append($(tmpl.replace('#url#', res[j])).attr('data-serid', rest.serverId));
                                            $('#countnum').html($uploaderFiles.children().length);
                                        }
                                        j++;
                                        uploadImg(res, j);
                                    }
                                });
                            }
                        }
                        uploadImg(res, 0);
                    }
                    //图片功能结束
                    
                    //语音功能开始
                    var voiceId = null;
                    var recording = false;
                    var playing = false;
                    $('#record').click(function(){
                        if (recording) {
                            recording = false;
                            $(this).html('点击 开始录音');
                            $('#play').show();
                            $('#cancel').show();
                            $('#record').hide();
                            var self = this;
                            wx.stopRecord({
                                success: function(res) {
                                    voiceId = res.localId;
                                    recording = false;
                                    $(self).html('点击 开始录音');
                                    $('#play').show();
                                    $('#cancel').show();
                                    $('#record').hide();
                                    uploadVoice();
                                }
                            });
                            wx.onVoiceRecordEnd({
                                complete: function (res) {
                                    voiceId = res.localId; 
                                    recording = false;
                                    $(self).html('点击 开始录音');
                                    $('#play').show();
                                    $('#cancel').show();
                                    $('#record').hide();
                                    uploadVoice();
                                }
                            });
                        } else {
                            recording = true;
                            $(this).html('点击 结束录音');
                            wx.startRecord();
                        } 
                    });
                    $('#play').click(function(){
                        if (playing) {
                            playing = false;
                            $(this).html('播放');
                            wx.stopVoice({
                                localId: voiceId
                            });
                        } else {
                            playing = true;
                            $(this).html('停止');
                            wx.playVoice({
                                localId: voiceId
                            });
                            var self = this;
                            wx.onVoicePlayEnd({
                                success: function (res) {
                                    voiceId = res.localId; // 返回音频的本地ID
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
                        voiceId = null;
                    });
                    $('#play').hide();
                    $('#cancel').hide();
                    function uploadVoice(){
                        wechatSDK.uploadVoice(voiceId, function(voiceSerId){
                            pageManager.voiceSerId = voiceSerId;
                        });
                    }
                    //语音功能结束

                    //提交工单
                    $('#submit').click(function(){debugger;
                        $('#errorMsg').html('');
                        var rReason = $('#requestReasonInfo').val();
                        if (!voiceId && !rReason){
                            $('#errorMsg').html('语音和文字至少一项有内容');
                            return;
                        }
                        if (!$('#userName').val() || !$('#telephone').val()) {
                            $('#errorMsg').html('用户姓名和手机号码必填');
                            return;
                        }
                        
                        var $loadingToast = $('#loadingToast');
                        if ($loadingToast.css('display') !== 'none') return;
                        $loadingToast.fadeIn(100);
                        
                        var data = {
                            assetId: pageManager.assetId,
                            voiceId: pageManager.voiceSerId,
                            imgIds: pageManager.serverIds.toString(),
                            priority: $('#casePriority').val(),
                            reason: rReason,
                            assetStatus: $('#assetStatus').val(),
                            confirmedDownTime : $('#confirmedDownTime').val()?$('#confirmedDownTime').val().replace('T', ' '):'',
                            userName: $('#userName').val(),
                            telephone: $('#telephone').val(),
                            wechatId: '${openId}',
                            nickName: '${nickName}'
                        };
                        $.ajax({
                            url: WEB_ROOT+'web/workorderCreate',
                            type: 'post',
                            contentType: 'application/json',
                            data: JSON.stringify(data),
                            beforeSend: function( xhr ) { 
                                xhr.setRequestHeader('X-Requested-With', {toString: function(){ return ''; }});
                            },
                            success: function(ret) {
                                $loadingToast.fadeOut(100);
                                if (ret === 'success') {
                                    $('#container').empty();
                                    $('#container').append($('#ts_msg_success').html());
                                } else {
                                    $('#container').empty();
                                    $('#container').append($('#ts_msg_failed').html());
                                }
                            }
                        });
                    });

                });
        </script>
        
        <jsp:include page="imgshow.html"/>
        <jsp:include page="tipsTemplate.html"/>
    </body>
</html>
