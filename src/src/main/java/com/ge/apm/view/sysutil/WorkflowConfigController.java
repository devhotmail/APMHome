package com.ge.apm.view.sysutil;

import com.ge.apm.dao.UserAccountRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.uaa.UaaService;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class WorkflowConfigController extends JpaCRUDController<WorkflowConfig> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	WorkflowConfigRepository dao = null;
	private UserAccount user;
	private List<UserAccount> dispatchUserList;
	private UaaService uuaService;
        private UserAccount owner;

	@Override
	protected void init() {
            user = UserContextService.getCurrentUserAccount();
            dao = WebUtil.getBean(WorkflowConfigRepository.class);
            uuaService =  (UaaService) WebUtil.getBean(UaaService.class);
            dispatchUserList = uuaService.getUsersWithAssetHeadOrStaffRole(user.getHospitalId());
            this.selected = dao.getBySiteIdAndHospitalId(user.getSiteId(), user.getHospitalId());
            if (selected == null) {
                initWorkflowConfig(user);
            }
	}
        
        public void initWorkflowConfig(UserAccount user){
            WorkflowConfig wfConfig = new WorkflowConfig();
            wfConfig.setHospitalId(user.getHospitalId());
            wfConfig.setSiteId(user.getSiteId());

            wfConfig.setDispatchMode(3);
            wfConfig.setTimeoutDispatch(30);
            wfConfig.setTimeoutAccept(30);
            wfConfig.setTimeoutRepair(300);  //5 hours
            wfConfig.setTimeoutClose(30);
            wfConfig.setOrderReopenTimeframe(7);

            this.selected = dao.save(wfConfig);
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

        public void onOwnerChange() {
            if (null != owner) {
                selected.setDispatchUserId(owner.getId());
                selected.setDispatchUserName(owner.getName());
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

	public List<UserAccount> getDispatchUserList() {
		return dispatchUserList;
	}

	public void setDispatchUserList(List<UserAccount> dispatchUserList) {
		this.dispatchUserList = dispatchUserList;
	}

	@Override
	public void onBeforeSave(WorkflowConfig config) {
//            UserAccountRepository uaDao = WebUtil.getBean(UserAccountRepository.class);
//            config.setDispatchUserName(uaDao.getById(selected.getDispatchUserId()).getName());
	}

	public UserAccount getOwner() {
		return owner;
	}

	public void setOwner(UserAccount owner) {
		this.owner = owner;
	}
    
	/*
	 * @Override public void onBeforeNewObject(WorkflowConfig object) { }
	 * 
	 * @Override public void onAfterNewObject(WorkflowConfig object, boolean
	 * isOK) { }
	 * 
	 * @Override public void onBeforeUpdateObject(WorkflowConfig object) { }
	 * 
	 * @Override public void onAfterUpdateObject(WorkflowConfig object, boolean
	 * isOK) { }
	 * 
	 * @Override public void onBeforeDeleteObject(WorkflowConfig object) { }
	 * 
	 * @Override public void onAfterDeleteObject(WorkflowConfig object, boolean
	 * isOK) { }
	 * 
	 * 
	 * 
	 * @Override public void onAfterDataChanged(){ };
	 */

}