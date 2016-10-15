package com.ge.apm.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.ge.apm.dao.InstitutionRepository;
import com.ge.apm.domain.Site;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class JpaDynaColumnDemoController extends JpaCRUDController<Site> {

    InstitutionRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(InstitutionRepository.class);
    }

    @Override
    protected InstitutionRepository getDAO() {
        return dao;
    }

    @Override
    public String getColumnNameList() {
        return "id name nameEn location isEnabled";
    }

}