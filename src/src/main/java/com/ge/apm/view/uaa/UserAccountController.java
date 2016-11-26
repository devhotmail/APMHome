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
import java.util.ArrayList;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class UserAccountController extends JpaCRUDController<UserAccount> {

    UserAccountRepository dao = null;
    UaaService uaaService = WebUtil.getBean(UaaService.class);

    @Override
    protected void init() {
        uaaService = WebUtil.getBean(UaaService.class);
        dao = WebUtil.getBean(UserAccountRepository.class);

        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        setSiteId(loginUser.getSiteId());
        
        orgTree = buildOrgTree(UserContextService.getSiteId(), loginUser.getOrgInfoId());
        try{
            DefaultTreeNode selectedNode = (DefaultTreeNode)orgTree.getData();
            this.selectedOrg = (OrgInfo) selectedNode.getData();
        }
        catch(Exception ex){
        }
    }
    private int siteId;
    public int getSiteId() {
        return siteId;
    }
    public void setSiteId(int siteId) {
        this.siteId = siteId;
        SiteInfoRepository siteDao = WebUtil.getBean(SiteInfoRepository.class);
        selectedSite = siteDao.findById(siteId);
        buildOrgTree(siteId, null);
    }
    
    private SiteInfo selectedSite;
    public SiteInfo getSelectedSite() {
        return selectedSite;
    }
    
    public TreeNode buildOrgTree(int siteId, Integer selectedOrgId){
        orgTree = uaaService.getFullOrgTreeBySiteId(siteId, selectedOrgId);
        try{
            selectedOrg = null;
            DefaultTreeNode selectedNode = (DefaultTreeNode)orgTree.getData();
            selectedOrg = (OrgInfo) selectedNode.getData();
        }
        catch(Exception ex){
        }
        if(selectedOrg==null)
            selectedOrg = (OrgInfo)((DefaultTreeNode)orgTree.getChildren().get(0)).getData();
        
        return orgTree;
    }    
    @Override
    protected UserAccountRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<UserAccount> loadData(PageRequest pageRequest) {
        if ( selectedOrg == null) {
            return dao.getBySiteId(pageRequest, UserContextService.getSiteId());
        } else {
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
        
        selected.setSiteId(selectedOrg.getSiteId());
        selected.setHospitalId(selectedOrg.getHospitalId());
        selected.setOrgInfoId(selectedOrg.getId());
        selected.setPassword("123456");
    }
}