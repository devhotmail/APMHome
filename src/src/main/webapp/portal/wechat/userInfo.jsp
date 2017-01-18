<%-- 
    Document   : userInfo
    Created on : 2017-1-17, 11:09:15
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
        <title>帐号绑定</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="http://res.wx.qq.com/open/libs/weui/1.1.0/weui.min.css"/>
        <script src="http://apps.bdimg.com/libs/jquery/2.1.1/jquery.min.js"></script>
    </head>
    <body>
        <div class="page">
            <div class="page__hd"> 
                <form id="userForm">
                    <div class="weui-cells weui-cells_form">
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label class="weui-label">用户名</label></div>
                            <div class="weui-cell__bd">
                                <input id="username" name="username" class="weui-input" type="text" placeholder="请输入用户名"/>
                                <input id="openId" type="hidden" name="openId" value="${openId}"/>
                            </div>
                        </div>
                        <div class="weui-cell">
                            <div class="weui-cell__hd"><label class="weui-label">密码</label></div>
                            <div class="weui-cell__bd">
                                <input id="password" name="password" class="weui-input" type="password" placeholder="请输入密码"/>
                            </div>
                        </div>
                    </div>
                    <!-- 使用 -->
                    <a href="javascript:;" id="submit" class="weui-btn weui-btn_primary">提交</a>
                </form>
            </div>
        </div>
        <script>
            var WEB_ROOT = '${ctx}/';
            $(function(){
                $('#submit').click(function(){
                    $.ajax({
                    url: WEB_ROOT+'web/binduser',
                    type: 'post',
                    data: {'username': $('#username').val(), 'password': $('#password').val(), 'openId': $('#openId').val()},
                    success: function(ret) {
                        if (ret == 'success') {
                            alert('success');
                        } else {
                            alert('用户名或密码错误')
                        }
                    },
                    error: function(ret) {
                        alert('绑定失败');
                    }
                });
                });
            });
        </script>
    </body>
</html>
