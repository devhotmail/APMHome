package com.ge.apm.view.uaa;

import com.ge.apm.dao.SiteInfoRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SiteInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserRole;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class UserAccountController extends JpaCRUDController<UserAccount> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1L;

	public static final String DEFAULT_USER_ROLE = "设备科科员";

    UserAccountRepository dao = null;
    UaaService uaaService = WebUtil.getBean(UaaService.class);

    @Override
    protected void init() {
        filterBySite = false;
        
        uaaService = WebUtil.getBean(UaaService.class);
        dao = WebUtil.getBean(UserAccountRepository.class);

        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        setSiteId(loginUser.getSiteId());
        /*
        orgTree = buildOrgTree(UserContextService.getSiteId(), loginUser.getOrgInfoId());
        try{
            DefaultTreeNode selectedNode = (DefaultTreeNode)orgTree.getData();
            this.selectedOrg = (OrgInfo) selectedNode.getData();
        }
        catch(Exception ex){
        }*/
    }
    private int siteId;
    public int getSiteId() {
        return siteId;
    }
    public void setSiteId(int siteId) {
        this.siteId = siteId;
        SiteInfoRepository siteDao = WebUtil.getBean(SiteInfoRepository.class);
        selectedSite = siteDao.findById(siteId);
        orgList = uaaService.getFullOrgListBySiteId(siteId);
        buildOrgTree(siteId, null);
    }
    
    private SiteInfo selectedSite;
    public SiteInfo getSelectedSite() {
        return selectedSite;
    }

    private List<OrgInfo> orgList;
    public List<OrgInfo> getOrgList() {
        return orgList;
    }
    public String getOrgName(int orgId, String hospitalNameDelimiter){
        for(OrgInfo org: orgList){
            if(orgId==org.getId()){
                if(org.getId().equals(org.getHospitalId()))
                    return org.getName();
                else
                    return org.getHospital()+hospitalNameDelimiter+org.getName();
            }
        }
        return "";
    }
    
    public TreeNode buildOrgTree(int siteId, Integer selectedOrgId){
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        
        DefaultTreeNode rootNode = new DefaultTreeNode("org", rootOrg, null);
        rootNode.setRowKey("org_"+rootOrg.getId());
        rootNode.setExpanded(true);
        
        orgTree = uaaService.getFullOrgTreeBySiteId(rootNode, siteId, selectedOrgId);

        try{
            selectedOrg = null;
            DefaultTreeNode selectedNode = (DefaultTreeNode)orgTree.getData();
            selectedOrg = (OrgInfo) selectedNode.getData();
        }
        catch(Exception ex){
        }
        try{
            if(selectedOrg==null)
                selectedOrg = (OrgInfo)((DefaultTreeNode)orgTree.getChildren().get(0)).getData();
        }
        catch(Exception ex){
            
        }
        
        return orgTree;
    }    
    @Override
    protected UserAccountRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<UserAccount> loadData(PageRequest pageRequest) {
        this.selected = null;
        if ( selectedOrg == null) {
            return dao.getBySiteId(pageRequest, this.siteId);
        } else {
            if(selectedOrg.getId()==-1)
                return dao.getBySiteId(pageRequest, this.siteId);
            else
                return dao.getByOrgInfoId(pageRequest, selectedOrg.getId());
        }
    }

    @Override
    public String getErrorMessageForDuplicateKey(UserAccount userAccount){
        return String.format(WebUtil.getMessage("DuplicateLoginName"), userAccount.getLoginName());
    }
    
    @Override
    public String getKeyFieldNameValue(UserAccount obj){
        return WebUtil.getMessage("login_name")+"="+obj.getLoginName();
    }
    
    private List<String> allRoles;
    private List<String> assignedRoles;
    public List<String> getAllRoles() {
        return allRoles;
    }

    public void setAllRoles(List<String> allRoles) {
        this.allRoles = allRoles;
    }

    public List<String> getAssignedRoles() {
        return assignedRoles;
    }

    public void setAssignedRoles(List<String> assignedRoles) {
        this.assignedRoles = assignedRoles;
    }
    
    public void prepareRoleData() {
        if(selected == null) return;
        selected = uaaService.loadUserWithUserRoles(selected.getId());
        
        allRoles = uaaService.getSysRoleNames();
        
        assignedRoles = new ArrayList<String>();
        for(UserRole userRole: selected.getUserRoleList()){
            assignedRoles.add(userRole.getRole().getRoleDesc());
        }
    }

    public void saveRoles() {
        if(selected == null) return;
        
        uaaService.setUserRoles(selected, assignedRoles);
        WebUtil.addSuccessMessage(WebUtil.getMessage("UserRole") + WebUtil.getMessage("Updated"));
    }
 
    private OrgInfo selectedOrg = null;
    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }
 
    private TreeNode orgTree;
    public TreeNode getOrgTree() {
        return orgTree;
    }
    private TreeNode selectedNode;
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void onSelectTreeNode(NodeSelectEvent event){
        TreeNode node = event.getTreeNode();
        //node.setExpanded(!node.isExpanded());

        selectedOrg = (OrgInfo)node.getData();
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException{
        super.prepareCreate();
        
        selected.setPlainPassword("123456");
        try {
            selected.entryptPassword();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    @Override
    public void onAfterNewObject(UserAccount userAccount,boolean isOk ){
    	if(isOk && this.crudActionName.equals("Create")){
    		List<String> role = new ArrayList<String>();
    		role.add(DEFAULT_USER_ROLE);
    		uaaService.setUserRoles(selected, role);
    	}
    }
    
    @Override
    public void onBeforeSave(UserAccount user){
        //remove space charactors.
        String loginName = user.getLoginName();
        if(loginName==null) return;
        
        loginName = loginName.replace(" ", "");
        user.setLoginName(loginName);
        
        if(selected.getOrgInfoId()!=null){
            for(OrgInfo org: orgList){
                if(org.getId().equals(selected.getOrgInfoId())){
                    selected.setHospitalId(org.getHospitalId());
                    selected.setSiteId(org.getSiteId());
                }                    
            }
        }
        else{
            selected.setSiteId(selectedOrg.getSiteId());
            selected.setHospitalId(selectedOrg.getHospitalId());
            selected.setOrgInfoId(selectedOrg.getId());
        }
    }
    
    public void resetPassword() {
        selected.setPlainPassword("123456");
        try {
            selected.entryptPassword();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dao.save(selected);
    }
    
}
