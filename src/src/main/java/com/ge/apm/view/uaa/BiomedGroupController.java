package com.ge.apm.view.uaa;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.uaa.BiomedGroupService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 212605082 on 2017/5/15.
 */
@ManagedBean
@ViewScoped
public class BiomedGroupController extends JpaCRUDController<BiomedGroup> {

    private BiomedGroupRepository dao = null;
    private I18nMessageRepository i18nDao = null;
    private TenantInfoRepository siteInfoDao = null;
    private OrgInfoRepository orgInfoDao = null;

    private DualListModel<UserAccount> availableUserAccount;
    private List<UserAccount> sourceUserAccount;
    private List<UserAccount> targetUserAccount;

    private UserAccount queryUserAccount;

    private AssetCreateService acService;
    private BiomedGroupService biomedGroupService;

    private Integer siteId;
    private Integer hospitalId;

    private Integer siteIdFilter;
    private Integer hospitalIdFilter;

    private UserAccount currentUser;

    private BiomedGroup tempBiomedGroup;

    @Override
    protected void init() {
        dao = WebUtil.getBean(BiomedGroupRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        siteInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        biomedGroupService = WebUtil.getBean(BiomedGroupService.class);

        targetUserAccount = new ArrayList<UserAccount>();

        currentUser = UserContextService.getCurrentUserAccount();

        this.siteIdFilter = currentUser.getSiteId();

        queryUserAccount = new UserAccount();
        queryUserAccount.setSiteId(currentUser.getSiteId());
        //queryUserAccount.setHospitalId(currentUser.getHospitalId());
        this.getQueryUserAccountList();
    }

    @Override
    protected BiomedGroupRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<BiomedGroup> loadData(PageRequest pageRequest) {

        selected = null;

        List<SearchFilter> searchFilters = this.searchFilters;
        SearchFilter siteIdFilter = new SearchFilter("siteId", SearchFilter.Operator.EQ, currentUser.getSiteId());
        SearchFilter hospitalIdFilter = new SearchFilter("hospitalId", SearchFilter.Operator.EQ, currentUser.getHospitalId());
        searchFilters.add(siteIdFilter);
        searchFilters.add(hospitalIdFilter);

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(searchFilters, pageRequest);
        }

    }

    @Override
    public List<BiomedGroup> getItemList() {
        return dao.find();
    }

    public String getHospitalName(Integer hospitalId) {
        return acService.getHospitalName(hospitalId);
    }

    public String getSiteName(Integer siteId) {
        return acService.getTenantName(siteId);
    }

    public List<TenantInfo> getSiteList() {

        List<TenantInfo> siteList = siteInfoDao.find();

        return siteList;
    }

    public List<OrgInfo> getHospitalList() {

        List<OrgInfo> hospitalList = new ArrayList<>();
        if (siteId != null) {

            hospitalList = orgInfoDao.getHospitalBySiteId(Integer.valueOf(siteId));
        }

        return hospitalList;
    }

    public List<OrgInfo> getHospitalListFilter() {

        List<OrgInfo> hospitalList = new ArrayList<>();
        if (siteIdFilter != null) {

            hospitalList = orgInfoDao.getHospitalBySiteId(Integer.valueOf(siteIdFilter));
        }

        return hospitalList;
    }

    public void onSiteChange(){
        if(siteId == null){
            hospitalId = null;
        }
    }

    public void onSiteFilterChange(){
        if(siteIdFilter == null){
            hospitalIdFilter = null;
        }
    }

    public void getQueryUserAccountList() {
        sourceUserAccount = biomedGroupService.getSourceUserAccountList(queryUserAccount);
        targetUserAccount.forEach((item)->{
            if(sourceUserAccount.contains(item)){
                sourceUserAccount.remove(item);
            }
        });

        availableUserAccount = new DualListModel<>(sourceUserAccount, targetUserAccount);
    }

    public void onUserAccountTransfer(TransferEvent event) {

        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetUserAccount.add((UserAccount)item);
            }else{
                targetUserAccount.remove((UserAccount)item);
            }
        }
    }

    public void save (){
        super.save();
        biomedGroupService.saveBiomedGroup(this.getSelected(), targetUserAccount);
    }

    @Override
    public void onBeforeNewObject(BiomedGroup object) {
        object.setSiteId(currentUser.getSiteId());
        object.setHospitalId(currentUser.getHospitalId());

        targetUserAccount = new ArrayList<UserAccount>();
        this.getQueryUserAccountList();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();

        targetUserAccount = biomedGroupService.getTargetUserAccount(this.getSelected());
        this.getQueryUserAccountList();
    }

    /*
    @Override
    public void onAfterNewObject(BiomedGroup object, boolean isOK) {
    }

    @Override
    public void onAfterUpdateObject(BiomedGroup object, boolean isOK) {
    }

    @Override
    public void onBeforeDeleteObject(BiomedGroup object) {
    }

    @Override
    public void onAfterDeleteObject(BiomedGroup object, boolean isOK) {
    }

    @Override
    public void onBeforeSave(BiomedGroup object) {
    }

    @Override
    public void onAfterDataChanged(){
    };
     */

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getSiteIdFilter() {
        return siteIdFilter;
    }

    public void setSiteIdFilter(Integer siteIdFilter) {
        this.siteIdFilter = siteIdFilter;
    }

    public Integer getHospitalIdFilter() {
        return hospitalIdFilter;
    }

    public void setHospitalIdFilter(Integer hospitalIdFilter) {
        this.hospitalIdFilter = hospitalIdFilter;
    }

    public DualListModel<UserAccount> getAvailableUserAccount() {
        return availableUserAccount;
    }

    public void setAvailableUserAccount(DualListModel<UserAccount> availableUserAccount) {
        this.availableUserAccount = availableUserAccount;
    }

    public List<UserAccount> getSourceUserAccount() {
        return sourceUserAccount;
    }

    public void setSourceUserAccount(List<UserAccount> sourceUserAccount) {
        this.sourceUserAccount = sourceUserAccount;
    }

    public List<UserAccount> getTargetUserAccount() {
        return targetUserAccount;
    }

    public void setTargetUserAccount(List<UserAccount> targetUserAccount) {
        this.targetUserAccount = targetUserAccount;
    }

    public UserAccount getQueryUserAccount() {
        return queryUserAccount;
    }

    public void setQueryUserAccount(UserAccount queryUserAccount) {
        this.queryUserAccount = queryUserAccount;
    }
}
