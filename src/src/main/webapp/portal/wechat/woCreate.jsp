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
<script type="text/javascript">
    $(function(){
        debugger;
        var appId = '${appId}';
        var timestamp = '${timestamp}';
        var nonceStr = '${nonceStr}';
        var signature = '${signature}';
      /*  alert(appId);
        alert(timestamp);
        alert(nonceStr);
        alert(signature);*/
        wx.config({
            debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            appId: appId, // 必填，公众号的唯一标识
            timestamp: timestamp, // 必填，生成签名的时间戳
            nonceStr: nonceStr, // 必填，生成签名的随机串
            signature: signature,// 必填，签名，见附录1
            jsApiList: ['scanQRCode','chooseImage','uploadImage'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
        });

            var tmpl = '<li class="weui-uploader__file" style="background-image:url(#url#)"></li>',
                $gallery = $("#gallery"), $galleryImg = $("#galleryImg"),
                $uploaderInput = $("#uploaderInput"),
                $uploaderFiles = $("#uploaderFiles")
                ;
        $uploaderInput.on("change", function(e){
            var src, url = window.URL || window.webkitURL || window.mozURL, files = e.target.files;
            for (var i = 0, len = files.length; i < len; ++i) {
                var file = files[i];
                if (url) {
                    src = url.createObjectURL(file);
                } else {
                    src = e.target.result;
                }
                $uploaderFiles.append($(tmpl.replace('#url#', src)));
            }
        });
        $uploaderFiles.on("click", "li", function(){
            $galleryImg.attr("style", this.getAttribute("style"));
            $gallery.fadeIn(100);
        });
        $gallery.on("click", function(){
            $gallery.fadeOut(100);
        });

        wx.ready(function(){
                $.ajax({
                    type: "GET",
                    url: WEB_ROOT+"web/repairprocess",
                    data: {'assetId': 2},

                    success:function(ret) {
                        if (ret) {
                            alert("get success-->"+ret)
                            // $('#assetName').val(ret.name);
                        }
                    }

                });

          /*  wx.scanQRCode({
                needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
                scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
                success: function (res) {
                    alert("scan ok --"+res);
                    var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
                    $.ajax({
                        type: "GET",
                        url: WEB_ROOT+"web/repairprocess",
                        data: {'assetId': 2},

                        success:function(ret) {
                            if (ret) {
                                alert("get success-->"+ret)
                               // $('#assetName').val(ret.name);
                            }
                        }

                    });
                },
                error:function(){
                    alert("hehe");
                }

            });*/


        }

        );
        //提交工单
        $('#submit').click(function(){

        });

    });
</script>
<div class="page">
    <div class="page__hd">
        <div class="weui-cells weui-cells_form">
            <div class="weui-cell">
                <div class="weui-cell__hd"><label class="weui-label">资产x名称</label></div>
                <div class="weui-cell__bd">
                    <input id="assetName" class="weui-input" type="text" placeholder="请输入资产名称"/>
                </div>
            </div>
            <div class="weui-cell">
                <div class="weui-cell__hd"><label class="weui-label">紧急度</label></div>
                <div class="weui-cell__bd">
                    <div class="weui-cell weui-cell_select">
                        <div class="weui-cell__hd">
                            <select class="weui-select" name="select2">
                                <option value="1">紧急</option>
                                <option value="2">重要</option>
                                <option value="3">一般</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <!--故障描述 -->
            <div class="weui-cell">
                <div class="weui-cell__hd"><label class="weui-label">工单名称</label></div>
                <div class="weui-cell__bd">
                    <input class="weui-input" type="text" placeholder="请输入工单名称"/>
                </div>
            </div>

            <div class="weui-cell"><label class="weui-label">故障现象</label></div>
            <div class="weui-cell">
                <div class="weui-cell__bd">
                    <textarea class="weui-textarea" placeholder="请输入故障现象" rows="3"></textarea>
                    <div class="weui-textarea-counter"><span>0</span>/200</div>
                </div>
            </div>

            <!--图片 -->
            <!--<div class="weui-cells">-->
            <div class="weui-uploader">
                <div class="weui-uploader__hd">
                    <p class="weui-uploader__title">图片上传</p>
                    <div class="weui-uploader__info">0/2</div>
                </div>
                <div class="weui-uploader__bd">
                    <ul class="weui-uploader__files" id="uploaderFiles">
                    </ul>
                    <div class="weui-uploader__input-box">
                        <input id="uploaderInput" class="weui-uploader__input" type="file" accept="image/*" multiple />
                    </div>
                </div>
            </div>
            <!--</div>-->
        </div>

        <div class="weui-btn-area">
            <a class="weui-btn weui-btn_primary" href="javascript:" id="submit">提交</a>
        </div>
    </div>


</div>
</body>
</html>
