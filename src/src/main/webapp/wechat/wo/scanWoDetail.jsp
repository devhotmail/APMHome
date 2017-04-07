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
        <title>工单详情</title>
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
                
                function initWoDetail(qrcode, woId) {
                    $.get(WEB_ROOT+"web/scanaction", {'qrCode': qrcode, 'woId': woId}, function(ret){
                        if (ret) {
                            pageManager.woId = ret.id;
                            pageManager.entryType = 'scan';
                            pageManager.signUp = false;//ret.currentStepId === 4 && ret.pointStepNumber === 1;
                            pageManager.showCancel = false;//ret.requestorId == '${userId}';
                            pageManager.showBtn = ret.currentStepId === 6?
                                ret.requestorId == '${userId}'&& ret.feedbackRating === 0:
                                        (ret.currentPersonId == '${userId}'|| (ret.currentStepId < 4&&pageManager.showCancel))&&ret.status !== 3;
                            if (ret.currentStepId === 6 && ret.requestorId != '${userId}') {
                                pageManager.init('ts_msg_notfound');
                            }
                            pageManager.init('ts_wodetail');
                        } else {
                            pageManager.init('ts_msg_notfound');
                        }
                    });
                }

                pageManager.from = '${from}';
                var woId = '${woId}';
                if (woId){
                    initWoDetail('', woId);
                }else {
                    wechatSDK.scanQRCode(1, function(qrcode) {
                        if (qrcode.length > 16) {
                            qrcode = qrcode.substr(qrcode.length-16);
                        }
                        initWoDetail(qrcode, '');
                    });
                }
                
            });
        </script>
        
        <jsp:include page="imgshow.html"/>
        <jsp:include page="woDetail.html"/>
        <jsp:include page="tipsTemplate.html"/>
        <jsp:include page="workorderCost.html"/>
        <jsp:include page="wosteplist.html"/>
    </body>
</html>
