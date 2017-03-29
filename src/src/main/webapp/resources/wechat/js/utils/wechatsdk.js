/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


(function () {
    var wechatSDK = {
        config: {
            debug: false, 
            appId: '', 
            timestamp: '', 
            nonceStr: '', 
            signature: '',
            jsApiList: [
                'scanQRCode',
                'chooseImage',
                'uploadImage',
                'downloadImage',
                'previewImage',
                'startRecord',
                'stopRecord',
                'onVoiceRecordEnd',
                'playVoice',
                'onVoicePlayEnd',
                'pauseVoice',
                'stopVoice',
                'uploadVoice',
                'downloadVoice',
                'closeWindow'
            ]
        },

        init: function () {
            wx.config(wechatSDK.config);
        },

        setAppId: function (appid) {
            wechatSDK.config.appId = appid ? appid : '';
        },

        setSignature: function (timestamp, nonceStr, signature) {
            wechatSDK.config.timestamp = timestamp ? timestamp : '';
            wechatSDK.config.nonceStr = nonceStr ? nonceStr : '';
            wechatSDK.config.signature = signature ? signature : '';
        },

        setDebugMode: function (enable_debug_mode) {
            wechatSDK.config.debug = enable_debug_mode === true;
        },

        scanQRCode: function (needResult, callback) {
            wx.ready(function(){
                wx.scanQRCode({
                    needResult: needResult === 0 ? 0 : 1,
                    scanType: ["qrCode", "barCode"],
                    success: function (res) {
                        if (callback !== undefined) {
                            setTimeout(function() {
                                callback(res.resultStr);
                            }, 100);
                        }
                    }
                });
            });
        },

        chooseImages: function (maxCount, onSuccess, onFailed) {
            wx.chooseImage({
                count: maxCount>9?9:maxCount,
                sizeType: ['compressed'],
                sourceType: ['album', 'camera'],
                success: function (res) {
                    if (onSuccess !== undefined) {
                        onSuccess(res.localIds);
                    }
                },
                cancel: function (res) {
                    if (onFailed !== undefined) {
                        onFailed(res.localIds);
                    }
                },
                fail: function (res) {
                    if (onFailed !== undefined) {
                        onFailed(res.localIds);
                    }
                }
            });
        },

        previewImages: function (URLs, current) {
            if (URLs && URLs.length > 0) {
                wx.previewImage({
                    current: current || URLs[0],
                    urls: URLs
                });
            }
        },

        uploadImages: function (localIds, onCompleted) {
            if (!(localIds && localIds.length > 0)) {
                onCompleted([], [], {});
                return;
            }

            var i = 0,
                length = localIds.length,
                serverIds = [],
                result = {},
                failedLocalIds = [];
            function upload() {
                wx.uploadImage({
                    localId: localIds[i],
                    isShowProgressTips: 1,
                    success: function (res) {
                        result[localIds[i]] = res.serverId;
                        i++;
                        serverIds.push(res.serverId);
                        if (i < length) {
                            upload();
                        } else {
                            if (onCompleted !== undefined) {
                                onCompleted(serverIds, failedLocalIds, result);
                            }
                        }
                    },
                    fail: function (res) {
                        i++;
                        failedLocalIds.push(localIds[i]);
                        if (i < length) {
                            upload();
                        } else {
                            if (onCompleted !== undefined) {
                                onCompleted(serverIds, failedLocalIds, result);
                            }
                        }
                    }
                });
            }
            upload();
        },

        downloadImages: function (serverIds) {
            if (!(serverIds && serverIds.length > 0)) {
                onCompleted([], []);
                return;
            }
            var i = 0,
                length = serverIds.length,
                localIds = [],
                failedServerIds = [];
            function download() {
                wx.downloadImage({
                    serverId: serverIds[i],
                    success: function (res) {
                        i++;
                        localIds.push(res.localId);
                        if (i < length) {
                            download();
                        } else {
                            if (onCompleted !== undefined) {
                                onCompleted(localIds, failedServerIds);
                            }
                        }
                    },
                    failed: function (res) {
                        i++;
                        failedServerIds.push(serverIds[i]);
                        if (i < length) {
                            download();
                        } else {
                            if (onCompleted !== undefined) {
                                onCompleted(localIds, failedServerIds);
                            }
                        }
                    }
                });
            }
            download();
        },

        startRecord: function (onSuccess, onFailed) {
            wx.startRecord({
                cancel: function (res) {
                    if (onFailed !== undefined) {
                        onFailed(res);
                    }
                },
                fail: function (res) {
                    if (onFailed !== undefined) {
                        onFailed(res);
                    }
                }
            });
            wx.onVoiceRecordEnd({
                complete: function (res) {
                    if (onSuccess !== undefined) {
                        onSuccess(res);
                    }
                }
            });
        },

        stopRecord: function (onSuccess, onFailed) {
            wx.stopRecord({
                success: function (res) {
                    if (onSuccess !== undefined) {
                        onSuccess(res);
                    }
                },
                cancel: function (res) {
                    if (onFailed !== undefined) {
                        onFailed(res);
                    }
                },
                fail: function (res) {
                    if (onFailed !== undefined) {
                        onFailed(res);
                    }
                }
            });
        },

        playVoice: function (localId, onSuccess) {
            wx.playVoice({
                localId: localId
            });
            wx.onVoicePlayEnd({
                success: function (res) {
                    if (onSuccess !== undefined) {
                        onSuccess(res);
                    }
                }
            });
        },

        pauseVoice: function (localId) {
            wx.pauseVoice({
                localId: localId
            });
        },

        stopVoice: function (localId) {
            wx.stopVoice({
                localId: localId
            });
        },

        uploadVoice: function (localId, onSuccess, onFailed) {
            wx.uploadVoice({
                localId: localId,
                isShowProgressTips: 1,
                success: function (res) {
                    if (onSuccess !== undefined) {
                        onSuccess(res.serverId);
                    }
                },
                fail: function (res) {
                    if (onFailed !== undefined) {
                        onFailed(res);
                    }
                }
            });
        },

        downloadVoice: function (serverId, onSuccess, onFailed) {
            wx.downloadVoice({
                serverId: serverId,
                isShowProgressTips: 1,
                success: function (res) {
                    if (onSuccess !== undefined) {
                        onSuccess(res.localId);
                    }
                },
                fail: function (res) {
                    if (onFailed !== undefined) {
                        onFailed(res);
                    }
                }
            });
        }

    };

    window.wechatSDK = wechatSDK;
})();
