<%--
  Created by IntelliJ IDEA.
  User: 212605082
  Date: 2017/3/20
  Time: 13:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0"/>
    <title>二维码扫描</title>
    <link rel="stylesheet" href="${ctx}/resources/wechat/css/weui.min.css"/>
    <script src="${ctx}/resources/wechat/js/utils/jquery-3.1.1.min.js"></script>
    <script src="${ctx}/resources/wechat/js/utils/jweixin-1.2.0.js"></script>
    <script src="${ctx}/resources/wechat/js/utils/wechatsdk.js"></script>
    <script src="${ctx}/resources/wechat/js/MegaPixImage.js"></script>
    <script src="${ctx}/resources/wechat/js/exif.js"></script>

    <script>
        var WEB_ROOT = '${ctx}/';
    </script>
</head>
<body style="background-color:#E5E4E2;display:none;" id="qrCreateAssetBody">
<div class="page" >

    <div class="page__bd" id="assetInfoBody">

        <!--BEGIN loading toast -->
        <div id="loadingToast" style="display:none;">
            <div class="weui-mask_transparent"></div>
            <div class="weui-toast">
                <i class="weui-loading weui-icon_toast"></i>
                <p class="weui-toast__content">数据提交中</p>
            </div>
        </div>
        <!--END loading toast -->

        <!--BEGIN dialog2-->
        <div class="js_dialog" id="assetDialog" style="display: none;">
            <div class="weui-mask"></div>
            <div class="weui-dialog">
                <div id="dialogInfoDiv" class="weui-dialog__bd"></div>
                <div class="weui-dialog__ft">
                    <a id="dialogInfoA" href="javascript:;" class="weui-dialog__btn weui-dialog__btn_primary">知道了</a>
                </div>
            </div>
        </div>
        <!--END dialog2-->

        <div class="weui-gallery" id="gallery">
            <span class="weui-gallery__img" id="galleryImg"></span>
            <div class="weui-gallery__opr">
                <a href="javascript:void(0);" class="weui-gallery__del">
                    <i class="weui-icon-delete weui-icon_gallery-delete"></i>
                </a>
            </div>
        </div>

        <form id="qrAssetForm" enctype="multipart/form-data" method="post" action="${ctx}/web/submitQrAssetInfo">
            <input type="hidden" id="openId" name="openId" value="${openId}" />
            <input type="hidden" id="qrCode" name="qrCode" value="" />
            <input type="hidden" id="voiceServerId" name="voiceServerId" value="" />
            <input type="hidden" id="imageServerIds" name="imageServerIds" value="" />
            <input type="hidden" id="uploaderFileBase64" name="uploaderFileBase64" value="" />
            <input type="hidden" id="currentShowImageId" name="uploaderFileIds" value="" />

            <div class="weui-cells__title">&nbsp;资产图片(需要有设备名牌照片)<div id="imageNumDiv" class="weui-uploader__info" style="float: right;">0/5</div></div>
            <div class="weui-cells">
                <div class="weui-cell">
                    <div class="weui-cell__bd">
                        <div class="weui-uploader">
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
            </div>

            <div class="weui-cells__title">&nbsp;语音描述</div>
            <div class="weui-cells">
                <div class="weui-cell">
                    <div class="weui-cell__bd">
                        <a id="voiceRecordBtn" href="javascript:void(0);" class="weui-btn weui-btn_plain-primary">开始录音</a>
                        <a id="voicePlayBtn" href="javascript:void(0);" style="display:none;margin-top: 0;" class="weui-btn weui-btn_plain-primary">播放</a>
                    </div>
                    <div class="weui-cells__ft">
                        <div id="voiceDelDiv" style="display: none;"><i class="weui-icon-cancel"></i></div>
                    </div>
                </div>
            </div>

            <div class="weui-cells__title">&nbsp;备注</div>
            <div class="weui-cells">
                <div class="weui-cell">
                    <div class="weui-cell__bd">
                        <textarea id="comment" name="comment" class="weui-textarea" placeholder="设备基本情况，设备所在地点等重要信息" rows="3"></textarea>
                    </div>
                </div>
            </div>

            <label class="weui-agree">
                <span class="weui-agree__text">
                图片必须有，语音或文字至少一项有内容
                </span>
            </label>

            <div class="weui-btn-area">
                <a class="weui-btn weui-btn_primary" href="javascript:void(0);" id="submitCreateAssetBtn">提交</a>
            </div>

        </form>
        <br/><br/><br/><br/>
    </div>

    <div class="page__bd" id="assetMsgBody" style="display: none;">
        <div class="weui-msg">
            <div class="weui-msg__icon-area"><i id="assetMsgIcon" class="weui-icon-success weui-icon_msg"></i></div>
            <span id="assetMsgInfoSpan">设备没有找到</span>
            <div id="qrssetQrMsgDiv" class="weui-msg__opr-area" style="display: none;">
                <p class="weui-btn-area">
                    <a  href="javascript:location.reload();" class="weui-btn weui-btn_primary">继续扫码添加</a>
                </p>
            </div>
            <div id="assetShowMsgDiv" class="weui-msg__opr-area" style="display: none;">
                <p class="weui-btn-area">
                    <a href="javascript:void(0);" class="weui-btn weui-btn_primary">查看设备</a>
                </p>
            </div>
        </div>
        <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
    </div>

</div>
</body>
<script type="text/javascript">
    $(function(){

        wechatSDK.setAppId('${appId}');

        wechatSDK.setSignature('${timestamp}', '${nonceStr}', '${signature}');
        wechatSDK.init();

        wechatSDK.scanQRCode(1, function (qrCode) {
            /*if (qrCode.length > 16) {
             qrCode = qrCode.substr(qrCode.length-16);
             }*/

            $("#qrCode").val(qrCode);

            $.ajax({
                type: "GET",
                url: WEB_ROOT + "web/validateQrCode",
                data: {"openId": $("#openId").val(), "qrCode": qrCode},
                dataType: "json",
                beforeSend: function( xhr ) {
                    xhr.setRequestHeader('X-Requested-With', {toString: function(){ return ''; }});
                },
                success: function(data ,textStatus, jqXHR){

                    init();

                    if("9" != data){
                        if("3" == data){
                            gotoAssetMsgDiv("failed", "该二维码已存在关联设备！");
                            $("#qrssetQrMsgDiv").show();
                            $("#assetShowMsgDiv").hide();
                        }else if("2"== data){
                            gotoAssetMsgDiv("success", "该二维码已存在关联设备！");
                            $("#qrssetQrMsgDiv").hide();
                            $("#assetShowMsgDiv").show();
                        }else{
                            $("#assetInfoBody").show();
                            $("#assetMsgBody").hide();
                            $("#qrssetQrMsgDiv").show();
                            $("#assetShowMsgDiv").hide();
                        }
                    }else{
                        $("#qrssetQrMsgDiv").show();
                        $("#assetShowMsgDiv").hide();
                        gotoAssetMsgDiv("failed", "无效的二维码");
                    }
                },
                error: function(XMLHttpRequest, textStatus, errorThrown){
                    showDialog("请求失败！");
                    //alert("请求失败！"+ errorThrown);
                }
            });
        });

        var localId = null;
        var recording = false;
        var playing = false;
        $("#voiceRecordBtn").click(function(){

            if(recording){
                wechatSDK.stopRecord(function(res){
                    localId = res.localId;
                    recording = false;

                    wechatSDK.uploadVoice(res.localId, function(serverId){

                        $("#voiceServerId").val(serverId);// 返回音频的服务器端ID

                        $("#voicePlayBtn").html("播放");
                        $("#voicePlayBtn").show();
                        $("#voiceDelDiv").show();
                        $("#voiceRecordBtn").hide();

                    });

                }) ;

            }else{
                wechatSDK.startRecord(function(res){
                    localId = res.localId;
                    recording = false;

                    wechatSDK.uploadVoice(res.localId, function(serverId){

                        $("#voiceServerId").val(serverId);// 返回音频的服务器端ID

                        $("#voicePlayBtn").html("播放");
                        $("#voicePlayBtn").show();
                        $("#voiceDelDiv").show();
                        $("#voiceRecordBtn").hide();

                    });
                });
                recording = true;
                $("#voiceRecordBtn").html("结束录音");
                $("#voiceRecordBtn").show();

            }
        });

        $("#voicePlayBtn").click(function(){

            if(playing){
                wechatSDK.stopVoice(localId);
                playing = false;
                $("#voicePlayBtn").html("播放");
                $("#voicePlayBtn").show();
                $("#voiceDelDiv").show();

            }else{
                wechatSDK.playVoice(localId, function(res){
                    playing = true;
                    $("#voicePlayBtn").html("播放");
                    $("#voicePlayBtn").show();
                    $("#voiceDelDiv").show();
                });
                playing = true;
                $("#voicePlayBtn").html("结束");
                $("#voicePlayBtn").show();
                $("#voiceDelDiv").hide();
            }

        });

        $("#voiceDelDiv").click(function(){

            $("#voiceServerId").val("");

            $("#voiceRecordBtn").html("开始录音");
            $("#voiceRecordBtn").show();
            $("#voicePlayBtn").hide();
            $("#voiceDelDiv").hide();

        });

        $("#submitCreateAssetBtn").click(function(){

            processImage();

            if(!validateAssetInfo()){
                return false;
            }

            if ($('#loadingToast').css('display') != 'none') return;
            $('#loadingToast').fadeIn(100);

            $.ajax({
                type: "POST",
                url: WEB_ROOT + "web/submitQrAssetInfo",
                data: $('#qrAssetForm').serialize(),
                dataType: "json",
                beforeSend: function( xhr ) {
                    xhr.setRequestHeader('X-Requested-With', {toString: function(){ return ''; }});
                },
                success: function(data ,textStatus, jqXHR){
                    $('#loadingToast').fadeOut(100);
                    if(data){
                        gotoAssetMsgDiv("success", "创建成功");
                        init();
                    }else{
                        gotoAssetMsgDiv("failed", "创建失败");
                    }
                },
                error: function(XMLHttpRequest, textStatus, errorThrown){
                    showDialog("请求失败！");
                    //alert("请求失败！"+ errorThrown);
                }
            });
            //gotoAssetMsgDiv("failed", "失败");

        });

        function processImage(){
            var num = $("#uploaderFiles").children().length;
            if(num > 0 ){
                $("#imageServerIds").val("");

                var imageServerIds = new Array();
                var upload = {imageServerIds : imageServerIds};
                var list = $("#uploaderFiles");

                $.each(list.children(), function () {
                    upload.imageServerIds.push({
                        imageServerId: this.id
                    });
                });
                $("#imageServerIds").val(JSON.stringify(upload));
            }
        }

        function validateAssetInfo(){

            var qrcode = $("#qrcode").val();
            var voice = $("#voiceServerId").val();
            var image = $("#imageServerIds").val();
            var comment = $("#comment").val();

            if(qrcode == ""){
                showDialog("二维码不能为空！");
                return false;
            }

            if(image == ""){
                showDialog("图片不能为空！");
                return false;
            }

            if(voice == "" && comment == ""){
                showDialog("语音或文字不能都为空，至少一项有内容！");
                return false;
            }

            return true;
        }

        function init(){

            $("#voiceServerId").val("");
            $("#imageServerIds").val("");
            $("#uploaderFileBase64").val("");
            $("#currentShowImageId").val("");
            $("#comment").val("");

            $("#qrCreateAssetBody").show();
        }

        function gotoAssetMsgDiv (status, msgInfo){
            if("success" == status){
                $("#assetMsgIcon").attr("class","weui-icon-success weui-icon_msg");
            }else{
                $("#assetMsgIcon").attr("class","weui-icon-warn weui-icon_msg");
            }
            $("#assetMsgInfoSpan").html(msgInfo);
            $("#assetInfoBody").hide();
            $("#assetMsgBody").show();
        }

        var tmpl = '<li class="weui-uploader__file" style="background-image:url(#url#)" id="#id#"></li>',
            $gallery = $("#gallery"), $galleryImg = $("#galleryImg"),
            $uploaderInput = $("#uploaderInput"),
            $uploaderFiles = $("#uploaderFiles");

        $uploaderInput.on("click", function(e){
            var num = $("#uploaderFiles").children().length;
            if(num >= 5){
                showDialog("最多只能添加5张图片");
            }else{
                chooseImage(5 - num);
            }

            return false;
        });
        $uploaderFiles.on("click", "li", function(){
            $galleryImg.attr("style", this.getAttribute("style"));
            $gallery.fadeIn(100);

            $("#currentShowImageId").val($(this).attr("id"));
        });
        $gallery.on("click", function(){
            $gallery.fadeOut(100);
        });

        $('.weui-gallery__del').on('click', function(){
            var tempImageId = $("#currentShowImageId").val();
            $("#" + tempImageId).remove();

            countImage();
        });

        function showDialog(dialogInfo){
            $("#dialogInfoDiv").html(dialogInfo);
            $("#assetDialog").fadeIn(200);
        }

        $('#assetDialog').on('click', function(){
            $(this).fadeOut(200);
        });

        $('#assetShowMsgDiv').on("click", function(){
            var qrCode = $("#qrCode").val();
            //window.location.href = WEB_ROOT + "web/qrCreateAsset/" + qrCode;
            window.location.href = WEB_ROOT + "wechat/asset/Detail.xhtml?qrCode=" + qrCode;
        });

        function chooseImage(count) {
            if(count >= 1){
                wx.chooseImage({
                    count: count, // 默认9
                    sizeType: ['compressed'], // 可以指定是原图还是压缩图，默认二者都有['original'原图, 'compressed'压缩图]
                    sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
                    success: function (res) {
                        var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片

                        syncUpload(localIds);
                    }
                });
            }
        }

        function syncUpload(localIds){
            var localId = localIds.pop();
            wx.uploadImage({
                localId: localId,
                isShowProgressTips: 1,
                success: function (res) {
                    var serverId = res.serverId; // 返回图片的服务器端ID

                    if(window.__wxjs_is_wkwebview){
                        wx.getLocalImgData({
                            localId: localId,
                            success: function(ress){
                                var localData = ress.localData.replace('jgp', 'jpeg');
                                tmpl = tmpl.replace('#id#', serverId);
                                $uploaderFiles.append($(tmpl.replace('#url#', localData)));

                                countImage();
                            }
                        });

                    }else{
                        tmpl = tmpl.replace('#id#', serverId);
                        $uploaderFiles.append($(tmpl.replace('#url#', localId)));

                        countImage();
                    }

                    //其他对serverId做处理的代码
                    if(localIds.length > 0){
                        syncUpload(localIds);
                    }
                }
            });
        }

        function countImage(){
            var num = $("#uploaderFiles").children().length;

            $("#imageNumDiv").html(num + "/5");

            return num;
        }

    })

</script>
</html>
