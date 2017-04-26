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
        <div id="container" class="container"></div>
        <script>
            $(function(){
                wechatSDK.setAppId('${appId}');
                wechatSDK.setSignature('${timestamp}', '${nonceStr}', '${signature}');
                wechatSDK.init();
                pageManager.init('ts_myReports');
                app.intCasePriority();
            });
        </script>
        <script type="text/html" id="ts_myReports">
            <div class="page">
                <div class="page__bd" style="height: 100%;">
                    <div class="weui-tab">
                        <div class="weui-navbar">
                            <div class="weui-navbar__item weui-bar__item_on" data-close="1">
                                未完成报修
                            </div>
                            <div class="weui-navbar__item" data-close="2">
                                已完成报修
                            </div>
                        </div>
                        <div class="weui-tab__panel">
                            <div id="myReports" class="page__bd page__bd_spacing">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <script type="text/javascript">
                $(function(){
                    //data search function
                    pageManager.myreportList = function(close) {
                        var $loadingToast1 = $('#loadingToast1');
                        if ($loadingToast1.css('display') !== 'none') return;
                        $loadingToast1.fadeIn(100);
                        //fetch data from server    wolistdata is the restful url
                        if (close === 1) {
                            pageManager.entryType = 'myreport1';
                            pageManager.showTime = true;
                            pageManager.showReView = false;
                            pageManager.showComment = false;
                            pageManager.showCancel = false;//true;
                        } else {
                            pageManager.showTime = false;
                            pageManager.showReView = true;
                            pageManager.showComment = true;
                            pageManager.showCancel = false;
                            pageManager.entryType = 'myreport2';
                        }
                        $.get(WEB_ROOT+'web/officeworkorder', {status: close}, function(ret) {
                            var data = [];
                            if (ret && ret.length !== 0) {
                                $.each(ret, function(i, v){
                                    data.push({title:'工单编号: '+ v['id'], 
                                               ftitle: pageManager.msgTypes['casePriority'][v['casePriority']], 
                                               ftitleColor : v['casePriority']===1?'#F76260':v['casePriority']===2?'#FFBE00':'#09BB07',
                                               rater: (close === 2 ? v['feedbackRating']: -1),
                                               data : ['资产编号：'+v['assetId'],
                                                        '资产名称：'+v['assetName'],
                                                        '报修人：'+v['requestorName'],
                                                        '报修时间：'+v['requestTime'],
                                                       '工单状态：'+v['currentStepName'],
                                                       '当前人员：'+v['currentPersonName']]});
                                });
                            } 
                            //show the data list
                            app.fullListItem('myReports', data);
                            $loadingToast1.fadeOut(100);
                        });
                    }
                    
                    //bind click event
                    $('#myReports').on('click', '.weui-cell_access', function(){
                        var $loadingToast = $('#loadingToast1');
                        if ($loadingToast.css('display') === 'none') {
                            $loadingToast.fadeIn(100);
                        }
                        
                        pageManager.woId = $(this).parent().find('h4').html().split(': ')[1];
                        pageManager.showMsgs = false;
                        if($(this).find('.reportview').html()) {
                            pageManager.showBtn = true;
                        } else {
                            pageManager.showBtn = false || pageManager.showCancel;
                        }
                        pageManager.go('#ts_wodetail');
                    });
                    $('.weui-navbar__item').on('click', function () {
                        $(this).addClass('weui-bar__item_on').siblings('.weui-bar__item_on').removeClass('weui-bar__item_on');
                        //do the search action     1-在修 / 2-完成 / 3-取消
                        pageManager.myreportList($(this).data('close'));
                    });
                    pageManager.myreportList(1);
                });
        </script>
           
        <jsp:include page="imgshow.html"/>
        <jsp:include page="woDetail.html"/>
        <jsp:include page="msgTemplate.html"/>
        <jsp:include page="listTemplate.html"/>
        <jsp:include page="tipsTemplate.html"/>
        <jsp:include page="wosteplist.html"/>
    </body>
</html>
