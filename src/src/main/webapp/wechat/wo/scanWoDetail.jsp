<%-- 
    Document   : scanWoDetail   扫码接单/签到/关单
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
        <title>扫码操作</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/weui.min.css"/>
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/wo/woprogress.css"/>
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/default-skin.css"/>
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/photoswipe.css"/>
        <script src="${ctx}/resources/wechat/js/utils/jquery-3.1.1.min.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/jweixin-1.0.0.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/wechatsdk.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/pagemanager.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/app.js"></script>
        <script src="${ctx}/resources/wechat/js/photoswipe.js"></script>
        <script src="${ctx}/resources/wechat/js/photoswipe-ui-default.min.js"></script>
        <script>
            var WEB_ROOT = '${ctx}/';
        </script>
    </head>
    <body style="background-color:#f8f8f8">
        <div id="container" class="container"></div>
        <script>
            $(function(){
                wechatSDK.setAppId('${appId}');
                wechatSDK.setSignature('${timestamp}', '${nonceStr}', '${signature}');
                wechatSDK.init();

                wechatSDK.scanQRCode(1, function(qrCode) {
                    if (qrCode.length > 16) {
                        qrCode = qrCode.substr(qrCode.length-16);
                    }
                    $.get(WEB_ROOT+"web/scanaction", {'qrCode': qrCode}, function(ret){
                        if (ret) {
                            pageManager.woId = ret.id;
                            pageManager.showBtn = ret.currentStepId === 6?
                                ret.requestorId ==ret.currentPersonId && ret.currentPersonId == '${userId}':
                                        ret.currentPersonId == '${userId}';
                            pageManager.showReView = ret.currentStepId === 6;
                            pageManager.showAssign = ret.currentStepId === 2 && pageManager.showBtn;
                            pageManager.step = ret.currentStepId;
                            pageManager.showTime = ret.currentStepId >= 3 && pageManager.showBtn && ret.currentStepId <5;
                            pageManager.showComment = ret.currentStepId > 2;
                            pageManager.signUp = ret.currentStepId === 4 && pageManager.showBtn && ret.pointStepNumber === 1;
                            pageManager.entryType = 'scan';
                            pageManager.showPat = ret.currentStepId === 5 && pageManager.showBtn;
                            pageManager.init('ts_wodetail');
                        } else {
                            pageManager.init('ts_msg_notfound');
                        }
                    });
                });
                
            });
        </script>
        
        <jsp:include page="imgshow.html"/>
        <jsp:include page="woDetail.html"/>
        <jsp:include page="tipsTemplate.html"/>
    </body>
</html>
