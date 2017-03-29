package com.ge.apm.view.asset;

import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SiteInfoRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SiteInfo;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class QrCodeLibController extends JpaCRUDController<QrCodeLib> {

    QrCodeLibRepository dao = null;
    I18nMessageRepository i18nDao = null;
    SiteInfoRepository siteInfoDao = null;
    OrgInfoRepository orgInfoDao = null;
    QrCodeLibRepository qrCodeLibDao = null;

    private AssetCreateService acService;

    private Integer siteId;
    private Integer hospitalId;
    private Integer qrCodeNum;

    @Override
    protected void init() {
        dao = WebUtil.getBean(QrCodeLibRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        siteInfoDao = WebUtil.getBean(SiteInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        qrCodeLibDao = WebUtil.getBean(QrCodeLibRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);

        
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);

        if (userContextService.hasRole("SuperAdmin")) {
            this.filterByHospital = false;
            this.filterBySite = false;
        } else {
            this.filterBySite = true;
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
        if (this.searchFilters == null || this.searchFilters.size() == 0) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
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
        return acService.getSiteName(siteId);
    }

    public List<I18nMessage> getQrCodeLibStatusList() {

        List<I18nMessage> qrCodeLibStatusList = i18nDao.getByMsgType("qrCodeLibStatus");

        return qrCodeLibStatusList;
    }

    public List<SiteInfo> getSiteList() {

        List<SiteInfo> siteList = siteInfoDao.find();

        return siteList;
    }

    public List<OrgInfo> getHospitalList() {

        List<OrgInfo> hospitalList = null;
        if (siteId != null) {

            hospitalList = orgInfoDao.getHospitalBySiteId(Integer.valueOf(siteId));
        }

        return hospitalList;
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
        siteId = null;
        hospitalId = null;
    }

    public void createQrCode() {
        Set<String> tempSet = new HashSet<String>(qrCodeNum);

        DateTime dt = new DateTime();
        String dtStr = dt.toString("yyMMdd");

        int i = 1;
        while (true) {
            String qrCode = dtStr + Long.valueOf((long) ((Math.random() * 9 + 1) * 1000000000L));
            if (!tempSet.contains(qrCode)) {
                QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode);
                if (null == qrCodeLib) {
                    qrCodeLib = new QrCodeLib();
                    qrCodeLib.setSiteId(siteId);
                    qrCodeLib.setHospitalId(hospitalId);
                    /*-- 1:已发行(未上传) / 2: 已上传(待建档) / 3: 已建档(待删除)*/
                    qrCodeLib.setStatus(1);
                    qrCodeLib.setIssueDate(new Date());
                    qrCodeLib.setQrCode(qrCode);
                    tempSet.add(qrCode);
                    qrCodeLibDao.save(qrCodeLib);

                    i++;
                }
            }

            if (i > qrCodeNum){
                siteId = null;
                hospitalId = null;

                return;
            }
        }

    }

    public void onSiteChange(){
        if(siteId == null){
            hospitalId = null;
        }
    }

    public Integer getSiteId() {
        return siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public Integer getQrCodeNum() {
        return qrCodeNum;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setQrCodeNum(Integer qrCodeNum) {
        this.qrCodeNum = qrCodeNum;
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
