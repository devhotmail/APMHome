package com.ge.apm.view.misc;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.I18nMessage;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class I18nMessageCrudController extends JpaCRUDController<I18nMessage> {

    I18nMessageRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(I18nMessageRepository.class);
    }

    @Override
    protected I18nMessageRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<I18nMessage> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<I18nMessage> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(I18nMessage object) {
    }
    
    @Override
    public void onAfterNewObject(I18nMessage object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(I18nMessage object) {
    }
    
    @Override
    public void onAfterUpdateObject(I18nMessage object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(I18nMessage object) {
    }
    
    @Override
    public void onAfterDeleteObject(I18nMessage object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(I18nMessage object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}