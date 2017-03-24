<%-- 
    Document   : myWoList   工单管理(根据不同的人显示不同的工单)
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
        <title>工单管理</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/weui.min.css"/>
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/wo/woprogress.css"/>
        <script src="${ctx}/resources/wechat/js/utils/jquery-3.1.1.min.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/jweixin-1.0.0.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/wechatsdk.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/pagemanager.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/app.js"></script>
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/photoswipe.css"/>
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/default-skin.css"/>
        <script src="${ctx}/resources/wechat/js/photoswipe.js"></script>
        <script src="${ctx}/resources/wechat/js/photoswipe-ui-default.min.js"></script>
    
        <script>
            var WEB_ROOT = '${ctx}/';
        </script>
    </head>
    <body style="background-color:#f8f8f8">
        <jsp:include page="imgshow.html"/>
        
        <div id="container" class="container"></div>
        <script>
            $(function(){
                //show the tabs by the role of the user
                $.get(WEB_ROOT+'web/choosetab', function(ret){
                    if (ret) {
                        if (ret === 1) { // assetHead
                            pageManager.assetHead = true;
                        } else { // assetStaff
                            pageManager.assetHead = false;
                        }
                        pageManager.init('ts_mywoList');
                    } else {
                        pageManager.init('ts_role_not_found');
                    }
                });
            });
        </script>
        
        <script type="text/html" id="ts_mywoList">
            <div class="page">
                <div class="page__bd" style="height: 100%;">
                    <div class="weui-tab">
                        <div class="weui-navbar">
                            <div class="weui-navbar__item weui-bar__item_on headRole" data-step="1">
                                待派单
                            </div>
                            <div class="weui-navbar__item headRole" data-step="2">
                                已派单
                            </div>
                            <div class="weui-navbar__item weui-bar__item_on staffRole" data-step="3">
                                未接工单
                            </div>
                            <div class="weui-navbar__item staffRole" data-step="4">
                                已接工单
                            </div>
                            <div class="weui-navbar__item staffRole" data-step="5">
                                他人工单
                            </div>
                        </div>
                        <div class="weui-tab__panel">
                            <div id="wolist" class="page__bd page__bd_spacing">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <script type="text/javascript">
                $(function(){
                    
                    //data search function
                    function loadData(step) {
                        //fetch data from server    wolistdata is the restful url
                        $.get(WEB_ROOT+'web/wolistdata', {stepId: step}, function(ret) {
                            pageManager.workOrders = [];
                            var data = [];
                            if (ret && ret.length !== 0) {
                                $.each(ret, function(i, v){
                                    pageManager.workOrders[v['id']] = v;
                                    data.push({title:'工单编号: '+ v['id'], 
                                               ftitle: v['requestTime'], 
                                               data : ['资产名称：'+v['assetName'],
                                                       '工单状态：'+v['currentStepName'],
                                                       '紧急程序：'+v['casePriority']]});
                                });
                            } 
                            //show the data list
                            app.fullListItem('wolist', data);
                        });
                    }
                    
                    //bind click event
                    $('#wolist').on('click', '.weui-cell_access', function(){
                        pageManager.woId = $(this).parent().find('h4').html().split(': ')[1];
                        
                        var wo = pageManager.workOrders[pageManager.woId];
//                        pageManager.showMsgs = wo.currentStepId === 4;
                        pageManager.showBtn = wo.currentStepId === 6?false:true;
                        pageManager.showAssign = wo.currentStepId === 2 && pageManager.showBtn;
                        pageManager.step = wo.currentStepId;
                        pageManager.showTime = wo.currentStepId >= 3 && pageManager.showBtn && wo.currentStepId <5;
                        
                        pageManager.showReView = false;
                        
                        pageManager.showComment = wo.currentStepId > 2;
                        pageManager.signUp = wo.currentStepId === 4 && pageManager.showBtn && wo.pointStepNumber === 1;
                        pageManager.entryType = 'wolist';
                        pageManager.showPat = wo.currentStepId === 5 && pageManager.showBtn;
                        
                        pageManager.go('#ts_wodetail');
                    });
                    $('.weui-navbar__item').on('click', function () {
                        $(this).addClass('weui-bar__item_on').siblings('.weui-bar__item_on').removeClass('weui-bar__item_on');
                        //do the search action     1-在修 / 2-完成 / 3-取消
                        loadData($(this).data('step'));
                    });
                    
                    if (pageManager.assetHead) {
                        $('.staffRole').hide();
                        loadData(1);
                    } else {
                        $('.headRole').hide();
                        loadData(3);
                    }
                    
                });
        </script>
        
        <jsp:include page="woDetail.html"/>
        <jsp:include page="msgTemplate.html"/>
        <jsp:include page="listTemplate.html"/>
        <jsp:include page="tipsTemplate.html"/>
        
    </body>
</html>
