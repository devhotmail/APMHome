package com.ge.apm.view.wo;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.wo.WorkOrderImportService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by 212605082 on 2017/7/26.
 */
@ManagedBean
@ViewScoped
public class WorkOrderReportExportController {

    private WorkOrderImportService workOrderImportService;

    private StreamedContent exportFile;

    private UserAccount currentUser;

    private OrgInfoRepository orgDao;

    private List<OrgInfo> institutionList;
    private List<OrgInfo> hospitalList;
    private List<OrgInfo> siteList;

    private String currentInstitutionUID;
    private String currentHospitalUID;
    private String currentSiteUID;

    private Date startTime;
    private Date endTime;

    @PostConstruct
    protected void init() {

        workOrderImportService = WebUtil.getBean(WorkOrderImportService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);

        currentUser = UserContextService.getCurrentUserAccount();
        currentInstitutionUID = currentUser.getInstitutionUID();
        currentHospitalUID = currentUser.getHospitalUID();
        currentSiteUID = currentUser.getSiteUID();

        institutionList = orgDao.findByTenantUIDAndOrgType(currentUser.getTenantUID(), 1);
        hospitalList = orgDao.findByParentUIDAndOrgType(currentUser.getInstitutionUID(), 2);
        siteList = orgDao.findByParentUIDAndOrgType(currentUser.getHospitalUID(), 3);
    }

    public void exportData(){
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/wo/woExport/APM_WorkOrder_Template.xlsx");
//          InputStream stream = this.file.getInputstream();
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = workOrderImportService.exportData(doc, workOrderImportService.createData(currentInstitutionUID, currentHospitalUID, currentSiteUID, startTime, endTime));
            if(exportFlag){
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "WorkOrderData.xlsx");
            }
        } catch (IOException ex) {
            Logger.getLogger(WorkOrderReportExportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void filterSiteByHospital(){
        siteList = orgDao.findByParentUIDAndOrgType(this.getCurrentHospitalUID(), 3);
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public List<OrgInfo> getInstitutionList() {
        return institutionList;
    }

    public void setInstitutionList(List<OrgInfo> institutionList) {
        this.institutionList = institutionList;
    }

    public List<OrgInfo> getHospitalList() {
        return hospitalList;
    }

    public void setHospitalList(List<OrgInfo> hospitalList) {
        this.hospitalList = hospitalList;
    }

    public List<OrgInfo> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<OrgInfo> siteList) {
        this.siteList = siteList;
    }

    public String getCurrentInstitutionUID() {
        return currentInstitutionUID;
    }

    public void setCurrentInstitutionUID(String currentInstitutionUID) {
        this.currentInstitutionUID = currentInstitutionUID;
    }

    public String getCurrentHospitalUID() {
        return currentHospitalUID;
    }

    public void setCurrentHospitalUID(String currentHospitalUID) {
        this.currentHospitalUID = currentHospitalUID;
    }

    public String getCurrentSiteUID() {
        return currentSiteUID;
    }

    public void setCurrentSiteUID(String currentSiteUID) {
        this.currentSiteUID = currentSiteUID;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
