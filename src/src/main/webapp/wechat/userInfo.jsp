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
<meta charset="UTF-8" />
<meta name="viewport"
	content="width=device-width,initial-scale=1,user-scalable=0" />
<title>帐号绑定</title>
<!-- 引入 WeUI -->
<link rel="stylesheet" href="${ctx}/resources/wechat/css/weui.min.css" />
<script src="http://libs.baidu.com/jquery/1.9.1/jquery.min.js"></script>
</head>
<body>
	<div id="container" class="container"></div>
	<script>
		$(function() {
			$('#container').append($('#create').html());
		});
	</script>
	<script type="text/html" id="create">
            <div class="page">
                <div class="page__bd"> 
					<div class="weui-cells__title">已绑定信息</div>
					<form id= "originUserForm" >
						<div class="weui-cells weui-cells_form">
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">医院</label></div>
                                <div class="weui-cell__bd">
                                    <input id="hospitalName" class="weui-input" name="hospitalName" value="${user.hospitalName}" readonly="readonly" />
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">名字</label></div>
                                <div class="weui-cell__bd">
                                    <input id="name" class="weui-input" name="name" value="${user.userName}" readonly="readonly" />
                                </div>
                            </div>
							<div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">角色</label></div>
                                <div class="weui-cell__bd">
                                    <input id="userRole" class="weui-input"  name="userRole" value="${user.userRole}" readonly="readonly" />
                                </div>
                            </div>
							<div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">手机号</label></div>
                                <div class="weui-cell__bd">
                                    <input id="loginName" class="weui-input" name="loginName" value="${user.loginName}" readonly="readonly" />
                                </div>
                            </div>
                        </div>
					</form>

					<div class="weui-cells__title">绑定账号</div>
                    <form id="userForm">
                        <div class="weui-cells weui-cells_form">
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">手机号</label></div>
                                <div class="weui-cell__bd">
                                    <input id="username" name="username" class="weui-input" type="text" placeholder="请输入手机号"/>
                                    <input id="openId" type="hidden" name="openId" value="${openId}"/>
                                </div>
                            </div>
                            <div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">密码</label></div>
                                <div class="weui-cell__bd">
                                    <input id="password" name="password" class="weui-input" type="password" placeholder="请输入密码"/>
                                </div>
                            </div>
							<div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">新密码</label></div>
                                <div class="weui-cell__bd">
                                    <input id="newPwd" name="newPwd" class="weui-input" type="password" placeholder="请输入新密码"/>
                                </div>
                            </div>
							<div class="weui-cell">
                                <div class="weui-cell__hd"><label class="weui-label">确认新密码</label></div>
                                <div class="weui-cell__bd">
                                    <input id="confirmPwd" name="confirmPwd" class="weui-input" type="password" placeholder="请重新输入新密码"/>
                                </div>
                            </div>
                        </div>
                        <!-- 使用 -->
                        <div class="weui-btn-area">
                            <a class="weui-btn weui-btn_primary" href="javascript:" id="submit">提交</a>
                        </div>
                    </form>
                </div>
                <div id="loadingToast" style="display:none;">
                    <div class="weui-mask_transparent"></div>
                    <div class="weui-toast">
                        <i class="weui-loading weui-icon_toast"></i>
                        <p class="weui-toast__content">帐号绑定中</p>
                    </div>
                </div>
            </div>

            <script type="text/javascript">
                var WEB_ROOT = '${ctx}/';
                $(function(){
        		$('#submit').click(function(){
				var flag = true;			
            	var Inputs = $('#userForm').find('input');
            $.each(Inputs, function (idx, value) {
                if (value.type === "password") {
                    if (value.value === "") {
                        flag = false;
                        $(value).parent().parent().addClass('weui-cell_warn');
                    } else {
                        $(value).parent().parent().removeClass('weui-cell_warn');
                    }
                }else if(value.type === "text"){
					if(value.value === ""){
						flag = false;
                        $(value).parent().parent().addClass('weui-cell_warn');
					}else{
						$(value).parent().parent().removeClass('weui-cell_warn');
					}
					
					var reg = /^1[34578]\d{9}$/; //^1[3|4|5|7|8][0-9]\d{4,8}$/; 
					if(!reg.test(value.value)){
						flag = false;
						$(value).parent().parent().addClass('weui-cell_warn');
					}else{
						$(value).parent().parent().removeClass('weui-cell_warn');
					}
				}
            });
            if (flag) {
                var confirmPwd = $("#confirmPwd")[0];
                if ($("#newPwd")[0].value != confirmPwd.value) {
                    flag = false;
                    $(confirmPwd).parent().parent().addClass('weui-cell_warn');
                    confirmPwd.value = "";
                }
            }							
			if(flag){
				var $loadingToast = $('#loadingToast');
                        $.ajax({
                            url: WEB_ROOT+'web/binduser',
                            type: 'post',
                            data: {'username': $('#username').val(), 'password': $('#password').val(), 'openId': $('#openId').val(),'newPwd':$('#newPwd').val()},
                            beforeSend: function( xhr ) { 
                                xhr.setRequestHeader('X-Requested-With', {toString: function(){ return ''; }});
                                },
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
						}
                    });
                });
		                  
        </script>
	<script type="text/html" id="msg_success">
            <div class="page">
                <div class="weui-msg">
                    <div class="weui-msg__icon-area"><i class="weui-icon-success weui-icon_msg"></i></div>
                    <div class="weui-msg__text-area">
                        <h2 class="weui-msg__title">绑定成功</h2>
                    </div>
                    <div class="weui-msg__opr-area">
                        <p class="weui-btn-area">
                            <a href="javascript:WeixinJSBridge.call('closeWindow');" class="weui-btn weui-btn_primary">返回菜单</a>
                        </p>
                    </div>
                </div>
            </div>
        </script>
	<script type="text/html" id="msg_failed">
            <div class="page">
                <div class="weui-msg">
                    <div class="weui-msg__icon-area"><i class="weui-icon-warn weui-icon_msg"></i></div>
                    <div class="weui-msg__text-area">
                        <h2 class="weui-msg__title">绑定失败</h2>
                        <p class="weui-msg__desc">帐号或密码错误</p>
                    </div>
                    <div class="weui-msg__opr-area">
                        <p class="weui-btn-area">
                            <a href="javascript:WeixinJSBridge.call('closeWindow');" class="weui-btn weui-btn_primary">返回菜单</a>
                        </p>
                    </div>
                </div>
            </div>
        </script>
</body>
</html>
