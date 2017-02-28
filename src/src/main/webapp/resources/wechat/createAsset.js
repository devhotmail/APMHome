/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(function () {
    var tmpl = '<li class="weui-uploader__file" style="background-image:url(#url#)" title="#name#" ></li>',
            $gallery = $("#gallery"), $galleryImg = $("#galleryImg"),
            $uploaderInput = $("#assetForm\\:uploaderInput"),
            $uploaderFiles = $("#uploaderFiles");
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

    $uploaderInput.on("change", function (e) {
        var file = $uploaderInput[0].files['0'];
        var Orientation = null;
        if ($uploaderInput[0].files && file) {
            var rFilter = /^(image\/jpeg|image\/png)$/i; // 检查图片格式
            if (!rFilter.test(file.type)) {
                return;
            }
            EXIF.getData(file, function () {
                EXIF.getAllTags(this);
                Orientation = EXIF.getTag(this, 'Orientation');
            });
            var reader = new FileReader();
            reader.onload = function (evt) {
                var image = new Image();
                image.src = evt.target.result;
                image.onload = function () {
                    var expectWidth = this.naturalWidth;
                    var expectHeight = this.naturalHeight;
                    var canvas = document.createElement("canvas");
                    var ctx = canvas.getContext("2d");
                    canvas.width = expectWidth;
                    canvas.height = expectHeight;
                    ctx.drawImage(this, 0, 0, expectWidth, expectHeight);
                    var base64 = null;
                    var mpImg = new MegaPixImage(image);
                    mpImg.render(canvas, {
                        maxWidth: 800,
                        maxHeight: 1200,
                        quality: 0.8,
                        orientation: Orientation
                    });
                    base64 = canvas.toDataURL("image/jpeg", 0.8);
                    var ins = tmpl.replace('#name#', file.name);
                    $("#uploaderFiles").append($(ins.replace('#url#', base64)));
                };
            };
            reader.readAsDataURL($uploaderInput[0].files[0]);
        }

    });
    
    
});





