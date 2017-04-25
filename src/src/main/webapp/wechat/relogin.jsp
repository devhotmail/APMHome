<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport"
              content="width=device-width,initial-scale=1,user-scalable=0" />
        <title>重新进入</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/weui.min.css" />
        <script src="${ctx}/resources/wechat/js/utils/jweixin-1.2.0.js"></script>
    </head>
    <body>
        <div class="page">
            <div class="weui-msg">
                <div class="weui-msg__icon-area"><i class="weui-icon-warn weui-icon_msg"></i></div>
                <div class="weui-msg__text-area">
                    <h2 class="weui-msg__title">会话失效</h2>
                    <p class="weui-msg__desc">长时间无操作会导致会话失效，需要返回菜单重新进入建立新的会话</p>
                </div>
                <div class="weui-msg__opr-area">
                    <p class="weui-btn-area">
                        <a href="javascript:WeixinJSBridge.call('closeWindow');" class="weui-btn weui-btn_primary">返回菜单</a>
                    </p>
                </div>
            </div>
        </div>

    </body>
</html>
