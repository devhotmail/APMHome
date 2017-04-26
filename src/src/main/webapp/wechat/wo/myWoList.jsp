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
        <style>
            .weui-navbar__item.weui-bar__item_on {
                background-color: #f8f8f8;
                border-bottom: 1px solid #f8f8f8;
            }
            .weui-navbar:after {
                border-bottom: 0;
            }
            .weui-navbar__item:after {
                border-bottom: 0;
            }
            .weui-navbar__item {
                background-color: #eaeaea;
                border-bottom: 1px solid #ccc;
            }
        </style>
    </head>
    <body style="background-color:#f8f8f8">
        <jsp:include page="imgshow.html"/>
        
        <div id="container" class="container"></div>
        <script>
            $(function(){
                wechatSDK.setAppId('${appId}');
                wechatSDK.setSignature('${timestamp}', '${nonceStr}', '${signature}');
                wechatSDK.init();
                pageManager.hospitalId = '${hospitalId}';
                //show the tabs by the role of the user
                $.get(WEB_ROOT+'web/choosetab', function(ret){
                    if (ret && ret !== 3) {
                        if (ret === 1) { // assigner
                            pageManager.assetHead = true;
                        } else { // worker
                            pageManager.assetHead = false;
                        }
                        app.intCasePriority();
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
                                待派工
                            </div>
                            <div class="weui-navbar__item headRole" data-step="2">
                                已派工
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
                            <div class="weui-navbar__item" data-step="6">
                                已关闭工单
                            </div>
                        </div>
                        <div class="weui-tab__panel">
                            <div id="wolist" class="page__bd page__bd_spacing">
                            </div>
                            <div class="weui-btn-area">
                                <a class="weui-btn weui-btn_plain-primary" href="javascript:" id="loadMore">查看更多...</a>
                            </div>
                            <div style="height:20px;"></div>
                        </div>
                    </div>
                </div>
            </div>
            <script type="text/javascript">
                $(function(){
                    pageManager.mywolist = function(){
                        //data search function
                        function loadData(step) {
                            pageManager.pageNum = 0;
                            pageManager.pageSize = 10;
                            var $loadingToast1 = $('#loadingToast1');
                            if ($loadingToast1.css('display') !== 'none') return;
                            $loadingToast1.fadeIn(100);
                            //fetch data from server    wolistdata is the restful url
                            $.get(WEB_ROOT+'web/wolistdata', {stepId: step, pageSize: pageManager.pageSize, pageNum: pageManager.pageNum}, function(ret) {
                                pageManager.workOrders = [];
                                var data = [];
                                assembleData(data, ret);
                                //show the data list
                                app.fullListItem('wolist', data);
                                $loadingToast1.fadeOut(100);
                            });
                        }
                        
                        $('#loadMore').click(function(){
                            var $loadingToast1 = $('#loadingToast1');
                            if ($loadingToast1.css('display') !== 'none') return;
                            $loadingToast1.fadeIn(100);
                            //fetch data from server    wolistdata is the restful url
                            $.get(WEB_ROOT+'web/wolistdata', {stepId: pageManager.tab, pageSize: pageManager.pageSize, pageNum: pageManager.pageNum}, function(ret) {
                                var data = [];
                                assembleData(data, ret);
                                //show the data list
                                app.appendListItem('wolist', data);
                                $loadingToast1.fadeOut(100);
                            });
                        });

                        //bind click event
                        $('#wolist').on('click', '.weui-cell_access', function(){
                            var $loadingToast = $('#loadingToast1');
                            if ($loadingToast.css('display') === 'none') {
                                $loadingToast.fadeIn(100);
                            }
                            
                            pageManager.woId = $(this).parent().find('h4').html().split(': ')[1];

                            var wo = pageManager.workOrders[pageManager.woId];
                            pageManager.entryType = 'wolist';
//                            pageManager.showBtn = (wo.currentPersonId == '${userId}' || !pageManager.assetHead) && pageManager.tab !== 6;
//                            pageManager.takeOtherWo = !pageManager.assetHead && wo.currentPersonId != '${userId}';
//                            pageManager.go('#ts_wodetail');
                            switch(wo.currentStepId) {
                                case 2: pageManager.go('#ts_assignWo');break;
                                case 3: pageManager.tab===5?pageManager.go('#ts_takeOtherWo'):pageManager.go('#ts_takeWo');break;
                                case 4: pageManager.tab===5?pageManager.go('#ts_takeOtherWo'):pageManager.go('#ts_repairWo');break;
                                case 5: pageManager.go('#ts_closeWo');break;
                                default: pageManager.go('#ts_ratingWo');
                            }
                        });
                        $('.weui-navbar__item').on('click', function () {
                            $(this).addClass('weui-bar__item_on').siblings('.weui-bar__item_on').removeClass('weui-bar__item_on');
                            //do the search action     1-在修 / 2-完成 / 3-取消
                            pageManager.tab = $(this).data('step');
                            loadData($(this).data('step'));
                        });

                        if (pageManager.assetHead) {
                            $('.staffRole').hide();
                            pageManager.tab = 1;
                            loadData(1);
                        } else {
                            $('.headRole').hide();
                            pageManager.tab = 3;
                            loadData(3);
                        }
                    }
                    pageManager.mywolist();
                    
                    function assembleData(data, ret) {
                        if (ret && ret.content && ret.content.length !== 0) {
                            $.each(ret.content, function(i, v){
                                pageManager.workOrders[v['id']] = v;
                                data.push({title:'工单编号'+(pageManager.tab===5?(pageManager.hospitalId==v['hospitalId']?'':'/'+v['hospitalName']):'')+': '+ v['id'],
                                       ftitle: pageManager.msgTypes['casePriority'][v['casePriority']], 
                                       ftitleColor : v['casePriority']===1?'#F76260':v['casePriority']===2?'#FFBE00':'#09BB07',
                                       rater: (close === 2 ? v['feedbackRating']: -1),
                                       data : ['资产编号：'+v['assetId'],
                                                '资产名称：'+v['assetName'],
                                                '报修人：'+v['requestorName'],
                                                '报修时间：'+v['requestTime'],
                                               '工单状态：'+v['currentStepName'] ,
                                               '当前人员：'+v['currentPersonName']]});
                            });
                            pageManager.pageNum = ret.number +1;
                        } 
                        if (!ret.totalPages || pageManager.pageNum >= ret.totalPages) {
                            $('#loadMore').hide();
                        } else {
                            $('#loadMore').show();
                        }
                    }
                });
        </script>
        
        <jsp:include page="wosteps/assignWo.html"/>
        <jsp:include page="wosteps/takeWo.html"/>
        <jsp:include page="wosteps/repairWo.html"/>
        <jsp:include page="wosteps/closeWo.html"/>
        <jsp:include page="wosteps/ratingWo.html"/>
        <jsp:include page="wosteps/takeOtherWo.html"/>
        <jsp:include page="msgTemplate.html"/>
        <jsp:include page="listTemplate.html"/>
        <jsp:include page="tipsTemplate.html"/>
        <jsp:include page="workorderCost.html"/>
        <jsp:include page="wosteplist.html"/>
    </body>
</html>
