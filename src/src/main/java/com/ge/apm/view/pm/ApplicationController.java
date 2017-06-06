package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;

import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.asset.AssetInfoController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class ApplicationController extends JpaCRUDController<PurchaseApplication> {

    private static final long serialVersionUID = 1L;

    private PurchaseApplicationRepository dao = null;
    private String operation;
    private List<SelectItem> cars;
    private String[] selectedCars;




    private String needCost;

    @Override
    protected void init() {
        this.filterBySite=false;
        dao=WebUtil.getBean(PurchaseApplicationRepository.class);
        cars = new ArrayList<SelectItem>();
        SelectItemGroup germanCars = new SelectItemGroup("拟用方向:");
        germanCars.setSelectItems(new SelectItem[] {
                new SelectItem("lingchuangbibei", "临床必备"),
                new SelectItem("xuekefazhan", "学科发展"),
                new SelectItem("jiaoxuekeyan", "教学科研")
        });
        cars.add(germanCars);


        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }else if ("Edit".equalsIgnoreCase(actionName)) {

           setSelectedByUrlParam(encodeStr, "selectedid");
            prepareEdit();
        } else if ("Delete".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
          //setSelectedByUrlParam(encodeStr, "selectedid");
            prepareDelete();
        }



    }

    protected void setSelectedByUrlParam(String encodeUrl, String paramName) {
        setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeUrl, paramName)));
    }

    @Transactional
    public void applyChange() {
        this.save();
    }


    @Override
    protected String getActionMessage(String actionName) {
        return WebUtil.getMessage("标签");
    }

    @Override
    protected PurchaseApplicationRepository getDAO() {
        return dao;
    }
    String url;
    public String getViewPage(String pageName, String actionName) throws IOException {
         url = "actionName=Edit&selectedid="
                + selected.getId().toString();
        url = "application.xhtml?str="+ UrlParamUtil.encodeUrlQueryString(url);
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    return url;
    }

    public List<SelectItem> getCars() {
        return cars;
    }

    public void setCars(List<SelectItem> cars) {
        this.cars = cars;
    }

    public String[] getSelectedCars() {
        return selectedCars;
    }

 /*   public List<String> getFundingResource() {
        return fundingResource;
    }

    public void setFundingResource(List<String> fundingResource) {
        this.fundingResource = fundingResource;
    }*/

    public void setSelectedCars(String[] selectedCars) {
        this.selectedCars = selectedCars;
    }



    public String getNeedCost() {
        return needCost;
    }

    public void setNeedCost(String needCost) {
        this.needCost = needCost;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}