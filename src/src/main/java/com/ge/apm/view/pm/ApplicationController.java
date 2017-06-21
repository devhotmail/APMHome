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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ManagedBean
@ViewScoped
public class ApplicationController extends JpaCRUDController<PurchaseApplication> {

    private static final long serialVersionUID = 1L;

    private PurchaseApplicationRepository dao = null;
    private List<Integer> selectedIntention1;
    private List<String> selectedIntention2;
    private String needCost;

    @Override
    protected void init() {
        this.filterBySite=false;
        dao=WebUtil.getBean(PurchaseApplicationRepository.class);
        selectedIntention1=new ArrayList<Integer>();
        selectedIntention2 = new ArrayList<String>();
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
            PurchaseApplication byId = dao.findById(this.selected.getId());

            int v1=  byId.getIntent();
            if(v1==7){
                selectedIntention2.add("1");
                selectedIntention2.add("2");
                selectedIntention2.add("4");
            }else if(v1==6){
                selectedIntention2.add("2");
                selectedIntention2.add("4");
            }else if(v1==5){
                selectedIntention2.add("1");
                selectedIntention2.add("4");
            }else if(v1==4){
                selectedIntention2.add("4");
            }else if(v1==3){
                selectedIntention2.add("1");
                selectedIntention2.add("2");
            }else if(v1==2){
                selectedIntention2.add("2");
            }else if(v1==1){
                selectedIntention2.add("1");
            }
        } else if ("Delete".equalsIgnoreCase(actionName)) {
            prepareDelete();
        }
    }

    public void onSelectAsset() throws IOException{
        if(this.selected!=null){
            PurchaseApplication byId = dao.findById(this.selected.getId());
          /*  int v1=  byId.getIntent();
            System.out.println("v1v1 "+v1);
            // selectedIntention1 = intToIntegerList(v1);
            System.out.println();*/
        }

    }

    public static  List<Integer> intToIntegerList(int ind) {
        String s = Integer.toBinaryString(ind);
        int[] intArray = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            intArray[i] = Character.digit(s.charAt(i), 10);
        }
        List<Integer> integelist  = IntStream.of(intArray).boxed().collect(Collectors.toList());
        return integelist;
    }
    protected void setSelectedByUrlParam(String encodeUrl, String paramName) {
        setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeUrl, paramName)));
    }

    @Transactional
    public void applyChange() {
        //资金来源是其它输入
        if(this.selected.getFundingResource()==4){
            this.selected.setFundingResourceOthers(this.selected.getFundingResourceOthers());
        }

        for( int i=0;i<this.selectedIntention2.size();i++){
            selectedIntention1.add(Integer.parseInt(selectedIntention2.get(i)));
        }
        int sumInt= selectedIntention1.stream().reduce(0,(Integer::sum));
        this.selected.setIntent(sumInt);
        this.save();
    }

    @Override
    protected String getActionMessage(String actionName) {
        return WebUtil.getMessage("申请单"+WebUtil.getMessage(actionName));
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

    public List<Integer> getSelectedIntention1() {
        return selectedIntention1;
    }

    public void setSelectedIntention1(List<Integer> selectedIntention1) {
        this.selectedIntention1 = selectedIntention1;
    }
    public List<String> getSelectedIntention2() {
        return selectedIntention2;
    }

    public void setSelectedIntention2(List<String> selectedIntention2) {
        this.selectedIntention2 = selectedIntention2;
    }
}