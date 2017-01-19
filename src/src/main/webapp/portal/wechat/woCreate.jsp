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
        <title>新增报修</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="http://res.wx.qq.com/open/libs/weui/1.1.0/weui.min.css"/>
        <script src="http://apps.bdimg.com/libs/jquery/2.1.1/jquery.min.js"></script>
        <script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
        <script>var WEB_ROOT = '${ctx}/';</script>
    </head>
    <body>
        <div class="page">
            <div class="page__bd">
                <div class="weui-gallery" id="gallery">
                    <span class="weui-gallery__img" id="galleryImg"></span>
                    <div class="weui-gallery__opr">
                        <a href="javascript:" class="weui-gallery__del">
                            <i class="weui-icon-delete weui-icon_gallery-delete"></i>
                        </a>
                    </div>
                </div>
                
                <form id="assetForm">
                    <div class="weui-cells weui-cells_form">
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label class="weui-label">工单名称*</label></div>
                            <div class="weui-cell__bd">
                                <input class="weui-input" name="name" type="text" placeholder="请输入工单名称" required="required"/>
                            </div>
                            <div class="weui-cell__ft">
                                <i class="weui-icon-warn"></i>
                            </div>
                        </div>
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label class="weui-label">资产名称*</label></div>
                            <div class="weui-cell__bd">
                                <input id="assetName" name="assetName" class="weui-input" type="text" readonly="readonly" required="required"/>
                                <input id="closeReason" type="hidden" name="closeReason"/><!--用来存放图片serverId -->
                                <input id="assetId" type="hidden" name="assetId"/>
                                <input id="hospitalId" type="hidden" name="hospitalId"/>
                            </div>
                            <div class="weui-cell__ft">
                                <i class="weui-icon-warn"></i>
                            </div>
                        </div>
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label for="caseOwnerId" class="weui-label">负责人</label></div>
                            <div class="weui-cell__bd">
                                <input id="caseOwnerId" name="caseOwnerId" type="hidden" />
                                <input id="caseOwnerName" name="caseOwnerName" readonly="readonly" class="weui-input" type="text"/>
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
                            <div class="weui-cell__hd"><label for="requestorId" class="weui-label">报修人*</label></div>
                            <div class="weui-cell__bd">
                                <div class="weui-cell weui-cell_select">
                                    <div class="weui-cell__bd">
                                        <select class="weui-select" name="requestorId" id="requestorId" required="required">
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
                            <div class="weui-cell__hd"><label for="requestTime" class="weui-label">报修时间*</label></div>
                            <div class="weui-cell__bd">
                                <input id="requestTime" name="requestTime" class="weui-input" type="datetime-local" required="required"/>
                            </div>
                            <div class="weui-cell__ft">
                                <i class="weui-icon-warn"></i>
                            </div>
                        </div>
                        
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label for="creatorName" class="weui-label">创建者*</label></div>
                            <div class="weui-cell__bd">
                                <input id="creatorName" name="creatorName" readonly="readonly" class="weui-input" type="text" value="${creatorName}" required="required"/>
                            </div>
                            <div class="weui-cell__ft">
                                <i class="weui-icon-warn"></i>
                            </div>
                        </div>
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label for="currentPersonId" class="weui-label">当前处理人*</label></div>
                            <div class="weui-cell__bd">
                                <div class="weui-cell weui-cell_select">
                                    <div class="weui-cell__bd">
                                        <select class="weui-select" id="currentPersonId" name="currentPersonId" required="required">
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
                                <input id="isInternal" name="isInternal" class="weui-switch" type="checkbox" checked="checked" required="required"/>
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

                        <div class="weui-cell"><label for="requestReason" class="weui-label">故障现象*</label></div>
                        <div class="weui-cell">
                            <div class="weui-cell__bd">
                                <textarea name="requestReason" id="requestReason" class="weui-textarea" placeholder="请输入故障现象" rows="3" required="required"></textarea>
                            </div>
                            <div class="weui-cell__ft">
                                <i class="weui-icon-warn"></i>
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
                            
            <div id="loadingToast" style="display:none;">
                <div class="weui-mask_transparent"></div>
                <div class="weui-toast">
                    <i class="weui-loading weui-icon_toast"></i>
                    <p class="weui-toast__content">数据保存中</p>
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
                        jsApiList: ['scanQRCode','chooseImage','uploadImage'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                    });
                    
                    var tmpl = '<li class="weui-uploader__file" style="background-image:url(#url#)"></li>',
                        $gallery = $("#gallery"), $galleryImg = $("#galleryImg"),
                        $uploaderInput = $("#uploaderInput"),
                        $uploaderFiles = $("#uploaderFiles");
                    $uploaderInput.on("click", function(e){
                        if($uploaderFiles.children().size() == 1){
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
                    
                    //初始化下拉框
                    initData('casePriority', 'casePriority', '${casePriority}');
                    initData('caseType', 'caseType');
                    initData('caseSubType', 'caseSubType');
                    function initData(keyId, msgType, defaultSelected) {
                        $.ajax({
                            type: "GET",
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
                    initPersonData('requestorId', 'getrequestor');
                    initPersonData('currentPersonId', 'getcurrentperson');
                    function initPersonData(keyId, msgType) {
                        $.ajax({
                            type: "GET",
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
                    
                    wx.ready(function(){
                        wx.scanQRCode({
                            needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
                            scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
                            success: function (res) {
                                var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
                                $.ajax({
                                    type: "GET",
                                    url: WEB_ROOT+"web/findasset",
                                    data: {'assetId': result},
                                    success:function(ret) {
                                        if (ret) {
                                            $('#assetId').val(ret.id);
                                            $('#assetName').val(ret.name);
                                            $('#caseOwnerId').val(ret.assetOwnerId);
                                            $('#caseOwnerName').val(ret.assetOwnerName);
                                            $('#hospitalId').val(ret.hospitalId);
                                        }
                                    }
                                });
                            }
                        });
                    });
                    //提交工单
                    $('#submit').click(function(){
                        var array = $('#assetForm').serializeArray();
                        var assetData = {};
                        $.each(array, function(index, value){
                            assetData[value.name] = value.value;
                        });
                        if(assetData['isInternal'] == 'on') {
                            assetData['isInternal'] = true;
                        } else {
                            assetData['isInternal'] = false;
                        }
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
                                } else {
                                    alert('保存失败');
                                }
                            }
                        });
                        if ($loadingToast.css('display') != 'none') return;
                        $loadingToast.fadeIn(100);
                    });
                    
                    function formValidate() {
                        var inputs = $('#assetForm').find('[required=required]');
                        var flag = true;
                        $.each(inputs, function(idx, value){
                            if (value.value == ""){
                                flag = false;
                                if (value.localName == 'select') {
                                    $(value).parent().parent().parent().parent().addClass('weui-cell_warn');
                                } else {
                                    $(value).parent().parent().addClass('weui-cell_warn');
                                }
                            } else {
                                if (value.localName == 'select') {
                                    $(value).parent().parent().parent().parent().removeClass('weui-cell_warn');
                                } else { 
                                    $(value).parent().parent().removeClass('weui-cell_warn');
                                }
                            }
                        });
                        return flag;
                    }
                    
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
                    
                });
            </script>
        </div>
    </body>
</html>
