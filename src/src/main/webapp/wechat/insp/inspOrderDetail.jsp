<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0"/>
        <title>dfdfd</title>
        <!-- 引入 WeUI -->
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/weui.min.css"/>
        <link rel="stylesheet" href="${ctx}/resources/wechat/css/example.css"/>
        <script src="${ctx}/resources/wechat/js/utils/jquery-3.1.1.min.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/jquery.cookie.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/jweixin-1.0.0.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/wechatsdk.js"></script>
        <script src="${ctx}/resources/wechat/js/utils/apmRest.js"></script>
        <style>
            .submargin {margin: 0 20px;opacity: .4;}
            .weui-flex {opacity: 1!important;}
        </style>
        <script>
            var WEB_ROOT = '${ctx}/';
        </script>
    </head>
    <body style="background-color:#f8f8f8">
        
        <div id="container" class="container">
            <div class="page home js_show">
                <div class="page__hd" style="padding: 10px 0;height: 30px;">
                    <a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_plain-primary" id="chooseAll" style="float: right; margin-right: 40px; border: 0">取消全选</a>
                </div>
                <div class="page__bd page__bd_spacing">
                    <ul id="asestItems">
                        
                    </ul>
                    
                    <div class="weui-gallery" id="gallery">
                        <span class="weui-gallery__img" id="galleryImg"></span>
                        <div class="weui-gallery__opr">
                            <a href="javascript:" class="weui-gallery__del">
                                <i class="weui-icon-delete weui-icon_gallery-delete"></i>
                            </a>
                        </div>
                    </div>
                    <div class="weui-cells__title">图片上传<div class="weui-uploader__info" style="float: right;"><span id="countnum">0</span>/1</div></div>
                    <div class="weui-cells">
                        <div class="weui-cell">
                            <div class="weui-cell__bd">
                                <div class="weui-uploader">
                                    <div class="weui-uploader__bd">
                                        <ul class="weui-uploader__files" id="uploaderFiles">
                                        </ul>
                                        <div class="weui-uploader__input-box">
                                            <input id="uploaderInput" class="weui-uploader__input" type="file" accept="image/*" />
                                            <input type="hidden" id="deleteImg">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="weui-cells__title">文字描述</div>
                    <div class="weui-cells">
                        <div class="weui-cell">
                            <div class="weui-cell__bd">
                                <textarea id="comments" class="weui-textarea" rows="3"></textarea>
                            </div>
                        </div>
                    </div>
                    
                </div>
                <div class="weui-btn-area">
                    <a href="javascript:;" class="weui-btn weui-btn_primary" id="submit">保存</a>
                </div>
            </div>
        </div>
        <script>
            $(function(){
                wechatSDK.setAppId('${appId}');
                wechatSDK.setSignature('${timestamp}', '${nonceStr}', '${signature}');
                wechatSDK.init();
                
                function initData() {
                    apmRest.get('/hcapmassetservice/api/apm/asset/inspection/'+'${inspId}', null, function(ret){
                        assembleData(ret);
                    });
                }
                initData();
                
                function assembleData(ret) {
                    //var details = [{assetName: 'ddfd', items:[{name:'1111'},{name:'2222'}]},{assetName: 'ggggg', items:[{name:'3333'},{name:'4444'}]}];
                    if (ret.bizStatusCode === 'OK' && ret.data && ret.data.details && ret.data.details.length > 0)  {
                        window.inspDetails = ret.data.details;
                        window.orderId = ret.data.order.id;
                        setAllDetails(true);
                        var assets = [];
                        $.each(ret.data.details, function(idx, val){
                            if (assets.indexOf(val.assetName) === -1) {
                                assets.push(val.assetName);
                            }
                        });
                        var details = [];
                        $.each(assets, function(idx, val){
                            var asset = {};
                            asset.assetName = val;
                            asset.items = [];
                            $.each(ret.data.details, function(i, v){
                                if (asset.assetName === v.assetName) {
                                    var item = {};
                                    item.id = v.id;
                                    item.name = v.itemName;
                                    asset.items.push(item);
                                }
                            });
                            details.push(asset);
                        });
                        initAssetList(details);
                        binds();
                        imageAction();
                    } else {
                        $('#container').empty().append('<div class="page js_show"><div class="page_hd"><h1>没有数据...</h1></div></div>');
                    }
                }
                
                function initAssetList(details) {
                    var html = $('#assetItem').html();
                    var item = $('#checkItem').html();
                    var $ul = $('#asestItems');
                    $.each(details, function(idx, val){
                        var $html = $(html);
                        $html.find('.js_category p').html(val.assetName);
                        var $content = $html.find('.js_categoryInner .weui-cells_checkbox');
                        $.each(val.items, function(i,v){
                            var $item = $(item);
                            $item.find('p').html(v.name);
                            $item.find('input').attr('data-id', v.id);
                            $content.append($item);
                        });
                        $ul.append($html);
                    });
                }
                
                //click chooseAll to reset the values
                function setAllDetails(isPassed) {
                    if (window.inspDetails) {
                        $.each(window.inspDetails, function(i, v){
                            v.isPassed = isPassed;
                        });
                    }
                }
                
                //click item_checked to reset the few items
                function setAssetDetails(items) {
                    if (window.inspDetails) {
                        $.each(items, function(idx, val){
                            $.each(window.inspDetails, function(i, v){
                                if ($(val).data('id') === v.id){
                                    if($(val).is(':checked')){
                                        v.isPassed = true;
                                    }else {
                                        v.isPassed = false;
                                    }
                                    return false;
                                }
                            });
                        });
                    }
                }
                
                function imageAction() {
                    //图片功能开始
                    var tmpl = '<li class="weui-uploader__file" style="background-image:url(#url#)"></li>',
                        $gallery = $("#gallery"), $galleryImg = $("#galleryImg"),
                        $uploaderInput = $("#uploaderInput"),
                        $uploaderFiles = $("#uploaderFiles");
                    $uploaderInput.on("click", function(e){
                        if($uploaderFiles.children().length >= 1){
                            $('#iosDialog2').fadeIn(200);
                            return false;
                        }
                        wechatSDK.chooseImages(1-$uploaderFiles.children().length, upload);
//                        return false;
                    });
                    $uploaderFiles.on("click", "li", function(){
                        $('#deleteImg').val($(this).data('serid'));
                        $galleryImg.attr("style", this.getAttribute("style"));
                        $gallery.fadeIn(100);
                    });
                    $gallery.on("click", function(){
                        $gallery.fadeOut(100);
                    });
                    $('#dialogs').on('click', '.weui-dialog__btn', function(){
                        $(this).parents('.js_dialog').fadeOut(200);
                    });
                    $('.weui-gallery__del').on('click', function(){
                        var serid = $('#deleteImg').val();
                        $uploaderFiles.find('[data-serid='+serid+']').remove();
                        $('#countnum').html($uploaderFiles.children().length);
                    });
                    function upload(res) {
                        if (window.__wxjs_is_wkwebview){
                            wx.getLocalImgData({
                                localId: res[0],
                                success: function(ress){
                                    var localData = ress.localData.replace('jgp', 'jpeg');
                                    $uploaderFiles.append($(tmpl.replace('#url#', localData)).attr('data-serid', rest.serverId));
                                    $('#countnum').html($uploaderFiles.children().length);
                                }
                            });
                        } else {
                            $uploaderFiles.append($(tmpl.replace('#url#', res[0])).attr('data-serid', rest.serverId));
                            $('#countnum').html($uploaderFiles.children().length);
                        }
                    }
                    //图片功能结束
                }
                
                function binds() {
                    var winH = $(window).height();
                    var categorySpace = 10;

                    $('.js_category').on('click', function(){
                        var $this = $(this),
                            $inner = $this.next('.js_categoryInner'),
                            $page = $this.parents('.page'),
                            $parent = $(this).parent('li');
                        var innerH = $inner.data('height');
                        if(!innerH){
                            $inner.css('height', 'auto');
                            innerH = $inner.height();
                            $inner.removeAttr('style');
                            $inner.data('height', innerH);
                        }
                        if($parent.hasClass('js_show')){
                            $parent.removeClass('js_show');
                        }else{
                            $parent.siblings().removeClass('js_show');
                            $parent.addClass('js_show');
                            if(this.offsetTop + this.offsetHeight + innerH > $page.scrollTop() + winH){
                                var scrollTop = this.offsetTop + this.offsetHeight + innerH - winH + categorySpace;
                                if(scrollTop > this.offsetTop){
                                    scrollTop = this.offsetTop - categorySpace;
                                }
                                $page.scrollTop(scrollTop);
                            }
                            if ($parent.find('.js_category input').is(':checked') !== $parent.find('.js_categoryInner input').is(':checked')) {
                                $parent.find('.js_categoryInner input').click();
                            }
                        }
                        if($('li input:checked').length === $('li input').length) {
                            $('#chooseAll').html('取消全选');
                        } else {
                            $('#chooseAll').html('全选');
                        }
                    });

                    $('.item_checked').on('click', function(){
                        var $this = $(this);
                        var items = $this.parents('li').find('.js_categoryInner input');
                        items.click();
                        setAssetDetails(items);
                        return $this.siblings().click();
                    });

                    $('#chooseAll').on('click', function(){
                        var $this = $(this);
                        if ('全选' === $this.html()) {
                            $this.html('取消全选');
                            $.each($('li input'), function(i, v){
                                if(!$(v).is(':checked')){
                                    $(v).click();
                                }
                            });
                            setAllDetails(true);
                        } else {
                            $this.html('全选');
                            $('li input:checked').click();
                            setAllDetails(false);
                        }
                        $('li.js_show').removeClass('js_show');
                    });
                    
                    $('#submit').click(function(){
                        var $loadingToast = $('#loadingToast');
                        if ($loadingToast.css('display') !== 'none') return;
                        $loadingToast.fadeIn(100);
                        
                        var formData = new FormData();  
                        formData.append('file', $("#uploaderInput")[0].files[0]);
                        $.ajax({  
                             url: 'http://120.132.8.152:8090/hcapmobjecthubservice/api/apm/objectHub/objects/single' ,  
                             type: 'POST',  
                             data: formData,  
                             async: false,  
                             cache: false,  
                             contentType: false,  
                             processData: false,  
                             headers: {"Authorization":$.cookie('Authorization')},
                             success: function (ret) {  alert(ret.data.objectId);
                                if (ret && ret.bizStatusCode === 'OK' && ret.data) {
                                   send(ret.data.objectId);
                                }
                             },  
                             error: function (returndata) {  
                             }  
                        });  
                        
                        function send(attachId) {
                            var data = {
                                "attachId": attachId,
                                "detailsList": window.inspDetails,
                                "comments": $('#comments').val()
                            };

                            apmRest.put('/hcapmassetservice/api/apm/asset/inspection/'+window.orderId+'/excute', data, function(ret){
                                $loadingToast.fadeOut(100);
                                if (ret && ret.bizStatusCode === 'OK') {
                                    $('#container').empty().append($('#ts_msg_success').html());
                                } else {
                                    $('#container').empty().append($('#ts_msg_failed').html());
                                }
                            });
                        }
                    });
                }
            });
        </script>
        <script type="text/html" id="assetItem">
            <li>
                <div class="weui-flex js_category">
                    <div class="weui-cells weui-cells_checkbox" style="width:100%">
                        <div class="weui-cell weui-check__label">
                            <div class="weui-cell__bd">
                                <p>设备1</p>
                            </div>
                            <div class="weui-cell__ft">
                                <input type="checkbox" class="weui-check" name="checkbox1" checked="checked">
                                <i class="weui-icon-checked item_checked"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="page__category js_categoryInner">
                    <div class="weui-cells page__category-content">
                        <div class="weui-cells weui-cells_checkbox">
                        
                        </div>
                    </div>
                </div>
            </li>
        </script>
        <script type="text/html" id="checkItem">
            <div class="weui-cell weui-check__label submargin">
                <div class="weui-cell__bd">
                    <p>检查1</p>
                </div>
                <div class="weui-cell__ft">
                    <input type="checkbox" class="weui-check" checked="checked">
                    <i class="weui-icon-checked"></i>
                </div>
            </div>
        </script>
        <script type="text/html" id="ts_msg_success">
            <div class="page js_show">
                <div class="weui-msg">
                    <div class="weui-msg__icon-area"><i class="weui-icon-success weui-icon_msg"></i></div>
                    <div class="weui-msg__text-area">
                        <h2 class="weui-msg__title">保存成功</h2>
                    </div>
                    <div class="weui-msg__opr-area">
                        <p class="weui-btn-area">
                            <a href="javascript:history.back();" class="weui-btn weui-btn_primary">返回</a>
                        </p>
                    </div>
                </div>
            </div>
        </script>
        <script type="text/html" id="ts_msg_failed">
            <div class="page js_show">
                <div class="weui-msg">
                    <div class="weui-msg__icon-area"><i class="weui-icon-warn weui-icon_msg"></i></div>
                    <div class="weui-msg__text-area">
                        <h2 class="weui-msg__title">保存失败</h2>
                    </div>
                    <div class="weui-msg__opr-area">
                        <p class="weui-btn-area">
                            <a href="javascript:history.back();" class="weui-btn weui-btn_primary">返回</a>
                        </p>
                    </div>
                </div>
            </div>
        </script>
        <div id="loadingToast" style="display:none;">
            <div class="weui-mask_transparent"></div>
            <div class="weui-toast">
                <i class="weui-loading weui-icon_toast"></i>
                <p class="weui-toast__content">数据保存中...</p>
            </div>
        </div>
        <div id="dialogs">
            <div class="js_dialog" id="iosDialog2" style="display: none;">
                <div class="weui-mask"></div>
                <div class="weui-dialog">
                    <div class="weui-dialog__bd">最多只能上传1张照片</div>
                    <div class="weui-dialog__ft">
                        <a href="javascript:;" class="weui-dialog__btn weui-dialog__btn_primary">知道了</a>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
