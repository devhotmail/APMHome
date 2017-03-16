<%-- 
    Document   : scanWoReport
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
        <title>新增报修</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/weui.min.css"/>
        <script src="${ctx}/resources/wechat/js/utils/jquery-2.0.0.min.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/jweixin-1.0.0.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/wechatsdk.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/pagemanager.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/app.js"></script>
        <script>
            var WEB_ROOT = '${ctx}/';
        </script>
    </head>
    <body style="background-color:#f8f8f8">
        <div id="container" class="container">
            <div class="page">
                <div class="page__bd">
                    
                    <div class="weui-cells__title">资产基本信息</div>
                    <div class="weui-form-preview">
                        <div class="weui-form-preview__bd">
                            <div class="weui-form-preview__item">
                                <label class="weui-form-preview__label">资产名称</label>
                                <span class="weui-form-preview__value" id="assetName"></span>
                                <input id="assetId" type="hidden"/>
                            </div>
                            <div class="weui-form-preview__item">
                                <label class="weui-form-preview__label">供应商</label>
                                <span class="weui-form-preview__value" id="supplier"></span>
                            </div>
                            <div class="weui-form-preview__item">
                                <label class="weui-form-preview__label">类型</label>
                                <span class="weui-form-preview__value" id="assetGroup"></span>
                            </div>
                            <div class="weui-form-preview__item">
                                <label class="weui-form-preview__label">状态</label>
                                <span class="weui-form-preview__value" id="assetStatus"></span>
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
                        <div class="weui-cells__title">故障紧急程度</div>
                        <div class="weui-cells">
                            <div class="weui-cell">
                                <div class="weui-cell__bd">
                                    <div class="weui-cell weui-cell_select">
                                        <div class="weui-cell__bd">
                                            <select class="weui-select" name="casePriority" id="casePriority" >
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!--图片 -->
                        <div class="weui-cells__title">故障图片上传<div class="weui-uploader__info" style="float: right;">0/5</div></div>
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
                        <!--语音-->
                        <div class="weui-cells__title">故障语音描述</div>
                        <div class="weui-cells">
                            <div class="weui-cell">
                                <div class="weui-cell__bd">
                                    <button class="weui-btn voice-btn" type="button" id="replay">播放</button>
                                </div>
                            </div>
                        </div>
                        <!--故障现象-->
                        <div class="weui-cells__title">故障文字描述</div>
                        <div class="weui-cells">
                            <div class="weui-cell">
                                <div class="weui-cell__bd">
                                    <textarea name="requestReason" id="requestReason" class="weui-textarea" placeholder="语音和文字至少一项有内容" rows="3"></textarea>
                                </div>
                            </div>
                        </div>
                    </form>

                    <div class="weui-btn-area" style="margin-bottom: 20px">
                        <a class="weui-btn weui-btn_primary" href="javascript:" id="submit">提交</a>
                    </div>
                </div>
            </div>
        </div>
            
        <script type="text/javascript">
            $(function(){
                wechatSDK.setAppId('${appId}');
                wechatSDK.setSignature('${timestamp}', '${nonceStr}', '${signature}');
                wechatSDK.init();

                wechatSDK.scanQRCode(1, function(qrCode) {
                        if (qrCode.length > 16) {
                            qrCode = qrCode.substr(qrCode.length-16);
                        }
                        $.get(WEB_ROOT+"web/findassetinfo", {'qrCode': qrCode}, function(ret){
                            if (ret) {
                                $('#assetId').val(ret.assetId);
                                $('#assetName').html(ret.assetName);
                                $('#supplier').html(ret.supplier);
                                $('#assetGroup').html(ret.assetGroup);
                                $('#assetStatus').html(ret.assetStatus);
                            }
                        });
                    });
                //init casePriority
                app.initSelectData('casePriority', 'casePriority', '${casePriority}');
                
                var tmpl = '<li class="weui-uploader__file" style="background-image:url(#url#)"></li>',
                    $gallery = $("#gallery"), $galleryImg = $("#galleryImg"),
                    $uploaderInput = $("#uploaderInput"),
                    $uploaderFiles = $("#uploaderFiles");
                $uploaderInput.on("click", function(e){
                    if($uploaderFiles.children().size() === 5){
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

                //提交工单
                $('#submit').click(function(){
                    if(!$('#assetId').val()){
                        
                    }
                    var assetData = {};
                    $.each(array, function(index, value){
                        assetData[value.name] = value.value;
                    });
                    if(assetData['isInternal'] == 'on') {
                        assetData['isInternal'] = true;
                    } else {
                        assetData['isInternal'] = false;
                    }
                    assetData['requestTime'] = assetData['requestTime'].replace('T', ' ');
                    var flag = formValidate();
                    if (!flag) return;
                    var $loadingToast = $('#loadingToast');
                    $.ajax({
                        url: WEB_ROOT+'web/saveworkorder',
                        type: 'post',
                        contentType: 'application/json',
                        data: JSON.stringify(assetData),
                        success: function(ret) {
                            if (ret == 'success') {
                                $loadingToast.fadeOut(100);
                                $('#container').empty();
                                $('#container').append($('#msg_success').html());
                            } else {
                                $('#container').empty();
                                $('#container').append($('#msg_failed').html());
                            }
                        }
                    });
                    if ($loadingToast.css('display') != 'none') return;
                    $loadingToast.fadeIn(100);
                });

                function formValidate() {
                    
                }

                function upload() {
                    wx.chooseImage({
                        count: 5, // 默认9
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

            });
        </script>
        
        <jsp:include page="tipsTemplate.html"/>
    </body>
</html>
