package com.ge.apm.view.sysutil;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkflowConfig;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class WorkflowConfigController extends JpaCRUDController<WorkflowConfig> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	WorkflowConfigRepository dao = null;
	private UserAccount user;
	private UserAccountRepository userAccountRepository;

    @Override
    protected void init() {
    	user = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(WorkflowConfigRepository.class);
    }

    @Override
    protected WorkflowConfigRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<WorkflowConfig> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<WorkflowConfig> getItemList() {
        return dao.find();
    }

	public UserAccount getUser() {
		return user;
	}

	public void setUser(UserAccount user) {
		this.user = user;
	}

    @Override
    public void onBeforeSave(WorkflowConfig config) {
    	config.setSiteId(user.getSiteId());
    	config.setHospitalId(user.getHospitalId());
    	config.setDispatchUserName(userAccountRepository.getById(config.getDispatchUserId()).getName());
    }
/*
    @Override
    public void onBeforeNewObject(WorkflowConfig object) {
    }
    
    @Override
    public void onAfterNewObject(WorkflowConfig object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(WorkflowConfig object) {
    }
    
    @Override
    public void onAfterUpdateObject(WorkflowConfig object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(WorkflowConfig object) {
    }
    
    @Override
    public void onAfterDeleteObject(WorkflowConfig object, boolean isOK) {
    }
    

    
    @Override
    public void onAfterDataChanged(){
    };
*/
    
    
}