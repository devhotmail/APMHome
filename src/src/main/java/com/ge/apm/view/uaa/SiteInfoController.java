package com.ge.apm.view.uaa;

import com.ge.apm.dao.OrgInfoRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.SiteInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SiteInfo;
import static java.lang.Math.E;
import org.springframework.dao.DataIntegrityViolationException;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class SiteInfoController extends JpaCRUDController<SiteInfo> {

    SiteInfoRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SiteInfoRepository.class);
    }

    @Override
    protected SiteInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<SiteInfo> loadData(PageRequest pageRequest) {
        selected = null;

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<SiteInfo> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }
    
    @Override
    public String getErrorMessageForDuplicateKey(SiteInfo site){
        return String.format(WebUtil.getMessage("DuplicateSiteName"), site.getName());
    }
    
    @Override
    public String getKeyFieldNameValue(SiteInfo site){
        return WebUtil.getMessage("name")+"="+site.getName();
    }

    public void onAfterNewObject(SiteInfo site, boolean isOK) {
        // import field code type data for this site
        String sql = "insert into i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) select msg_type, msg_key, value_zh, value_en, value_tw, %d as site_id from i18n_message where msg_type in (select msg_type from field_code_type)";
        NativeSqlUtil.execute(String.format(sql, site.getId()), null);

        // and create an default org for this site
        OrgInfo hospital = new OrgInfo();
        hospital.setSiteId(site.getId());
        hospital.setName(WebUtil.getMessage("DefaultHospitalName"));
        
        OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
        orgDao.save(hospital);
    }

    @Override
    protected boolean onPersistException(Exception ex, SiteInfo site){
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