<%-- 
    Document   : woCreate  
    Created on : 2017-1-17, 14:30:39
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
        <title>我的工单</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="${ctx}/resources/wechat/weui.min.css"/>
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>
        <script src="${ctx}/resources/wechat/jweixin-1.0.0.js"></script>
        <style>
            .page{
                position: absolute;
                top: 0; bottom: 0; left: 0; right: 0;
                opacity: 1; background: white;
            }
            @-webkit-keyframes slideIn {
                from {
                    transform: translate3d(100%, 0, 0);
                    opacity: 0;
                }
                to {
                    transform: translate3d(0, 0, 0);
                    opacity: 1;
                }
            }

            @keyframes slideOut {
                from {
                    transform: translate3d(0, 0, 0);
                    opacity: 1;
                }
                to {
                    transform: translate3d(100%, 0, 0);
                    opacity: 0;
                }
            }
            .page.slideIn {
                animation: slideIn .2s forwards;
            }

            .page.slideOut {
                animation: slideOut .2s forwards;
            }
        </style>
        <script>var WEB_ROOT = '${ctx}/';</script>
    </head>
    <body>
        <div id="container" class="container"></div>
        <div id="loadingToast" style="display:none;">
            <div class="weui-mask_transparent"></div>
            <div class="weui-toast">
                <i class="weui-loading weui-icon_toast"></i>
                <p class="weui-toast__content"></p>
            </div>
        </div>
        <script type="text/javascript">
            $(function(){
//                $('#container').append($('#wolist').html());
                
                window.pageManager = {
                    _pageStack: [],
                    _pageIndex: 0,
                    init: function(){
                        var self = this;
                        $(window).on('hashchange', function () {
                            var state = history.state || {};
                            var page = location.hash.indexOf('#') === 0 ? location.hash : '#';
                            if (state._pageIndex <= self._pageIndex ) {
                                self._back();
                            } else {
                                self._go(page);
                            }
                        });
                        this.go('#wolist');
                    },
                    go: function(page) {
                        location.hash = page;   
                    },
                    _go: function(page) {
                        this._pageIndex++;
                        history.replaceState && history.replaceState({_pageIndex: this._pageIndex}, '', location.href);
                        var html = $(page).html()+'<\/script>';
                        var $html = $(html);
                        $html.addClass('slideIn');
                        $('#container').append($html);
                        this._pageStack.push({dom: $html});
                    },
                    _back: function() {
                        this._pageIndex--;
                        var stack = this._pageStack.pop();
                        if (!stack) {
                            return;
                        }
                        stack.dom.addClass('slideOut').on('animationend webkitAnimationEnd', function () {
                            stack.dom.remove();
                        });
                    }
                }
                pageManager.init();
            });
        </script>
        <script type="text/html" id="no_data">
            <div class="page">
                <div class="weui-msg">
                    <div class="weui-msg__icon-area"><i class="weui-icon-info weui-icon_msg"></i></div>
                    <div class="weui-msg__text-area">
                        <h2 class="weui-msg__title">暂时没有需要处理的工单</h2>
                    </div>
                    <div class="weui-msg__opr-area">
                        <p class="weui-btn-area">
                            <a href="javascript:WeixinJSBridge.call('closeWindow');" class="weui-btn weui-btn_primary">返回菜单</a>
                        </p>
                    </div>
                </div>
            </div>
        </script>
        <script type="text/html" id="wolist">
            <div class="page">
                <div id="ui_list" class="page__bd page__bd_spacing">
                </div>
            </div>
            <script type="text/javascript">var test=null;
                $(function(){
                    //fetch data from server
                    var $loadingToast = $('#loadingToast');
                    $loadingToast.fadeIn(100);
                    $loadingToast.find('.weui-toast__content').html('数据加载中...');
                    var $ui_list = $('#ui_list');
                    var tmpl = $('#wo_li').html();
                    $.ajax({
                        url: WEB_ROOT+'web/wolistdata',
                        type: 'get',
                        contentType: 'application/json',
                        success: function(ret) {
                            if (ret && ret.length !=0) {
                                $.each(ret, function(idx, value){
                                    var $tmpl = $(tmpl);
                                    $tmpl.find('#li_title').html('工单编号：'+ value['id']);
                                    $tmpl.find('#li_ft').html(value['requestTime']);
                                    var lcs = $tmpl.find('#li_context p');
                                    $(lcs[0]).html('资产编号：'+value['assetId']);
                                    $(lcs[1]).html('资产名称：'+value['assetName']);
                                    $(lcs[2]).html('工单状态：'+value['currentStepName']);
                                    $ui_list.append($tmpl);
                                });
                                $loadingToast.fadeOut(100);
                            } else {
                                $loadingToast.fadeOut(100);
                                $('#container').empty();
                                $('#container').append($('#no_data').html());
                            }
                        }
                    });

                    //bind click event
                    $('#ui_list').on('click', '.weui-cell_access', function(){
                        pageManager.go('#wo_detail');
                    });
                });
        </script>
        
        <script type="text/html" id="wo_detail">
            <div class="page">
                <div class="page__bd">
                    <div class="weui-tab">
                        <div class="weui-navbar">
                            <div class="weui-navbar__item weui-bar__item_on">
                                选项
                            </div>
                           <div class="weui-navbar__item weui-bar__item_on">
                               <span style="display: inline-block;position: relative;">
                                   <span class="weui-badge" style="position: absolute;top: -2px;right: -13px;">测试</span>
                               </span>
                           </div>
                           <div class="weui-navbar__item">
                               <p class="weui-tabbar__label">通讯录</p>
                           </div>
                           <div class="weui-navbar__item">
                               <span style="display: inline-block;position: relative;">
                                   <span class="weui-badge weui-badge_dot" style="position: absolute;top: 0;right: -6px;"></span>
                               </span>
                           </div>
                           <div class="weui-navbar__item">
                               <p class="weui-tabbar__label">我</p>
                           </div>
                       </div>
                   </div>
               </div>
           </div>
        <script type="text/javascript">
            $(function(){
                $('.weui-tabbar__item').on('click', function () {
                    $(this).addClass('weui-bar__item_on').siblings('.weui-bar__item_on').removeClass('weui-bar__item_on');
                });
            });
        </script>
        
        <script type="text/html" id="wo_li">
            <div class="weui-cells">
                <div class="weui-cell">
                    <div class="weui-cell__bd">
                        <h4 id="li_title"></h4>
                    </div>
                    <div id="li_ft" class="weui-cell__ft"></div>
                </div>
                <a class="weui-cell weui-cell_access" href="javascript:;">
                    <div id="li_context" class="weui-cell__bd">
                        <p></p>
                        <p></p>
                        <p></p>
                    </div>
                    <div class="weui-cell__ft">
                    </div>
                </a>
            </div>
        </script>
    </body>
</html>
