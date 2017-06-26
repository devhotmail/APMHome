package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.asset.AssetTagService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 212605082 on 2017/5/12.
 */
@ManagedBean
@ViewScoped
public class AssetTagController extends JpaCRUDController<AssetTag> {

    AssetTagRepository dao = null;
    I18nMessageRepository i18nDao = null;
    TenantInfoRepository tenantInfoDao = null;
    OrgInfoRepository orgInfoDao = null;

    private DualListModel<BiomedGroup> availableBiomedGroup;
    private List<BiomedGroup> sourceBiomedGroup;
    private List<BiomedGroup> targetBiomedGroup;
    private BiomedGroup queryBiomedGroup;

    private DualListModel<AssetInfo> availableAssets;
    private List<AssetInfo> sourceAssets;
    private List<AssetInfo> targetAssets;
    private AssetInfo queryAsset;

    private AssetCreateService acService;
    private AssetTagService assetTagService;

    private Integer siteId;
    private Integer hospitalId;

    private Integer siteIdFilter;
    private Integer hospitalIdFilter;

    private UserAccount currentUser;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetTagRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        assetTagService = WebUtil.getBean(AssetTagService.class);

        currentUser = UserContextService.getCurrentUserAccount();

        this.siteIdFilter = currentUser.getSiteId();

        targetBiomedGroup = new ArrayList<>();
        queryBiomedGroup = new BiomedGroup();
        queryBiomedGroup.setSiteId(currentUser.getSiteId());
        queryBiomedGroup.setHospitalId(currentUser.getHospitalId());
        this.getQueryBiomedGroupList();

        targetAssets = new ArrayList();
        queryAsset = new AssetInfo();
        queryAsset.setSiteId(currentUser.getSiteId());
        //queryAsset.setHospitalId(currentUser.getHospitalId());
        this.getQueryAssetsList();

    }

    @Override
    protected AssetTagRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetTag> loadData(PageRequest pageRequest) {

        selected = null;

        List<SearchFilter> searchFilters = this.searchFilters;
        SearchFilter siteIdFilter = new SearchFilter("siteId", SearchFilter.Operator.EQ, currentUser.getSiteId());
        SearchFilter hospitalIdFilter = new SearchFilter("hospitalId", SearchFilter.Operator.EQ, currentUser.getHospitalId());
        searchFilters.add(siteIdFilter);
        searchFilters.add(hospitalIdFilter);

        if(searchFilters.size() <= 0){
            return dao.findAll(pageRequest);
        }else{
            return dao.findBySearchFilter(searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetTag> getItemList() {
        return dao.find();
    }

    public String getHospitalName(Integer hospitalId) {
        return acService.getHospitalName(hospitalId);
    }

    public String getTenantName(Integer siteId) {
        return acService.getTenantName(siteId);
    }

    public List<TenantInfo> getSiteList() {

        List<TenantInfo> siteList = tenantInfoDao.find();

        return siteList;
    }

    public List<OrgInfo> getHospitalList() {

        List<OrgInfo> hospitalList = null;
        if (siteId != null) {

            hospitalList = orgInfoDao.getHospitalBySiteId(Integer.valueOf(siteId));
        }

        return hospitalList;
    }

    public List<OrgInfo> getHospitalListFilter() {

        List<OrgInfo> hospitalList = null;
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

    public void getQueryBiomedGroupList() {
        sourceBiomedGroup = assetTagService.getBiomedGroupList(queryBiomedGroup);
        targetBiomedGroup.forEach((item)->{
            if(sourceBiomedGroup.contains(item)){
                sourceBiomedGroup.remove(item);
            }
        });

        availableBiomedGroup = new DualListModel<>(sourceBiomedGroup, targetBiomedGroup);
    }

    public void onBiomedGroupTransfer(TransferEvent event) {

        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetBiomedGroup.add((BiomedGroup)item);
            }else{
                targetBiomedGroup.remove((BiomedGroup)item);
            }
        }
    }

    public void getQueryAssetsList() {
        sourceAssets = assetTagService.getAssetList(queryAsset);
        targetAssets.forEach((item)->{
            if(sourceAssets.contains(item)){
                sourceAssets.remove(item);
            }
        });

        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }

    public void onAssetsTransfer(TransferEvent event) {

        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetAssets.add((AssetInfo)item);
            }else{
                targetAssets.remove((AssetInfo)item);
            }
        }
    }

    public List<OrgInfo> getClinicalDeptList() {
        List<OrgInfo> tempDeptList = new ArrayList<>();
        if(queryAsset.getHospitalId() != null && queryAsset.getHospitalId() != 0){
            tempDeptList = acService.getClinicalDeptList(queryAsset.getHospitalId());
        }
        return tempDeptList;
    }

    public List<UserAccount> getOwnerList() {
        List<UserAccount> res = acService.getAssetOnwers(queryAsset.getSiteId(), queryAsset.getHospitalId());
        return res;
    }

    public void save(){
        super.save();
        assetTagService.saveAssetTag(this.getSelected(), targetBiomedGroup, targetAssets);
    }

    @Override
    public void onBeforeNewObject(AssetTag object) {
        object.setSiteId(currentUser.getSiteId());
        object.setHospitalId(currentUser.getHospitalId());

        targetBiomedGroup = assetTagService.getTargetBiomedGroup(this.getSelected());
        this.getQueryBiomedGroupList();

        targetAssets = assetTagService.getTargetAssets(this.getSelected());
        this.getQueryAssetsList();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();

        targetBiomedGroup = assetTagService.getTargetBiomedGroup(this.getSelected());
        this.getQueryBiomedGroupList();

        targetAssets = assetTagService.getTargetAssets(this.getSelected());
        this.getQueryAssetsList();
    }

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

    public DualListModel<BiomedGroup> getAvailableBiomedGroup() {
        return availableBiomedGroup;
    }

    public void setAvailableBiomedGroup(DualListModel<BiomedGroup> availableBiomedGroup) {
        this.availableBiomedGroup = availableBiomedGroup;
    }

    public List<BiomedGroup> getSourceBiomedGroup() {
        return sourceBiomedGroup;
    }

    public void setSourceBiomedGroup(List<BiomedGroup> sourceBiomedGroup) {
        this.sourceBiomedGroup = sourceBiomedGroup;
    }

    public List<BiomedGroup> getTargetBiomedGroup() {
        return targetBiomedGroup;
    }

    public void setTargetBiomedGroup(List<BiomedGroup> targetBiomedGroup) {
        this.targetBiomedGroup = targetBiomedGroup;
    }

    public DualListModel<AssetInfo> getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public List<AssetInfo> getSourceAssets() {
        return sourceAssets;
    }

    public void setSourceAssets(List<AssetInfo> sourceAssets) {
        this.sourceAssets = sourceAssets;
    }

    public List<AssetInfo> getTargetAssets() {
        return targetAssets;
    }

    public void setTargetAssets(List<AssetInfo> targetAssets) {
        this.targetAssets = targetAssets;
    }

    public BiomedGroup getQueryBiomedGroup() {
        return queryBiomedGroup;
    }

    public void setQueryBiomedGroup(BiomedGroup queryBiomedGroup) {
        this.queryBiomedGroup = queryBiomedGroup;
    }

    public AssetInfo getQueryAsset() {
        return queryAsset;
    }

    public void setQueryAsset(AssetInfo queryAsset) {
        this.queryAsset = queryAsset;
    }
}
