/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wechat;

import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.view.wechat.config.MenuConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import me.chanjar.weixin.mp.api.WxMpService;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

/**
 *
 * @author 212595360
 */
@ManagedBean
@ViewScoped
public class WeChatController extends JpaCRUDController<WorkOrder>{
    
    WorkOrderRepository dao = null;
    WxMpService wxMpService = null;
    String domianUrl;
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(WorkOrderRepository.class);
        wxMpService = WebUtil.getBean(WxMpService.class);
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String serverName = request.getRequestURL().toString();
        domianUrl = serverName.replace("portal/wechat/createMenu.xhtml", "");
    }
    
    public void createMenu() {
        try {
            wxMpService.getMenuService().menuCreate(MenuConfig.getMenu(wxMpService, domianUrl));
        } catch (Exception ex) {
            WebUtil.addSuccessMessage("菜单创建失败。");
            Logger.getLogger(WeChatController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        WebUtil.addSuccessMessage("菜单创建成功。");
    }

    @Override
    protected WorkOrderRepository getDAO() {
        return dao;
    }

    public String getDomianUrl() {
        return domianUrl;
    }

    public void setDomianUrl(String domianUrl) {
        this.domianUrl = domianUrl;
    }

}
