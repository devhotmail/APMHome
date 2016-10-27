package com.ge.apm.view.uaa;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class OrgInfoController extends JpaCRUDController<OrgInfo> {

    OrgInfoRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(OrgInfoRepository.class);
    }

    @Override
    protected OrgInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<OrgInfo> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public List<OrgInfo> getHospitalDeptList() {
        return dao.getByHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    @Override
    public void onBeforeNewObject(OrgInfo object) {
        object.setHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
    }
    
}