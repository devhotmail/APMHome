package com.ge.apm.view.pm;

import com.ge.apm.dao.AssetTagMsgSubscriberRepository;
import com.ge.apm.dao.AssetTagRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetTag;
import com.ge.apm.domain.AssetTagMsgSubscriber;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class ApplicationController extends JpaCRUDController<AssetTagMsgSubscriber> {

    private static final long serialVersionUID = 1L;

    private AssetTagMsgSubscriberRepository dao = null;
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

        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);

        WebUtil.addSuccessMessage(WebUtil.getMessage("UserAccount")+WebUtil.getMessage("Updated"));
    }

    @Override
    protected String getActionMessage(String actionName) {
        return null;//WebUtil.getMessage("标签"+this.getTagName(this.selected.getTagId())+"  "+WebUtil.getMessage(actionName));
    }

    @Override
    protected AssetTagMsgSubscriberRepository getDAO() {
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