<%-- 
    Document   : myReport   我的报修单
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
        <title>我的报修单</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/weui.min.css"/>
        <script src="${ctx}/resources/wechat/js/utils/jquery-3.1.1.min.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/jweixin-1.0.0.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/wechatsdk.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/pagemanager.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/app.js"></script>
        <script>
            var WEB_ROOT = '${ctx}/';
        </script>
    </head>
    <body style="background-color:#f8f8f8">
        <div id="container" class="container"></div>
        <script>
            $(function(){
                pageManager.init('ts_myReports');
            });
        </script>
        <script type="text/html" id="ts_myReports">
            <div class="page">
                <div id="myReports" class="page__bd page__bd_spacing">
                </div>
            </div>
            <script type="text/javascript">
                $(function(){
                    //fetch data from server    wolistdata is the restful url
                    $.get(WEB_ROOT+'web/wolistdata', function(ret) {
                        if (ret && ret.length !== 0) {
                            var data = [];
                            $.each(ret, function(i, v){
                                data.push({title:'工单编号: '+ v['id'], 
                                           ftitle: v['requestTime'], 
                                           data : ['资产名称：'+v['assetName'],
                                                   '工单状态：'+v['currentStepName'],
                                                   '紧急程序：'+v['casePriority']]});
                            });
                            //show the data list
                            app.fullListItem('myReports', data);
                        } else {
                            $('#container').empty();
                            $('#container').append($('#ts_no_data').html());
                        }
                    });

                    //bind click event
                    $('#myReports').on('click', '.weui-cell_access', function(){
                    });
                });
        </script>
           
        <jsp:include page="listTemplate.html"/>
        <jsp:include page="tipsTemplate.html"/>
    </body>
</html>
