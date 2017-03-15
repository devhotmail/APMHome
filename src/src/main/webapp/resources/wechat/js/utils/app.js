/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(function () {
    var app = window.app = {};
    app.WechatImageUploadWidget = WechatImageUploadWidget;
    app.WechatImageUploadListWidget = WechatImageUploadListWidget;
    app.ImageUploadWidget = ImageUploadWidget;
    app.ImageUploadListWidget = ImageUploadListWidget;
    app.VoiceUploadWidget = VoiceUploadWidget;
    app.FormValidator = FormValidator;
    app.BrandCategorySelect = BrandCategorySelect;
    app.LoadingModal = LoadingModal;
    app.SpeakingModal = SpeakingModal;
    app.ScanQRCode = ScanQRCode;

    /////////////////////////

    /**
     *
     * @param $el: container
     * @param name: name and id of the input
     * @param required: true if the upload is required
     */
    function VoiceUploadWidget($el, name, required) {
        var $input = $('<input type="text" class="voice-upload"/>');
        var $button = $('<button type="button" class="btn btn-default voice-upload" />').html('点击 开始说话');
        $el.addClass('voice-upload');
        this.$el = $el;
        this.getData = getData;

        var speakingModal = new SpeakingModal(0);
        var localId = null;
        var playing = false;
        var recording = false;

        initialize();

        ////////////////////////////////////////////

        function initialize() {
            if (required === undefined) {
                required = false;
            }

            // widget instance methods
            $input
                .attr({
                    id: name,
                    name: name,
                    required: required
                });

            // delegate events for input by button
            $button
                .on('click', function () {
                    var self = $(this);
                    if (recording) {
                        speakingModal.stop();
                        speakingFinished();
                        recording = false;
                        wechatSDK.stopRecord(function (res) {
                           localId = res.localId;
                        });
                        self.html('点击 开始说话');
                    } else {
                        recording = true;
                        self.html('点击 结束录音');
                        speakingModal.start();
                        wechatSDK.startRecord(function (res) {
                            speakingModal.stop();
                            localId = res.localId;
                            speakingFinished();
                            recording = false;
                            self.html('点击 开始说话');
                        });
                    }
                });

            $el.empty().append($input, $button);
        }

        function speakingFinished() {
            $input.val(localId);
            $button.hide();
            var $wrap = $('<div>').addClass('voice-upload wrap');
            $('<button type="button">播放</button>')
                .addClass('btn btn-success')
                .on('click', function () {
                    var self = $(this);
                    if (!playing) {
                        wechatSDK.playVoice(localId, function () {
                            playing = false;
                            self.html('播放');
                        });
                        playing = true;
                        self.html('停止');
                    } else {
                        playing = false;
                        self.html('播放');
                        wechatSDK.stopVoice(localId);
                    }
                })
                .appendTo($wrap);
            $('<button type="button">重录</button>')
                .addClass('btn btn-warning')
                .on('click touchstart', function () {
                    if (confirm('重新录制语音?')) {
                        playing = false;
                        wechatSDK.stopVoice(localId);
                        $input.val(null);
                        $el.find('.voice-upload.wrap').remove();
                        $button.show();
                    }
                    return false;
                })
                .appendTo($wrap);
            $wrap.appendTo($el);
        }

        /**
         * get data for POST
         * @returns {{name: *, data: string}}
         */
        function getData() {
            // compress and change input type to string to store data url
            // of the compressed image
            return {
                name: name,
                data: localId
            }
        }
    }

    /**
     *
     * @param $el: container
     * @param name: name and id of the input
     * @param required: true if the upload is required
     */
    function WechatImageUploadWidget($el, name, required, addExtras, maxSlots) {
        var $input = $('<input type="text" class="img-upload"/>');
        var $button = $('<button type="button" class="btn btn-default img-upload" />').html('+');
        $el.addClass('img-upload');
        this.$el = $el;
        this.getData = getData;
        this.attachLocalImage = attachLocalImage;
        this.attachRemoteImage = attachRemoteImage;
        var currentLocalId = null;
        var currentImageId = null;

        initialize();
        ////////////////////////////////////////////

        function initialize() {
            if (required === undefined) {
                required = false;
            }
            // widget instance methods
            // delegate events for input by button
            $button.on('click touchstart', function () {
                var max = 9;
                if (maxSlots) {
                    if ($.isFunction(maxSlots)) {
                        max = maxSlots();
                    } else {
                        max = maxSlots;
                    }
                }
                wechatSDK.chooseImages(max, function (res) {
                    var localIds = res.localIds;
                    if (localIds && localIds.length > 0) {
                        var localId = res.localIds[0];
                        attachLocalImage(localId);
                        localIds.splice(0, 1);
                        addExtras(localIds);
                    }
                });
                return false;
            });

            $el.empty().append($input, $button);
        }

        function attachRemoteImage(imageId, url, onRemove) {
            currentImageId = imageId;
            $button.hide();
            var $wrap = $('<div>').addClass('img-upload outer');
            var $imgWrap = $('<div>').addClass('img-upload wrap').appendTo($wrap);
            $('<img src="' + url + '" data-value="' + imageId + '">')
                .addClass('img-responsive img-upload')
                .appendTo($imgWrap);
            $('<div>')
                .addClass('img-upload delete glyphicon glyphicon-remove')
                .on('click touchstart', function () {
                    if (confirm('删除此图片?')) {
                        $input.val(null);
                        $el
                            .trigger('img:remove', true)
                            .find('.img-upload.outer').remove();
                        $button.show();
                        if (onRemove != null) {
                            onRemove(currentImageId);
                        }
                    }
                    return false;
                })
                .appendTo($wrap);
            $wrap.appendTo($el);
            $el.trigger('img:add', false);
            addExtras([]);
        }

        function attachLocalImage(localId) {
            currentLocalId = localId;
            $input
                .attr({
                    id: name,
                    name: name,
                    required: required
                });
            $input.val(localId);
            $button.hide();
            var $wrap = $('<div>').addClass('img-upload outer');
            var $imgWrap = $('<div>').addClass('img-upload wrap').appendTo($wrap);
            $('<img src="' + localId + '">')
                .addClass('img-responsive img-upload')
                .appendTo($imgWrap);
            $('<div>')
                .addClass('img-upload delete glyphicon glyphicon-remove')
                .on('click touchstart', function () {
                    if (confirm('删除此图片?')) {
                        $input.val(null);
                        $el
                            .trigger('img:remove', true)
                            .find('.img-upload.outer').remove();
                        $button.show();
                    }
                    return false;
                })
                .appendTo($wrap);
            $wrap.appendTo($el);
            $el.trigger('img:add', false);
        }

        /**
         * get data for POST
         * @returns {{name: *, data: string}}
         */
        function getData() {
            // compress and change input type to string to store data url
            // of the compressed image
            return {
                name: name,
                data: currentLocalId,
                imageId: currentImageId
            }
        }
    }

    /**
     *
     * @param $el: container
     * @param name: name and id of the input
     * @param required: true if the upload is required
     * @param maxSize:
     *      maximum pixels of the longer side of the image to be uploaded;
     *      if exceeded, the image is resized to maxSize before uploading
     */
    function ImageUploadWidget($el, name, required, maxSize) {
        var $input = $('<input type="file" accept="image/*;capture=camera" capture="camera" class="img-upload"/>');
        var $button = $('<button type="button" class="btn btn-default img-upload" />').html('+');
        $el.addClass('img-upload');
        this.$el = $el;
        this.getData = getData;
        var imageURL = null;

        initialize();
        ////////////////////////////////////////////

        function initialize() {
            if (required === undefined) {
                required = false;
            }
            // widget instance methods
            $input
                .attr({
                    id: name,
                    name: name,
                    required: required
                })
                .change(function () { // show preview of image to be uploaded
                if (this.files && this.files[0]) {
                    loadImage(this.files[0], function (canvas) {
                        imageURL = canvas.toDataURL('image/jpeg');
                        $button.hide();
                        var $wrap = $('<div>');
                        var $imgWrap = $('<div>').addClass('img-upload wrap');
                        $imgWrap.appendTo($wrap);
                        $(canvas)
                            .addClass('img-responsive img-upload')
                            .appendTo($imgWrap);
                        $('<div>')
                            .addClass('img-upload delete glyphicon glyphicon-remove')
                            .on('click touchstart', function () {
                                if (confirm('删除此图片?')) {
                                    $input.val(null);
                                    $el
                                        .trigger('img:remove', true)
                                        .find('.img-upload.wrap').remove();
                                    $button.show();
                                }
                                return false;
                            })
                            .appendTo($wrap);
                        $wrap.appendTo($el);
                        $el.trigger('img:add', false);
                    }, {
                        maxWidth: maxSize || 1200,
                        maxHeight: maxSize || 1200,
                        canvas: true
                    });
                }
            });

            // delegate events for input by button
            $button.on('click touchstart', function () {
                $input.click();
                return false;
            });

            $el.empty().append($input, $button);
        }

        /**
         * get data for POST
         * @returns {{name: *, data: string}}
         */
        function getData() {
            // compress and change input type to string to store data url
            // of the compressed image
            return {
                name: name,
                data: imageURL
            }
        }
    }

    /**
     *
     * @param $el: container element
     * @param name: name of the input element
     * @param options:
     *      maxImages: maximum number of images to be uploaded
     *      cols: width of each image preview
     *      required: true if the upload is required
     */
    function WechatImageUploadListWidget($el, name, options, onChange) {
        this.$el = $el;
        this.getData = getData;
        this.createWidget = createWidget;
        this.emptyWidget = emptyWidget;
        var $images = $('<div>').addClass('row');
        var widgets = [];
        var defaultOptions = {
            maxImages: 2,
            cols: 6,
            required: false
        };

        var $emptySlot = $('<div class="img-count">');

        initialize();

        ///////////////////////////////////////////
        function initialize () {
            $images.appendTo($el);
            $emptySlot.html('0/' + options.maxImages);
            $emptySlot.appendTo($el);
            if (!options) {
                options = defaultOptions;
            } else {
                for (var k in defaultOptions) {
                    if (defaultOptions.hasOwnProperty(k)) {
                        if (!options[k]) {
                            options[k] = defaultOptions[k];
                        }
                    }
                }
            }
            createWidget();
        }

        /**
         * get data for POST
         * @returns {Array}: each item is in the format:
         *      {name: "input name", data: "image data url"}
         */
        function getData() {
            $el.find('input.img-upload')
                // remove empty inputs
                .filter(function () {
                    return !$(this).val();
                })
                .remove();
            var index = 0;
            $el.find('input.img-upload')
                // append index
                .each(function () {
                    var inputName = name + '-' + index;
                    $(this).attr({
                        name: inputName,
                        id: inputName
                    });
                    index += 1;
                });
            var data = [];
            widgets.forEach(function (widget, index) {
                data.push({
                    name: name + '-' + index,
                    data: widget.getData().data,
                    imageId: widget.getData().imageId
                });
            });
            return data;
        }

        function getCurrentImagesNum() {
            return $el.find('img.img-upload').length;
        }

        function onNumChange() {
            var currentNum = getCurrentImagesNum();
            $emptySlot.html(currentNum + '/' + options.maxImages);
            if (onChange != null) {
                onChange(currentNum);
            }
        }

        function onRemoveImage(e) {
            $(e.target).remove();  // remove widget from DOM
            var currentCount = getCurrentImagesNum();
            if (currentCount == options.maxImages - 1 || currentCount == 0) {
                emptyWidget();
            }
            onNumChange();
        }

        function onAddImage() {

        }

        function onAddExtras(localIds) {
            localIds.forEach(function(localId, index) {
                var left = options.maxImages - getCurrentImagesNum();
                if (left > 0) {
                    var widget = createWidget();
                    widget.attachLocalImage(localId);
                }
            });
            if (getCurrentImagesNum() < options.maxImages) {
                createWidget();
            }
            onNumChange();
        }

        function getLeftSlots() {
            return options.maxImages - getCurrentImagesNum();
        }

        function createWidget() {
            var $widget = $('<div>')
                        .addClass('col-xs-' + options.cols);
            $widget.css('width', 120);
            var widget = new WechatImageUploadWidget(
                $widget, name, options.required, onAddExtras, getLeftSlots);
            widget.$el
                .on('img:add', onAddImage)
                .on('img:remove', function (e) {
                    // remove widget from list
                    var index = widgets.indexOf(widget);
                    if (index > -1) {
                        widgets.splice(index, 1);
                    }
                    onRemoveImage(e);
                })
                .appendTo($images);
            widgets.push(widget);
            return widget;
        }

        function emptyWidget() {
            var emptyWidgets = widgets.filter(function(widget, index) {
                var data = widget.getData();
                return (data == null || (data.data == null && data.imageId == null));
            });
            if (emptyWidgets.length > 0) {
                return emptyWidgets[0];
            }
            return createWidget();
        }
    }


    /**
     *
     * @param $el: container element
     * @param name: name of the input element
     * @param options:
     *      maxImages: maximum number of images to be uploaded
     *      cols: width of each image preview
     *      required: true if the upload is required
     */
    function ImageUploadListWidget($el, name, options, onChange) {
        this.$el = $el;
        this.getData = getData;
        this.createWidget = createWidget;
        var $images = $('<div>').addClass('row');
        var widgets = [];
        var defaultOptions = {
            maxImages: 2,
            cols: 6,
            required: false,
            maxSize: 800
        };
        var $emptySlot = $('<div style="float: right;">');

        initialize();

        ///////////////////////////////////////////
        function initialize () {
            $images.appendTo($el);
            $emptySlot.html('0/' + options.maxImages);
            $emptySlot.appendTo($el);
            if (!options) {
                options = defaultOptions;
            } else {
                for (var k in defaultOptions) {
                    if (defaultOptions.hasOwnProperty(k)) {
                        if (!options[k]) {
                            options[k] = defaultOptions[k];
                        }
                    }
                }
            }
            createWidget();
        }

        /**
         * get data for POST
         * @returns {Array}: each item is in the format:
         *      {name: "input name", data: "image data url"}
         */
        function getData() {
            $el.find('input.img-upload')
                // remove empty inputs
                .filter(function () {
                    return !$(this).val();
                })
                .remove();
            var index = 0;
            $el.find('input.img-upload')
                // append index
                .each(function () {
                    var inputName = name + '-' + index;
                    $(this).attr({
                        name: inputName,
                        id: inputName
                    });
                    index += 1;
                });
            var data = [];
            widgets.forEach(function (widget, index) {
                data.push({
                    name: name + '-' + index,
                    data: widget.getData().data
                });
            });
            return data;
        }

        function getCurrentImagesNum() {
            return $el.find('img.img-upload').length;
        }

        function onNumChange() {
            var currentNum = getCurrentImagesNum();
            $emptySlot.html(currentNum + '/' + options.maxImages);
            if (onChange != null) {
                onChange(currentNum);
            }
        }

        function onRemoveImage(e) {
            $(e.target).remove();  // remove widget from DOM
            if (getCurrentImagesNum() == options.maxImages - 1) {
                createWidget();
            }
            onNumChange();
        }

        function onAddImage() {
            if (getCurrentImagesNum() < options.maxImages) {
                createWidget();
            }
            onNumChange();
        }

        function createWidget() {
            var $widget = $('<div>')
                        .addClass('col-xs-' + options.cols);
            $widget.css('width', 120);
            var widget = new ImageUploadWidget(
                $widget, name, options.required, options.maxSize);
            widget.$el
                .on('img:add', onAddImage)
                .on('img:remove', function (e) {
                    // remove widget from list
                    var index = widgets.indexOf(widget);
                    if (index > -1) {
                        widgets.splice(index, 1);
                    }
                    onRemoveImage(e);
                })
                .appendTo($images);
            widgets.push(widget);
        }
    }

    /**
     * html5 from validation supporting safari.
     *
     * usage:
     *
     * define input validator in html5 fashion:
     *
     *     <input name="username" required>
     *
     * invoke form validation:
     *
     *     new FormValidation($("#form"));
     *
     * validation is automatically performed upon the "submit" event
     * if there are invalid inputs, the error is `alert`ed; otherwise the
     * `submit` event is propagated.
     *
     * @param $form: a $selector to a form
     */
    function FormValidator ($form) {
        this.validate = validate;
        // disable html5 validation since they're not supported no safari
        $form.attr('novalidate', true);

        var methods = {
            required: function (value) {
                if (!value) {
                    throw '必填';
                }
            },
            digits: function (value) {
                if (value && !value.match(/^[0-9]*$/)) {
                    throw '必须全部为数字';
                }
            },
            length: function (value, length) {
                length = parseInt(length);
                if (!!value && value.length != length) {
                    throw '长度必须等于' + length;
                }
            },
            phone: function(value) {
                if (value && !value.match(/^1[0-9]{10}$/)) {
                    throw '请输入真实的手机号码'
                }
            }
        };

        function validate() {
            var valid = true;
            $form.find('input').each(function (i, elem) {
                var $elem = $(elem),
                    value = $elem.val();
                $.each(methods, function (rule, method) {
                    // rule defined
                    var param = $elem.attr(rule);
                    if (param !== undefined) {
                        try {
                            method(value, param);
                        }
                        catch (e) {
                            // find label for the input
                            var label = $form
                                .find('label[for=' + $elem.attr('name') + ']')
                                .text()
                                .trim();
                            alert(label + ': ' + e);
                            valid = false;
                            return false;
                        }
                    }
                });
                return valid;  // if already invalid, break validation loop
            });
            // if valid then do not preventDefault,
            // i.e., submit form
            return valid;
        }
    }

    /**
     * displays categories according to selected brand
     *
     * @param category_map: brand_id -> list of (category_id, category_name)
     * @param $brand: a select for brands
     * @param $category: a select for categories
     * @param resetEmpty: if true, set category value to '' after brand change
     */
    function BrandCategorySelect(category_map, $brand, $category, resetEmpty, nullable, emptyOption, categoryDefault) {
        var initialized = false;
        populateCategories();
        $brand.change(populateCategories);

        function populateCategories() {
            var brand_id = $brand.val();
            if (brand_id === null) {
                return;
            }
            var $temp = $('<div>');
            if (nullable && emptyOption) {
                $temp
                    .append($('<option/>')
                    .attr('value', '__None')
                    .html(''));
            }
            var current_category = $category.val();
            var has_current = false;
            if (category_map[brand_id]) {
               $.each(category_map[brand_id], function (i, elem) {
                    $temp
                        .append($('<option/>')
                        .attr('value', elem.id)
                        .html(elem.name));
                   if (elem.id == current_category && current_category != null) {
                       has_current = true;
                   }
                });
            }
            $category
                .empty()
                .append($temp.children());
            if (current_category) {
                $category.val(current_category);
            } else if (!nullable) {
                $category.val($category.children().first().val());
            }
            if (categoryDefault != null) {
                $category.val(categoryDefault);
            }
            if (initialized && !has_current) {
                // update select2 widget
                var reset = '';
                if (!resetEmpty) {
                    reset = $category.children().first().val()
                }
                $category.val(reset);
            }
            initialized = true;
            $category.trigger('change');
        }
    }

    function SpeakingModal() {
        this.start = start;
        this.stop = stop;
        var $el = $('<div class="ab speaking-modal" id="speaking-modal">' +
                    '<p class="text-center">' +
                         '正在录音... ' +
                         '<span>0</span> 秒' +
                    '</p>' +
                    '</div>');
        var timer = null;
        $el.hide();
        $('body').append($el);

        function start(seconds) {
            seconds = Math.max(seconds || 0, 0);
            $el.show();
            timer = setInterval(function () {
                seconds++;
                $el.find('span').html(seconds);
            }, 1000);
        }

        function stop() {
            clearInterval(timer);
            $el.hide();
            $el.find('span').html(0);
        }
    }

    function LoadingModal(message) {
        this.show = show;
        this.hide = hide;
        var $el = $('<div class="modal fade" id="loading-modal" data-backdrop="static" data-keyboard="false">' +
                        '<div class="modal-dialog">' +
                            '<div class="modal-content">' +
                                '<div class="modal-body">' +
                                    '<p class="text-center"></p>' +
                                    '<div class="progress">' +
                                        '<div class="progress-bar progress-bar-striped active" style="width: 100%;" role="progressbar"></div>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                    '</div>');
        $el.find('p').html(message);

        function show(onShown) {
            $el
                .on('shown.bs.modal', onShown)
                .modal('show');
        }

        function hide(onHidden) {
            $el
                .on('hidden.bs.modal', onHidden)
                .modal('hide');
        }
    }

    function checkEquipCode(equip_code, cb) {
        equip_code = encodeURIComponent(equip_code);
        var url = '/h5/equipments/code_check?code=' + equip_code;
        $.get(url, function(result) {
            // not deal with error,
            // will catch by real submit after scan
            if (result.info != 'pass') {
                alert(result.info);
            } else {
                cb(result.code, result.code_type);
            }
        });
    }

    function handleScanResult(url, cb, skip_check) {
        var equip_code;
        if (parseInt(url)) {
            equip_code = parseInt(url);
        } else {
            var parser = document.createElement('a');
            parser.href = url;
            var path = parser.pathname;
            equip_code = path.substr(path.lastIndexOf('/') + 1);
        }
        if (skip_check) {
            var parts = url.split('\n');
            if (parts.length > 1) {
                var code_line = parts[1];
                var code = code_line.split('：');
                if (code.length > 1) {
                    cb(code[1], 1);
                }
            }
            cb(equip_code, 1);
        } else
            checkEquipCode(url, cb);
    }

});

