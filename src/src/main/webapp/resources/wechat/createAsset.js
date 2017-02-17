/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(function () {

    var tmpl = '<li class="weui-uploader__file" style="background-image:url(#url#)"></li>',
            $gallery = $("#gallery"), $galleryImg = $("#galleryImg"),
            $uploaderInput = $("#assetForm\\:uploaderInput"),
            $uploaderFiles = $("#uploaderFiles");
//    $uploaderInput.on("click", function (e) {
//        upload();
//        
//        return false;
//    });
    $uploaderFiles.on("click", "li", function () {
        $galleryImg.attr("style", this.getAttribute("style"));
        $gallery.fadeIn(100);
    });
    $gallery.on("click", function () {
        $gallery.fadeOut(100);
    });
    $('.weui-gallery__del').on('click', function () {
        $uploaderFiles.children().remove();
    });
    $uploaderInput.on("click", function (e) {
        wx.chooseImage({
            count: 1, // 默认9
            sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
            sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
            success: function (res) {
                var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                $uploaderFiles.append($(tmpl.replace('#url#', localIds)));
                wx.uploadImage({
                    localId: localIds[0], // 需要上传的图片的本地ID，由chooseImage接口获得
                    isShowProgressTips: 1, // 默认为1，显示进度提示
                    success: function (res) {
                        var serverId = res.serverId; // 返回图片的服务器端ID
                        $('#assetForm\\:uploaderFileId').val(serverId);
                    }
                });
            }
        });
        return false;
    });

//    $uploaderInput.on("change", function (e) {
//        if ($uploaderInput[0].files && $uploaderInput[0].files[0])
//        {
//            var img = document.createElement("img");
//            img.onload = function () {
//                var rect = clacImgZoomParam(100, 100, img.offsetWidth, img.offsetHeight);
//                img.width = rect.width;
//                img.height = rect.height;
//                img.style.marginTop = rect.top + 'px';
//            };
//            var reader = new FileReader();
//            reader.onload = function (evt) {
//                img.src = evt.target.result;
//                $("#uploaderFiles").append($(tmpl.replace('#url#', img.src)));
////                img = jic.compress(img,50,$uploaderInput[0].files[0].type);
////                dealImage(img.src, {
////                    width: 200
////                }, function (base) {
//////                    alert(base.length);
////                    var img = new Image();
////                   img.src = base;
////                    $uploaderInput[0].files[0] = img;
////                    console.log("压缩后：" + base.length / 1024 + "  " + base);
////
////                })
//            };
//            reader.readAsDataURL($uploaderInput[0].files[0]);
//        }
//
//        $uploaderInput[0].files[0] = img;
////         alert($uploaderInput[0].files[0].size);
//        return true;
//    });

//    var jic = {
//        compress: function (source_img_obj, quality, output_format) {
//            var mime_type = output_format; //"image/jpeg";
//            var cvs = document.createElement('canvas');
//            cvs.width = source_img_obj.naturalWidth;
//            cvs.height = source_img_obj.naturalHeight;
//            var ctx = cvs.getContext("2d").drawImage(source_img_obj, 0, 0);
//            var newImageData = cvs.toDataURL(mime_type, quality / 1000);
//            var result_image_obj = new Image();
//            result_image_obj.src = newImageData;
//            return result_image_obj;
//        }
//    }

//    function dealImage(path, obj, callback) {
//          var img = new Image();
//          img.src = path;
//          img.onload = function () {
//                var that = this;
//                // 默认按比例压缩
//                var w = that.width,
//                          h = that.height,
//                          scale = w / h;
//                  w = obj.width || w;
//                  h = obj.height || (w / scale);
//                var quality = 0.7;    // 默认图片质量为0.7
//                //生成canvas
//                var canvas = document.createElement('canvas');
//                var ctx = canvas.getContext('2d');
//                // 创建属性节点
//                var anw = document.createAttribute("width");
//                anw.nodeValue = w;
//                var anh = document.createAttribute("height");
//                anh.nodeValue = h;
//                canvas.setAttributeNode(anw);
//                canvas.setAttributeNode(anh);
//                ctx.drawImage(that, 0, 0, w, h);
//                // 图像质量
//                if (obj.quality && obj.quality <= 1 && obj.quality > 0) {
//                      quality = obj.quality;
//                }
//                // quality值越小，所绘制出的图像越模糊
//                var base64 = canvas.toDataURL('image/jpeg', quality);
//                // 回调函数返回base64的值
//                callback(base64);
//          }
//    }

});




