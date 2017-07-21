package com.ge.apm.view.asset;

import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.service.utils.QRCodeUtil;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;
import com.ge.apm.dao.TenantInfoRepository;
import java.util.stream.Collectors;
import org.primefaces.component.collector.Collector;

@ManagedBean
@ViewScoped
public class QrCodeLibController extends JpaCRUDController<QrCodeLib> {

    QrCodeLibRepository dao = null;
    I18nMessageRepository i18nDao = null;
    TenantInfoRepository tenantInfoDao = null;
    OrgInfoRepository orgInfoDao = null;
    QrCodeLibRepository qrCodeLibDao = null;

    private AssetCreateService acService;

//    private Integer siteId;
//    private Integer hospitalId;
    private String tenantUID;
    private String siteUID;
    private Integer qrCodeNum;
    private String qrCode;
    private String qrCodeImageBase64;

    private String tenantUIDFilter;
    private String siteUIDFilter;
    private Integer siteIdFilter;
    private Integer hospitalIdFilter;

    private String tempSiteId = "";

    private List<OrgInfo> hospitalFileterList=new ArrayList();

    @Override
    protected void init() {
        dao = WebUtil.getBean(QrCodeLibRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        qrCodeLibDao = WebUtil.getBean(QrCodeLibRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);

        if (userContextService.hasRole("SuperAdmin")) {
            this.filterByHospital = false;
            this.filterBySite = false;
        } else {
            tenantUIDFilter = UserContextService.getCurrentUserAccount().getTenantUID();
            this.filterBySite = true;
            hospitalFileterList = orgInfoDao.getByTenantUID(tenantUIDFilter).stream().filter(item -> item.getOrgType()<4).collect(Collectors.toList());
            if (!userContextService.hasRole("MultiHospital")) {
                this.filterByHospital = false;
            } else {
                this.filterByHospital = true;
            }
        }

    }

    @Override
    protected QrCodeLibRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<QrCodeLib> loadData(PageRequest pageRequest) {

        this.setSiteFilter();

        if (hospitalFileterList !=null && hospitalFileterList.stream().filter(item -> item.getSiteUID().equals(siteUIDFilter) && item.getTenantUID().equals(tenantUIDFilter)).collect(Collectors.toList()).size() < 1) {
            this.removeFilterOnField("siteUID");
        }

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
//        List<SearchFilter> searchFilters = this.searchFilters;
//        if (searchFilters == null || searchFilters.size() == 0) {
//            return dao.findAll(pageRequest);
//        } else {
//            Map<String, SearchFilter> tempSearchFilterMap = new HashMap<>(searchFilters.size());

//            for (SearchFilter tempSearchFilter : searchFilters) {
//                tempSearchFilterMap.put(tempSearchFilter.fieldName, tempSearchFilter);
//            }
//            if(tempSearchFilterMap.containsKey("siteId")){
//                if(!tempSearchFilterMap.get("siteId").value.toString().equals(tempSiteId)){
//                    tempSearchFilterMap.remove("hospitalId");
//                }
//
//                this.tempSiteId = tempSearchFilterMap.get("siteId").value.toString();
//            }
//
//            if(!tempSearchFilterMap.containsKey("siteId") && tempSearchFilterMap.containsKey("hospitalId")){
//                tempSearchFilterMap.remove("hospitalId");
//            }
//
//            searchFilters = new ArrayList<SearchFilter>();
//            for(Map.Entry<String, SearchFilter> entry : tempSearchFilterMap.entrySet()){
//                searchFilters.add(entry.getValue());
//            }
//            if(searchFilters.size() <= 0){
//                return dao.findAll(pageRequest);
//            }else{
//                return dao.findBySearchFilter(searchFilters, pageRequest);
//            }
//
//        }
    }

    @Override
    public List<QrCodeLib> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    public String getHospitalName(Integer hospitalId) {
        return acService.getHospitalName(hospitalId);
    }

    public String getSiteName(Integer siteId) {
        return acService.getTenantName(siteId);
    }

    public List<I18nMessage> getQrCodeLibStatusList() {

        List<I18nMessage> qrCodeLibStatusList = i18nDao.getByMsgType("qrCodeLibStatus");

        return qrCodeLibStatusList;
    }

    public List<TenantInfo> getTenantList() {

        List<SearchFilter> tenantFilter = new ArrayList();
        if (filterBySite) {
            tenantFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        }
        if (filterByHospital) {
            tenantFilter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getSiteUID()));
        }

        List<TenantInfo> tenantList = tenantInfoDao.findBySearchFilter(tenantFilter);

        return tenantList;
    }

    public void viewQrCodeLibCreate() {
        /*Map<String,Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("width", 640);
        options.put("height", 340);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        options.put("headerElement", "customheader");
        RequestContext.getCurrentInstance().openDialog("qrCodeLibCreate", options, null);*/
 /*siteId = null;
        hospitalId = null;*/
    }

    public void createQrCode() {

        if (tenantUID == null || siteUID == null) {
            return;
        }

        OrgInfo currentOrg = orgInfoDao.getByUid(siteUID);
        Set<String> tempSet = new HashSet<String>(qrCodeNum);

        DateTime dt = new DateTime();
        String dtStr = dt.toString("yyMMdd");

        int i = 1;
        while (i <= qrCodeNum) {
            String qrCode = dtStr + Long.valueOf((long) ((Math.random() * 9 + 1) * 1000000000L));
            if (!tempSet.contains(qrCode)) {
                QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode);
                if (null == qrCodeLib) {
                    qrCodeLib = new QrCodeLib();
                    qrCodeLib.setSiteId(currentOrg.getSiteId());
                    qrCodeLib.setHospitalId(currentOrg.getHospitalId());

                    qrCodeLib.setSiteUID(currentOrg.getSiteUID());
                    qrCodeLib.setHospitalUID(currentOrg.getHospitalUID());
                    qrCodeLib.setTenantUID(currentOrg.getTenantUID());
                    qrCodeLib.setInstitutionUID(currentOrg.getInstitutionUID());
                    /*-- 1:已发行(未上传) / 2: 已上传(待建档) / 3: 已建档(待删除)*/
                    qrCodeLib.setStatus(1);
                    qrCodeLib.setIssueDate(new Date());
                    qrCodeLib.setQrCode(qrCode);
                    tempSet.add(qrCode);
                    qrCodeLibDao.save(qrCodeLib);

                    i++;
                }
            }

//            if (i > qrCodeNum) {
//                siteId = null;
//                hospitalId = null;
//
//                return;
//            }
        }
        
        tenantUID = null;
        siteUID = null;

    }

    public void showQrCode() {
        qrCode = this.selected.getQrCode();
        qrCodeImageBase64 = QRCodeUtil.getQRCodeImageBase64(this.selected.getQrCode());
    }

    public void onSiteChange(){
        if(tenantUID != null){
            hospitalFileterList = orgInfoDao.getByTenantUID(tenantUID).stream().filter(item -> item.getOrgType()<4).collect(Collectors.toList());
        }
    }
    
    public void onTenantFilterChange() {
        if (tenantUIDFilter == null) {
            hospitalFileterList.clear();
            siteUIDFilter = null;
        } else {
            hospitalFileterList = orgInfoDao.getByTenantUID(tenantUIDFilter).stream().filter(item -> item.getOrgType()<4).collect(Collectors.toList());
        }
    }
    
    public Boolean isChooseTenant() {
        return this.filterBySite;
    }

    public Integer getQrCodeNum() {
        return qrCodeNum;
    }

    public Integer getSiteIdFilter() {
        return siteIdFilter;
    }

    public Integer getHospitalIdFilter() {
        return hospitalIdFilter;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getQrCodeImageBase64() {
        return qrCodeImageBase64;
    }

    public void setQrCodeNum(Integer qrCodeNum) {
        this.qrCodeNum = qrCodeNum;
    }

    public void setSiteIdFilter(Integer siteIdFilter) {
        this.siteIdFilter = siteIdFilter;
    }

    public void setHospitalIdFilter(Integer hospitalIdFilter) {
        this.hospitalIdFilter = hospitalIdFilter;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setQrCodeImageBase64(String qrCodeImageBase64) {
        this.qrCodeImageBase64 = qrCodeImageBase64;
    }

    public String getTenantUIDFilter() {
        return tenantUIDFilter;
    }

    public void setTenantUIDFilter(String tenantUIDFilter) {
        this.tenantUIDFilter = tenantUIDFilter;
    }

    public String getSiteUIDFilter() {
        return siteUIDFilter;
    }

    public void setSiteUIDFilter(String siteUIDFilter) {
        this.siteUIDFilter = siteUIDFilter;
    }

    public List<OrgInfo> getHospitalFileterList() {
        return hospitalFileterList;
    }

    public void setHospitalFileterList(List<OrgInfo> hospitalFileterList) {
        this.hospitalFileterList = hospitalFileterList;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }
    
    /*
    @Override
    public void onBeforeNewObject(QrCodeLib object) {
    }
    
    @Override
    public void onAfterNewObject(QrCodeLib object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(QrCodeLib object) {
    }
    
    @Override
    public void onAfterUpdateObject(QrCodeLib object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(QrCodeLib object) {
    }
    
    @Override
    public void onAfterDeleteObject(QrCodeLib object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(QrCodeLib object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
     */

    
}
