package com.ge.apm.view.asset;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.utils.QRCodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class GenerateQrCodesController extends JpaCRUDController<QrCodeLib>{
	Logger logger = LoggerFactory.getLogger(GenerateQrCodesController.class);
	private static final long serialVersionUID = -1;
	QrCodeLibRepository dao = null;
    TenantInfoRepository tenantdao = null;
    OrgInfoRepository orgDao = null;
    AssetCreateService acService;
    UserContextService userContextService;
    
    private Integer siteId;
    private Integer hospitalId;
    private Integer big;//大图数量
    private Integer small;//小图数量

    @Override
    protected void init() {
    	orgDao = WebUtil.getBean(OrgInfoRepository.class);
        dao = WebUtil.getBean(QrCodeLibRepository.class);
        tenantdao = WebUtil.getBean(TenantInfoRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        userContextService = WebUtil.getBean(UserContextService.class);

        if (userContextService.hasRole("SuperAdmin")) {
            this.filterByHospital = false;
            this.filterBySite = false;
        } else {
            this.filterBySite = true;
            if (userContextService.hasRole("MultiHospital")) {
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
    
    private DefaultStreamedContent qrZip;
    
    public DefaultStreamedContent getQrZip() {
		return qrZip;
    }

    public List<TenantInfo> getSiteList() {
        List<TenantInfo> res = new ArrayList<TenantInfo>();
        if (userContextService.hasRole("SuperAdmin")) {
            res.addAll(tenantdao.find());
        } else {
            res.add(tenantdao.findById(UserContextService.getCurrentUserAccount().getSiteId()));
        }
        return res;
    }

	public String getHospitalName(Integer hospitalId) {
        return acService.getHospitalName(hospitalId);
    }

    public String getSiteName(Integer siteId) {
        return acService.getTenantName(siteId);
    }
    
    public String getOrgName(Integer orgId) {
        return acService.getOrgName(orgId);
    }
    
    public List<OrgInfo> getHospitalList() {
        List<OrgInfo> hospitalList = null;
        if (siteId != null) {
            hospitalList = orgDao.getHospitalBySiteId(Integer.valueOf(siteId));
        }
        return hospitalList;
    }
    
    public List<OrgInfo> getOrgList() {
    	return  acService.getClinicalDeptList(UserContextService.getCurrentUserAccount().getHospitalId());
    }
    
    public void onSiteChange(){
        if(siteId == null){
            hospitalId = null;
        }
    }
    
    public List<String> saveQrCode(Integer siteId,Integer hospitalId,Integer big,Integer small) {
        if(siteId == null || hospitalId == null){
            return null;
        }
        
        OrgInfo org = orgDao.findById(hospitalId);
        int number = big + small;
        Set<String> tempSet = new HashSet<String>(number);
        DateTime dt = new DateTime();
        String dtStr = dt.toString("yyMMdd");
        int i = 1;
        List<QrCodeLib> result = new ArrayList<QrCodeLib>(number * 2);
        while (true) {
            String qrCode = dtStr + Long.valueOf((long) ((Math.random() * 9 + 1) * 1000000000L));
            if (!tempSet.contains(qrCode)) {
                QrCodeLib qrCodeLib = dao.findByQrCode(qrCode);
                if (null == qrCodeLib) {
                    qrCodeLib = new QrCodeLib();
                    qrCodeLib.setSiteId(siteId);
                    qrCodeLib.setHospitalId(hospitalId);
                    qrCodeLib.setSiteUID(org.getSiteUID());
                    qrCodeLib.setHospitalUID(org.getHospitalUID());
                    qrCodeLib.setTenantUID(org.getTenantUID());
                    qrCodeLib.setInstitutionUID(org.getInstitutionUID());
                   // -- 1:已发行(未上传) / 2: 已上传(待建档) / 3: 已建档(待删除)
                    qrCodeLib.setStatus(1);
                    qrCodeLib.setIssueDate(new Date());
                    qrCodeLib.setQrCode(qrCode);
                    result.add(qrCodeLib);
                    tempSet.add(qrCode);
//                    dao.save(qrCodeLib);
                    i++;
                }
            }
            if (i > number){
                dao.save(result);
                return  new ArrayList<String>(tempSet);
            }
        }

    }
    
    public void createQrCode() {
    	logger.info("siteId is {},hospitalId is {},small is {},big is {}",siteId,hospitalId,small,big);
        if(siteId == null || hospitalId == null){
            return;
        }
        List<String> codes = saveQrCode(siteId,hospitalId,big,small);
        if(CollectionUtils.isEmpty(codes)){
        	logger.error("save qr code error !");
        	return;
        }
        try {
			String zip = QRCodeUtil.generateQrCodes(codes,small,big,siteId);
			File zipFile = new File(zip);
			qrZip = new DefaultStreamedContent(new FileInputStream(zipFile));
			qrZip.setName("qrCodes.zip");
            siteId = null;
            hospitalId = null;
		} catch (Exception e) {
			logger.error("generateQrCodes error,ex is {}",e);
			e.printStackTrace();
		}
    }
    
	public Integer getBig() {
		return big;
	}
	
	public void setBig(Integer big) {
		this.big = big;
	}
	
	public Integer getSmall() {
		return small;
	}
	
	public void setSmall(Integer small) {
		this.small = small;
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
}
