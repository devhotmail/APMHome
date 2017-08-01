package com.ge.apm.view.sysutil;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.V2_WorkflowConfigRepository;
import com.ge.apm.domain.V2_DeviceType;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkflowConfig;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class V2_WorkflowConfigController extends JpaCRUDController<V2_WorkflowConfig> {
	
	private static final long serialVersionUID = 1L;
	V2_WorkflowConfigRepository dao = null;
	private UserAccount user;
	private List<UserAccount> dispatchUserList;
//	private UaaService uuaService;
    private UserAccount owner;
    private UserAccountRepository userDao;
    private List<UserInfo> userList;
    private List<V2_DeviceType> deviceType;
    

	@Override
	protected void init() {
            user = UserContextService.getCurrentUserAccount();
            dao = WebUtil.getBean(V2_WorkflowConfigRepository.class);
//            uuaService =  (UaaService) WebUtil.getBean(UaaService.class);
            //dispatchUserList = uuaService.getUsersWithAssetHeadOrStaffRole(user.getHospitalId());
            
            initDispatchUserList();
            //deviceType = 
            this.selected = dao.findByTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(user.getTenantUID(),user.getInstitutionUID(),user.getHospitalUID(), user.getSiteUID());
//            if (selected == null) {
//                initWorkflowConfig(user);
//            }
	}
        


    private void initDispatchUserList(){
    	userList = userDao.findByInstitutionUID(user.getInstitutionUID());
        //userList = new ArrayList<UserInfo>();
/*        try{
            String sql = "select distinct u.id, u.name, o.name as hospital_name, o.id as hospital_id from user_account u, org_info o, user_role r where u.hospital_id=o.id and u.id=r.user_id and r.role_id in (2,3) and u.site_id=:#siteId and u.hospital_id=:#hospitalId " +
                         "union all " +
                         "select distinct u.id, u.name, o.name as hospital_name, o.id as hospital_id from user_account u, org_info o, user_role r where u.hospital_id=o.id and u.id=r.user_id and r.role_id in (2,3) and u.site_id=:#siteId and u.hospital_id<>:#hospitalId";

            Map<String, Object> sqlParams = new HashMap<>();
            sqlParams.put("hospitalId", user.getHospitalId());
            sqlParams.put("siteId", user.getSiteId());

            List<Map<String, Object>> userListMap = NativeSqlUtil.queryForList(sql, sqlParams);
            for(Map<String, Object> infoMap: userListMap){
                UserInfo userInfo = new UserInfo();
                userList.add(userInfo);

                userInfo.setId(Integer.parseInt(infoMap.get("id").toString()));
                userInfo.setHospitalId(Integer.parseInt(infoMap.get("hospital_id").toString()));
                userInfo.setName(infoMap.get("name").toString());
                userInfo.setHospitalName(infoMap.get("hospital_name").toString());

                if(!userInfo.getHospitalId().equals(user.getHospitalId()))
                    userInfo.setName(String.format("%s(%s)", userInfo.getName(), userInfo.getHospitalName()));
            }
        }
        catch(Exception ex){
        }*/
    }

    public List<UserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<UserInfo> userList) {
        this.userList = userList;
    }
        
/*	public void initWorkflowConfig(UserAccount user) {
		WorkflowConfig wfConfig = new WorkflowConfig();
		wfConfig.setHospitalId(user.getHospitalId());
		wfConfig.setSiteId(user.getSiteId());

		wfConfig.setDispatchMode(3);
		wfConfig.setTimeoutDispatch(30);
		wfConfig.setTimeoutAccept(30);
		wfConfig.setTimeoutRepair(300); // 5 hours
		wfConfig.setTimeoutClose(30);
		wfConfig.setOrderReopenTimeframe(7);

		this.selected = dao.save(wfConfig);
	}*/

	@Override
	protected V2_WorkflowConfigRepository getDAO() {
		return dao;
	}

	@Override
	protected Page<V2_WorkflowConfig> loadData(PageRequest pageRequest) {
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
	public List<V2_WorkflowConfig> getItemList() {
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
	public void onBeforeSave(V2_WorkflowConfig config) {
            UserAccountRepository uaDao = WebUtil.getBean(UserAccountRepository.class);
            try{
                config.setDispatchUserName(uaDao.getById(selected.getDispatchUserId()).getName());
            }
            catch(Exception ex){
            }

            try{
                config.setDispatchUserName2(uaDao.getById(selected.getDispatchUserId2()).getName());            }
            catch(Exception ex){
            }
            
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