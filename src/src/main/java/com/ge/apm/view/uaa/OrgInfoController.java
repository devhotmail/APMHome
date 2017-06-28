package com.ge.apm.view.uaa;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.dao.DataIntegrityViolationException;
import webapp.framework.web.WebUtil;
import com.ge.apm.dao.TenantInfoRepository;
import java.util.UUID;

@ManagedBean
@ViewScoped
public class OrgInfoController extends JpaCRUDController<OrgInfo> {

    OrgInfoRepository dao = null;
    UaaService uaaService;

    private TenantInfo selectedTenant;

    @Override
    protected void init() {
        filterBySite = false;
        
        dao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        setTenantId(loginUser.getSiteId());
        buildOrdTree();
    }

    @Override
    protected OrgInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<OrgInfo> loadData(PageRequest pageRequest) {
        return dao.getBySiteId(pageRequest, tenantId);
    }

    private int tenantId;

    public int getTenantId() {
        return tenantId;
    }
    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
        TenantInfoRepository siteDao = WebUtil.getBean(TenantInfoRepository.class);
        selectedTenant = siteDao.findById(tenantId);
        
        this.selected = null;
        buildOrdTree();
    }

    public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }
    
    public void buildOrdTree(){
        if(selected!=null)
            orgTree = uaaService.getFullOrgTreeBySiteId(this.tenantId, selected.getId());
        else
            orgTree = uaaService.getFullOrgTreeBySiteId(this.tenantId, null);
        
        try{
            this.selectedNode = (TreeNode)orgTree.getData();
        }
        catch(Exception ex){
        }
    }

    public List<OrgInfo> getHospitalListBySiteId(){
        return uaaService.getHospitalListBySiteId(tenantId);
    }
    
    private DefaultTreeNode orgTree;
    public DefaultTreeNode getOrgTree(){
        return orgTree;
    }
    private TreeNode selectedNode;

    public TreeNode getSelectedNode() {
        return selectedNode;
    }
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
    
    public void prepareCreateInstitution(){
        OrgInfo org = new OrgInfo();
        org.setSiteId(tenantId);

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
       org.setInstitutionUID(org.getUid());
        org.setHospitalUID(org.getUid());
        org.setSiteUID(org.getUid());
        
        org.setOrgLevel(1);
        org.setOrgType(1);
        this.selected = org;
    }

    public void prepareCreateHospital(){
        if(selected==null) return;
        
        OrgInfo org = new OrgInfo();
        org.setParentOrg(selected);
        org.setParentUID(selected.getUid());

        org.setSiteId(selected.getSiteId());
        org.setHospitalId(selected.getHospitalId());

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
        org.setInstitutionUID(selected.getInstitutionUID());
        org.setHospitalUID(org.getUid());
        org.setSiteUID(org.getUid());
        
        org.setOrgLevel(2);
        org.setOrgType(2);
        this.selected = org;
    }
        
    public void prepareCreateSite(){
        if(selected==null) return;
        
        OrgInfo org = new OrgInfo();
        org.setParentOrg(selected);
        org.setParentUID(selected.getUid());

        org.setSiteId(selected.getSiteId());
        org.setHospitalId(selected.getHospitalId());

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
        org.setInstitutionUID(selected.getInstitutionUID());
        org.setHospitalUID(selected.getHospitalUID());
        org.setSiteUID(org.getUid());
        
        org.setOrgLevel(3);
        org.setOrgType(3);
        this.selected = org;
    }
    
    public void prepareCreateDepartment(){
        if(selected==null) return;
        
        OrgInfo org = new OrgInfo();
        org.setParentOrg(selected);
        org.setParentUID(selected.getUid());

        org.setSiteId(selected.getSiteId());
        org.setHospitalId(selected.getHospitalId());

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
        org.setInstitutionUID(selected.getInstitutionUID());
        org.setHospitalUID(selected.getHospitalUID());
        org.setSiteUID(selected.getSiteUID());
        
        org.setOrgLevel(selected.getOrgLevel());
        org.setOrgType(4);
        this.selected = org;
    }

    public void saveSelectedOrg(){
        super.save();
        initWorkflowConfig();
        
        buildOrdTree();
    }
    
    public void initWorkflowConfig(){
        if(!selected.getHospitalId().equals(selected.getId())) return;
        
        WorkflowConfigRepository wfConfigDao = WebUtil.getBean(WorkflowConfigRepository.class);
        WorkflowConfig wfConfig = wfConfigDao.findById(selected.getHospitalId());
        if(wfConfig==null){
            wfConfig = new WorkflowConfig();
            wfConfig.setHospitalId(selected.getHospitalId());
            wfConfig.setSiteId(selected.getSiteId());
            
            wfConfig.setDispatchMode(1);
            wfConfig.setTimeoutDispatch(30);
            wfConfig.setTimeoutAccept(30);
            wfConfig.setTimeoutRepair(300);  //5 hours
            wfConfig.setTimeoutClose(30);
            wfConfig.setOrderReopenTimeframe(7); //day
            wfConfig.setMaxMessageCount(2); 
            
            wfConfigDao.save(wfConfig);
        }
    }

    public void deleteSelectedOrg(){
        super.delete();
        buildOrdTree();
    }

    public void onSelectTreeNode(NodeSelectEvent event){
        TreeNode node = event.getTreeNode();
        //node.setExpanded(!node.isExpanded());
        
        selected = (OrgInfo)node.getData();
    }
 
    @Override
    public void onBeforeNewObject(OrgInfo object) {
        object.setHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    @Override
    protected boolean onPersistException(Exception ex, OrgInfo org){
        if(ex.getClass().equals(DataIntegrityViolationException.class)){
            DataIntegrityViolationException exFkey = (DataIntegrityViolationException) ex;
            if(exFkey.getRootCause().getMessage().toLowerCase().contains("foreign key constraint ")){
                String errMessage = WebUtil.getMessage("ForeighKeyErrorWithOrgId");
                WebUtil.addErrorMessage(errMessage);

                return true;
            }
        }
                
        return false;
    }
    
    public boolean getIsCreateHospitalDisabled(){
        return (selected==null) || (selected.getOrgType()==null) || (selected.getOrgType()!=1);
    }
    public boolean getIsCreateSiteDisabled(){
        return (selected==null) || (selected.getOrgType()==null) || (selected.getOrgType()!=2);
    }
    public boolean getIsCreateDepartmentDisabled(){
        return (selected==null) || (selected.getOrgType()==null) || (selected.getOrgType()==4);
    }
    
}