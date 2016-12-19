package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.domain.Supplier;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class SupplierController extends JpaCRUDController<Supplier> {

    SupplierRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SupplierRepository.class);
        this.filterBySite = true;
    }

    @Override
    protected SupplierRepository getDAO() {
        return dao;
    }

    public List<Supplier> getSupplierList() {
        return dao.getBySiteId(UserContextService.getSiteId());
    }
    
    public Supplier getSupplier(Integer id) {
        return dao.findById(id);
    }
    

    @Override
    public void onBeforeNewObject(Supplier supplier) {
        supplier.setSiteId(UserContextService.getSiteId());
    }
    
    @Override
    public String getKeyFieldNameValue(Supplier supplier){
        return WebUtil.getMessage("name")+"="+supplier.getName();
    }
    
    @Override
    protected Page<Supplier> loadData(PageRequest pageRequest) {
        this.selected = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }
    
}