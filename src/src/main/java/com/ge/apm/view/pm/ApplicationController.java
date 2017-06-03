package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;

import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;

@ManagedBean
@ViewScoped
public class ApplicationController extends JpaCRUDController<PurchaseApplication> {

    private static final long serialVersionUID = 1L;

    private PurchaseApplicationRepository dao = null;
private Date date1;
private String shenqingkeshi;
    private List<SelectItem> cars;
    private String[] selectedCars;

    private List<String> applyFeatures;
    private List<String> fundingResource;
    private String needCost;

    @Override
    protected void init() {
        cars = new ArrayList<SelectItem>();
        SelectItemGroup germanCars = new SelectItemGroup("拟用方向:");
        germanCars.setSelectItems(new SelectItem[] {
                new SelectItem("lingchuangbibei", "临床必备"),
                new SelectItem("xuekefazhan", "学科发展"),
                new SelectItem("jiaoxuekeyan", "教学科研")
        });
        cars.add(germanCars);



    }
    public void saveProfile(){

        PurchaseApplicationRepository userAccountDao = WebUtil.getBean(PurchaseApplicationRepository.class);
      //  userAccountDao.save()

      /*  WebUtil.addSuccessMessage(WebUtil.getMessage("UserAccount")+WebUtil.getMessage("Updated"));*/
    }

    @Override
    protected String getActionMessage(String actionName) {
        return null;//WebUtil.getMessage("标签"+this.getTagName(this.selected.getTagId())+"  "+WebUtil.getMessage(actionName));
    }

    @Override
    protected PurchaseApplicationRepository getDAO() {
        return dao;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public String getShenqingkeshi() {
        return shenqingkeshi;
    }

    public void setShenqingkeshi(String shenqingkeshi) {
        this.shenqingkeshi = shenqingkeshi;
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

    public List<String> getFundingResource() {
        return fundingResource;
    }

    public void setFundingResource(List<String> fundingResource) {
        this.fundingResource = fundingResource;
    }

    public void setSelectedCars(String[] selectedCars) {
        this.selectedCars = selectedCars;
    }

    public List<String> getApplyFeatures() {
        return applyFeatures;
    }

    public void setApplyFeatures(List<String> applyFeatures) {
        this.applyFeatures = applyFeatures;
    }

    public String getNeedCost() {
        return needCost;
    }

    public void setNeedCost(String needCost) {
        this.needCost = needCost;
    }
}