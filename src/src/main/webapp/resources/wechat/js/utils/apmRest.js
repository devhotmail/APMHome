/**
 * authenticalteUrl : http://localhost:8090
 * weChatId: deefsfiosejefose
 * 
 * these two properties must add to cookie
 * @returns {undefined}
 */
$(function () {
    var apmRest = window.apmRest = {};
    apmRest.login = login;
    apmRest.get = get;
    apmRest.post = post;
    apmRest.put = put;
    apmRest.del = del;
    
    function login(callback, failed) {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: $.cookie('authenticalteUrl')+"/api/apm/security/userAccounts/authenticateWeChat",
            data: JSON.stringify({"weChatId": $.cookie('weChatId')}),
            success:function(ret) {
                if (ret && ret.data && ret.data.id_token){
                    $.cookie('Authorization', ret.data.id_token);
                    $.cookie('loginId', ret.data.loginId);
                }
                if (callback) {
                    callback(ret);
                }
            },
            error: function(ret){
                if(failed) {
                    failed(ret);
                }
            }
        });
    }
    
    function get(url, data, callback){
        var i = 0;
        function doGet() {
            i++;
            $.ajax({
                type: "GET",
                contentType: "application/json",
                url: $.cookie('authenticalteUrl')+url,
                data: data,
                headers: {"Authorization":$.cookie('Authorization')},
                success:function(ret) {
                    if (callback){
                        callback(ret);
                    }
                },
                complete: function(ret) {
                    if(ret.readyState === 0 && i === 1) {
                        //loing again
                        login(function(){
                            doGet(url, data, callback);
                        });
                    }
                }
            });
        }
        doGet();
    }

    function post(url, data, callback) {
        var i = 0;
        function doPost() {
            i++;
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: $.cookie('authenticalteUrl')+url,
                data: JSON.stringify(data),
                headers: {"Authorization":$.cookie('Authorization')},
                success:function(ret) {
                    if (callback){
                        callback(ret);
                    }
                },
                complete: function(ret) {
                    if(ret.readyState === 0 && i === 1) {
                        //loing again
                        login(function(){
                            doPost(url, data, callback);
                        });
                    }
                }
            });
        }
        doPost();
    }
    
    function put(url, data, callback) {
        var i = 0;
        function doPut() {
            i++;
            $.ajax({
                type: "PUT",
                contentType: "application/json",
                url: $.cookie('authenticalteUrl')+url,
                data: JSON.stringify(data),
                headers: {"Authorization":$.cookie('Authorization')},
                success:function(ret) {
                    if (callback){
                        callback(ret);
                    }
                },
                complete: function(ret) {
                    if(ret.readyState === 0 && i === 1) {
                        //loing again
                        login(function(){
                            doPut(url, data, callback);
                        });
                    }
                }
            });
        }
        doPut();
    }
    
    //delete
    function del(url, data, callback) {
        var i = 0;
        function doDel() {
            i++;
            $.ajax({
                type: "DELETE",
                contentType: "application/json",
                url: $.cookie('authenticalteUrl')+url,
                data: JSON.stringify(data),
                headers: {"Authorization":$.cookie('Authorization')},
                success:function(ret) {
                    if (callback){
                        callback(ret);
                    }
                },
                complete: function(ret) {
                    if(ret.readyState === 0 && i === 1) {
                        //loing again
                        login(function(){
                            doDel(url, data, callback);
                        });
                    }
                }
            });
        }
        doDel();
    }
    
});