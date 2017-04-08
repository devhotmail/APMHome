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
        button1.setName("我的报修");
        WxMenuButton button11 = new WxMenuButton();
        button11.setType(WxConsts.BUTTON_VIEW);
        button11.setName("扫码报修");
        button11.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/21", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button12 = new WxMenuButton();
        button12.setType(WxConsts.BUTTON_VIEW);
        button12.setName("报修状态");
        button12.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/22", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));        
        button1.getSubButtons().add(button11);
        button1.getSubButtons().add(button12);

        WxMenuButton button2 = new WxMenuButton();
        button2.setName("我的工单");
        
//        WxMenuButton button23 = new WxMenuButton();
//        button23.setType(WxConsts.BUTTON_VIEW);
//        button23.setName("工单处理");
//        button23.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/34", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button21 = new WxMenuButton();
        button21.setType(WxConsts.BUTTON_VIEW);
        button21.setName("报修工单");
        button21.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/35", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button22 = new WxMenuButton();
        button22.setType(WxConsts.BUTTON_VIEW);
        button22.setName("巡检工单");
        button22.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/25", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button23 = new WxMenuButton();
        button23.setType(WxConsts.BUTTON_VIEW);
        button23.setName("质控工单");
        button23.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/26", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button24 = new WxMenuButton();
        button24.setType(WxConsts.BUTTON_VIEW);
        button24.setName("计量工单");
        button24.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/27", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button25 = new WxMenuButton();
        button25.setType(WxConsts.BUTTON_VIEW);
        button25.setName("保养工单");
        button25.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/28", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        
        button2.getSubButtons().add(button21);
        button2.getSubButtons().add(button22);
        button2.getSubButtons().add(button23);
        button2.getSubButtons().add(button24);
        button2.getSubButtons().add(button25);
        
        WxMenuButton button3 = new WxMenuButton();
        button3.setName("其他");
        WxMenuButton button31 = new WxMenuButton();
        button31.setType(WxConsts.BUTTON_VIEW);
        button31.setName("扫码建档");
        button31.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/11", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button32 = new WxMenuButton();
        button32.setType(WxConsts.BUTTON_VIEW);
        button32.setName("扫码查档");
        button32.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/12", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button33 = new WxMenuButton();
        button33.setType(WxConsts.BUTTON_VIEW);
        button33.setName("检索档案");
        button33.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/13", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button34 = new WxMenuButton();
        button34.setType(WxConsts.BUTTON_VIEW);
        button34.setName("用户绑定");
        button34.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName+"/web/menu/33", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        WxMenuButton button35 = new WxMenuButton();
        button35.setType(WxConsts.BUTTON_VIEW);
        button35.setName("设备盘点");
        button35.setUrl(wxMpService.oauth2buildAuthorizationUrl(serverName + "/web/menu/29", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        
        button3.getSubButtons().add(button31);
        button3.getSubButtons().add(button32);
        button3.getSubButtons().add(button33);
        button3.getSubButtons().add(button35);
        button3.getSubButtons().add(button34);
        
        menu.getButtons().add(button1);
        menu.getButtons().add(button2);
        menu.getButtons().add(button3);

        return menu;
    }

}
