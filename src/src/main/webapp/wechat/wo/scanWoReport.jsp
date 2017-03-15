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
        <link rel="stylesheet" href="${ctx}/resources/wechat/weui.min.css"/>
        <script src="${ctx}/resources/wechat/js/utils/jquery-2.0.0.min.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/jweixin-1.0.0.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/wechatsdk.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/app.js"></script>
        <script>var WEB_ROOT = '${ctx}/';</script>
    </head>
    <body>
        <h1>Hello World!</h1>
        <script type="text/javascript">
            $(function(){
                wechatSDK.setAppId('${appId}');
                wechatSDK.setSignature('${timestamp}', '${nonceStr}', '${signature}');
                wechatSDK.init();
                
                wechatSDK.scanQRCode(1, function (qcode) {
                    if (qcode.length > 36) {
                        qcode = qcode.substr(qcode.length-36);
                        alert(qcode);
                    }
                });
            });
        </script>
    </body>
</html>
