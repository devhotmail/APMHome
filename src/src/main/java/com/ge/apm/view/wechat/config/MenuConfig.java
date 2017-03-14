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
        button1.setName("设备档案");
//        button1.setUrl(wxMpService.oauth2buildAuthorizationUrl("http://danny.ittun.com/weixin-java-tools-springmvc/authurl", 
//                WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button11 = new WxMenuButton();
        button11.setType(WxConsts.BUTTON_VIEW);
        button11.setName("档案查看");
        button11.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/11", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button12 = new WxMenuButton();
        button12.setType(WxConsts.BUTTON_VIEW);
        button12.setName("扫码建档");
        button12.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/12", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        
        button1.getSubButtons().add(button11);
        button1.getSubButtons().add(button12);

        WxMenuButton button2 = new WxMenuButton();
        button2.setName("我的报修");
        WxMenuButton button21 = new WxMenuButton();
        button21.setType(WxConsts.BUTTON_VIEW);
        button21.setName("扫码报修");
        button21.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/21", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button22 = new WxMenuButton();
        button22.setType(WxConsts.BUTTON_VIEW);
        button22.setName("报修处理进度");
        button22.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/23", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        button2.getSubButtons().add(button21);
        button2.getSubButtons().add(button22);
        
        WxMenuButton button3 = new WxMenuButton();
        button3.setName("我的工单");
        WxMenuButton button31 = new WxMenuButton();
        button31.setType(WxConsts.BUTTON_VIEW);
        button31.setName("工单管理");
        button31.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/24", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        
//        WxMenuButton button32 = new WxMenuButton();
//        button32.setName("更多操作...");
//        WxMenuButton button321 = new WxMenuButton();
//        button321.setType(WxConsts.BUTTON_VIEW);
//        button321.setName("APM帐号信息");
//        button321.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/31", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
//        WxMenuButton button322 = new WxMenuButton();
//        button322.setType(WxConsts.BUTTON_VIEW);
//        button322.setName("APM重置密码");
//        button322.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName+"/web/menu/322", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
//        WxMenuButton button323 = new WxMenuButton();
//        button323.setType(WxConsts.BUTTON_VIEW);
//        button323.setName("APM帐号绑定");
//        button323.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName+"/web/menu/323", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
//        button32.getSubButtons().add(button321);
//        button32.getSubButtons().add(button322);
//        button32.getSubButtons().add(button323);
        
        button3.getSubButtons().add(button31);
//        button3.getSubButtons().add(button32);

        menu.getButtons().add(button1);
        menu.getButtons().add(button2);
        menu.getButtons().add(button3);

        return menu;
    }

}
