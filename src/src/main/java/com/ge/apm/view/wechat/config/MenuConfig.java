package com.ge.apm.view.wechat.config;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.mp.api.WxMpService;

public class MenuConfig {

    /**
     * 定义菜单结构
     *
     * @return
     */
    public static WxMenu getMenu(WxMpService wxMpService, String serverName) {
        if (serverName.endsWith("/")) {
            serverName = serverName.substring(0, serverName.length()-1);
        }
        WxMenu menu = new WxMenu();
        WxMenuButton button1 = new WxMenuButton();
        button1.setName("设备信息");
//        button1.setUrl(wxMpService.oauth2buildAuthorizationUrl("http://danny.ittun.com/weixin-java-tools-springmvc/authurl", 
//                WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button11 = new WxMenuButton();
        button11.setType(WxConsts.BUTTON_VIEW);
        button11.setName("查看设备信息");
        button11.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/wechat/asset/viewAssetInfo.xhtml", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button12 = new WxMenuButton();
        button12.setType(WxConsts.BUTTON_VIEW);
        button12.setName("新增设备");
        button12.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/wechat/asset/create.xhtml?str=YWN0aW9uTmFtZT1DcmVhdGU%3D", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        button1.getSubButtons().add(button11);
        button1.getSubButtons().add(button12);

        WxMenuButton button2 = new WxMenuButton();
        button2.setName("维修流程");
        WxMenuButton button21 = new WxMenuButton();
        button21.setType(WxConsts.BUTTON_VIEW);
        button21.setName("新增报修");
        button21.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/wocreate", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button22 = new WxMenuButton();
        button22.setType(WxConsts.BUTTON_VIEW);
        button22.setName("报修处理进度");
        button22.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/wechat/wo/process.xhtml", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        button2.getSubButtons().add(button21);
        button2.getSubButtons().add(button22);

        WxMenuButton button3 = new WxMenuButton();
        button3.setName("我的帐号");
        WxMenuButton button31 = new WxMenuButton();
        button31.setType(WxConsts.BUTTON_VIEW);
        button31.setName("APM帐号信息");
        button31.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/wechat/uaa/viewUserAccount.xhtml", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button32 = new WxMenuButton();
        button32.setType(WxConsts.BUTTON_VIEW);
        button32.setName("APM重置密码");
        button32.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName+"/wechat/uaa/resetAccountPassword.xhtml", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button33 = new WxMenuButton();
        button33.setType(WxConsts.BUTTON_VIEW);
        button33.setName("APM帐号绑定");
        button33.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName+"/web/authurl", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        button3.getSubButtons().add(button31);
        button3.getSubButtons().add(button32);
        button3.getSubButtons().add(button33);

        menu.getButtons().add(button1);
        menu.getButtons().add(button2);
        menu.getButtons().add(button3);

        return menu;
    }

}
