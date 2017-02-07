<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0"/>
    <title>报修处理进度</title>
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
        wx.config({
            debug:false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            appId: appId, // 必填，公众号的唯一标识
            timestamp: timestamp, // 必填，生成签名的时间戳
            nonceStr: nonceStr, // 必填，生成签名的随机串
            signature: signature,// 必填，签名，见附录1
            jsApiList: ['scanQRCode'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
        });

        var tmpl = '<li class="weui-uploader__file" style="background-image:url(#url#)"></li>',
            $gallery = $("#gallery"), $galleryImg = $("#galleryImg"),
            $uploaderInput = $("#uploaderInput"),
            $uploaderFiles = $("#uploaderFiles")
            ;

        wx.ready(function(){
                wx.scanQRCode({
                    needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
                    scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
                    success: function (res) {
                        var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
                        $.ajax({
                            type: "POST",
                            url: WEB_ROOT+"web/findRepairList",
                            data: {'assetId': res.resultStr},
                            success:function(data) {
                                if (data) {
                                    $("#wholepage").show();
                                    var html = '<div id="workOrderForm">';
                                    $.each(data, function(commentIndex, item){
                                        html +=" <a class='weui-cell weui-cell_access'>"
                                            +"<div  class=\"weui-cell__bd\" onclick=\"goWorkOrderDetail("+item.id+");\" style=\"display:block\">"
                                            +"<span>"+item.id+"</span>"+"&nbsp;&nbsp;&nbsp;&nbsp;"+"<span>"+item.assetName+"</span>"
                                            +"</div>"
                                            +"<div class=\"weui-cell__ft\"></div>"
                                            +"</a>"
                                    });
                                    html+='</div>';
                                    $( "#repairList" ).append(html);
                                }
                            },
                            error:function(){
                                alert("保修单获取失败，findRepairList");
                            }
                        })
                    },
                    error:function(){
                        alert("扫码失败，请再次扫码");
                    }

                });
            }
        );
        $('#goBack').click(function(){
            $("#workOrderDetailForm").hide();
            $("#workOrderForm").show();
            $("#weuiBtnAea").hide();
        })

    });

    function goWorkOrderDetail(id){
        // alert("ajax request");
        // $("#workOrderForm").hide();
        $.ajax({
            type: "GET",
            url: WEB_ROOT+"web/findRepairDetail",
            data: {'id': id},
            success:function(ret) {
                if (ret) {
                    $('#casePriority').val(ret[2]);
                    $('#creatorName').val(ret[0].creatorName);
                    $('#createTime').val(ret[0].createTime);
                    $('#workorder').val(ret[0].name);
                    $('#faultreason').val(ret[0].requestReason);
                    $('#casetype').val(ret[1]);
                    $('#assetname').val(ret[0].assetName);
                    $('#currentoperator').val(ret[0].requestorName);
                    $('#currentStepName').val(ret[0].currentStepName);
                    $("#workOrderForm").hide();
                    $("#workOrderDetailForm").show();
                    $("#weuiBtnAea").show();
                }
            }
        });
    }

</script>

<div id="wholepage" style="display:none" >
    <div>
        <label id="repairList" name="repairList"></label>
    </div>
    <div style="display:none" id="workOrderDetailForm">
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">紧急度</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" id="casePriority" value="" type="text" />
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">报修时间</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" id="createTime" value="" type="text" />
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">报修人</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" id="creatorName" value="" type="text" />
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">工单名称</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" id="workorder" value="" type="text" />
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">故障现象</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" id="faultreason" value="" type="text" />
            </div>
        </div>

        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">故障类别</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" id="casetype" value="" type="text" />
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">资产名称</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" id="assetname" value="" type="text" />
            </div>
        </div>

                <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">当前处理人</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" id="currentoperator" value="" type="text" />
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">当前处理步骤</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" id="currentStepName" value="" type="text" />
            </div>
        </div>


    </div>

    <div class="weui-btn-area" id="weuiBtnAea"  style="display:none" >
        <a class="weui-btn weui-btn_primary" href="javascript:" id="goBack">回退</a>
    </div>
</div>
</body>
</html>
