<%--
  Created by IntelliJ IDEA.
  User: 212605082
  Date: 2017/4/18
  Time: 16:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0"/>
    <title>${orderTypeName}</title>
    <link rel="stylesheet" href="${ctx}/resources/wechat/css/weui.min.css"/>
    <script src="${ctx}/resources/wechat/js/utils/jquery-3.1.1.min.js"></script>
    <script src="${ctx}/resources/wechat/js/utils/jweixin-1.2.0.js"></script>
    <script src="${ctx}/resources/wechat/js/utils/wechatsdk.js"></script>
    <script src="${ctx}/resources/wechat/js/utils/app.js"></script>
    <script src="${ctx}/resources/wechat/js/utils/jquery.cookie.js"></script>
    <script src="${ctx}/resources/wechat/js/utils/apmRest.js"></script>
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
<body>
    <jsp:include page="/wechat/wo/listTemplate.html"/>

    <div id="pageDiv" class="page" style="display: none">
        <div class="page__bd" style="height: 100%;">
            <div class="weui-tab">
                <div class="weui-navbar">
                    <div id="noFinishCard" class="weui-navbar__item weui-bar__item_on headRole">
                        未完成
                    </div>
                    <div id="finishCard" class="weui-navbar__item headRole">
                        已完成
                    </div>
                </div>
                <div class="weui-tab__panel">
                    <div id="orderList" class="page__bd page__bd_spacing">
                    </div>
                    <div class='weui-btn-area'>
                    </div>
                    <div style="height:20px;"></div>
                </div>
            </div>
        </div>
    </div>

</body>
<script type="text/javascript">

    var pageCount = 1;
    var pageSize = 10;
    var orderType = "${orderType}";

    $(function() {
        getPage(false);

        $('#noFinishCard').on("click", function(){
            $(this).addClass('weui-bar__item_on').siblings('.weui-bar__item_on').removeClass('weui-bar__item_on');
            $("#orderList").empty();
            pageCount = 1;
            getPage(false);

        });

        $('#finishCard').on("click", function(){
            $(this).addClass('weui-bar__item_on').siblings('.weui-bar__item_on').removeClass('weui-bar__item_on');
            $("#orderList").empty();
            pageCount = 1;
            getPage(true);
        });


    });

    function getPage(isFinished){

        var data = {"orderType": orderType, "isFinished":isFinished, "page": pageCount, "pageSize": pageSize};
        apmRest.get("/hcapmassetservice/api/apm/asset/inspectionorders", data, function(ret){
            $('#moreBtn').remove();

            $('#pageDiv').show();

            $.each(ret.data.pageList, function (i, v) {

                var listItemHtml = $('#ts_listItem').html();
                var tmpl = $(listItemHtml);
                tmpl.find('h4').html("工单编号");
                tmpl.find('.ftitle').html(v.id).css('color', '#09BB07');
                tmpl.find('a').attr("href", "javascript:gotoDetail("+v.id+", "+v.orderType+");");
                var parentEl = tmpl.find('.show-data');

                var createTime = v.createTime != null ? v.createTime : "";
                var startTime = v.startTime != null ? v.startTime : "";
                var endTime = v.endTime != null ? v.endTime : "";

                parentEl.append('<p>工单名称:' + v.name + '</p>');
                parentEl.append('<p>工单状态:' + v.isFinished + '</p>');
                parentEl.append('<p>创建时间:' + createTime + '</p>');
                parentEl.append('<p>开始时间:' + startTime + '</p>');
                parentEl.append('<p>结束时间:' + endTime + '</p>');
                $('#orderList').append(tmpl);

            });

            var moreBtn = "<a id='moreBtn' href='javascript:getPage("+isFinished+");' class='weui-btn weui-btn_plain-primary'>查看更多...</a>";
            if(ret.data.pageTotal > pageCount){
                $('.weui-btn-area').append(moreBtn);
            }

            pageCount++;
        });
    }

    function gotoDetail(id, orderType){
        window.location.href = WEB_ROOT + "web/inspOrderDetail?inspId=" + id + "&orderType=" + orderType;
    }

</script>


</html>
