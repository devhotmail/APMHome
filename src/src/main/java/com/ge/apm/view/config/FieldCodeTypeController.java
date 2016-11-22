package com.ge.apm.view.config;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.FieldCodeTypeRepository;
import com.ge.apm.domain.FieldCodeType;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.service.DbMessageSource;

@ManagedBean
@ViewScoped
public class FieldCodeTypeController extends JpaCRUDController<FieldCodeType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(FieldCodeTypeController.class);
	
	FieldCodeTypeRepository dao = null;
	FieldCodeType selectedType = null;
    UserAccount currentUser = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(FieldCodeTypeRepository.class);
        selectedType = new FieldCodeType();
        this.currentUser = UserContextService.getCurrentUserAccount();
        //this.searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, currentUser.getSiteId()));
    }

    @Override
    protected FieldCodeTypeRepository getDAO() {
        return dao;
    }

    @Override
    public void onAfterDataChanged(){
        DbMessageSource.reLoadMessages();
    };
    
   public List<FieldCodeType> getFieldCodeTypeList(){
    	return  dao.find();
    }
    
    public void onSelect(){
    	if(LOGGER.isDebugEnabled()){
    		LOGGER.debug("invoke by ajax listener : "+ selectedType);
    	}
    }

	public FieldCodeType getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(FieldCodeType selectedType) {
		this.selectedType = selectedType;
	}
    
}