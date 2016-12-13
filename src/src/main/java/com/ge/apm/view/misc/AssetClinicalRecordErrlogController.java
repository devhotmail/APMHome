package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetClinicalRecordErrlogRepository;
import com.ge.apm.domain.AssetClinicalRecordErrlog;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetClinicalRecordErrlogController extends JpaCRUDController<AssetClinicalRecordErrlog> {

    AssetClinicalRecordErrlogRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetClinicalRecordErrlogRepository.class);
    }

    @Override
    protected AssetClinicalRecordErrlogRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetClinicalRecordErrlog> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetClinicalRecordErrlog> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

}