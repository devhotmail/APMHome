package com.ge.apm.view.uaa;

import com.ge.apm.dao.OrgInfoRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import java.util.UUID;
import javax.faces.context.FacesContext;
import org.springframework.dao.DataIntegrityViolationException;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;
import com.ge.apm.dao.TenantInfoRepository;

@ManagedBean
@ViewScoped
public class TenantInfoController extends JpaCRUDController<TenantInfo> {

    TenantInfoRepository dao = null;

    @Override
    protected void init() {
        filterBySite = false;

        dao = WebUtil.getBean(TenantInfoRepository.class);
    }

    @Override
    protected TenantInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<TenantInfo> loadData(PageRequest pageRequest) {
        selected = null;

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<TenantInfo> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }
    
    @Override
    public String getErrorMessageForDuplicateKey(TenantInfo site){
        return String.format(WebUtil.getMessage("DuplicateSiteName"), site.getName());
    }
    
    @Override
    public String getKeyFieldNameValue(TenantInfo site){
        return WebUtil.getMessage("name")+"="+site.getName();
    }

    @Override
    public void onAfterNewObject(TenantInfo site, boolean isOK) {
        if(!isOK) return;
/*
        // import field code type data for this site
        try{
            String sql = "insert into i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) select distinct msg_type, msg_key, value_zh, value_en, value_tw, %d as site_id from i18n_message where msg_type in (select msg_type from field_code_type)";
            NativeSqlUtil.execute(String.format(sql, site.getId()), null);
        }
        catch(Exception ex){
            logger.error(ex.getMessage(), ex);
        }
*/

        // and create an default org for this site
        String orgUID = UUID.randomUUID().toString().replace("-", "");
        OrgInfo hospital = new OrgInfo();
        hospital.setSiteId(site.getId());

        //for new UUIDs
        hospital.setUid(orgUID);
        hospital.setTenantUID(selected.getUid());
        hospital.setInstitutionUID(orgUID);
        hospital.setHospitalUID(orgUID);
        hospital.setSiteUID(orgUID);
        
        hospital.setName(WebUtil.getMessage("DefaultHospitalName"));
        
        OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
        orgDao.save(hospital);
    }

    @Override
    protected boolean onPersistException(Exception ex, TenantInfo site){
        if(ex.getClass().equals(DataIntegrityViolationException.class)){
            DataIntegrityViolationException exFkey = (DataIntegrityViolationException) ex;
            if(exFkey.getRootCause().getMessage().toLowerCase().contains("foreign key constraint ")){
                String errMessage = WebUtil.getMessage("ForeighKeyErrorWithSiteId");
                WebUtil.addErrorMessage(errMessage);

                return true;
            }
        }
                
        return false;
    }
    
}