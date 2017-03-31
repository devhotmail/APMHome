package com.ge.apm.view.uaa;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SiteInfoRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SiteInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.dao.DataIntegrityViolationException;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class OrgInfoController extends JpaCRUDController<OrgInfo> {

    OrgInfoRepository dao = null;
    UaaService uaaService;

    @Override
    protected void init() {
        filterBySite = false;
        
        dao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        setSiteId(loginUser.getSiteId());
        setHospitalId(loginUser.getHospitalId());
        buildOrdTree();
    }

    @Override
    protected OrgInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<OrgInfo> loadData(PageRequest pageRequest) {
        selected = null;
        return dao.getBySiteId(pageRequest, hospitalId);
    }

    public List<OrgInfo> getHospitalDeptList() {
        return dao.getByHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    private int siteId;
    private int hospitalId;
    public int getSiteId() {
        return siteId;
    }
    public void setSiteId(int siteId) {
        this.siteId = siteId;
        SiteInfoRepository siteDao = WebUtil.getBean(SiteInfoRepository.class);
        selectedSite = siteDao.findById(siteId);
        
        this.selected = null;
        buildOrdTree();
    }
    
    private SiteInfo selectedSite;

    public SiteInfo getSelectedSite() {
        return selectedSite;
    }

    public void setSelectedSite(SiteInfo selectedSite) {
        this.selectedSite = selectedSite;
    }
    
    public int getHospitalId() {
        return hospitalId;
    }
    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
        buildOrdTree();        
    }
    
    public void buildOrdTree(){
        //orgTree = uaaService.getOrgTree(this.hospitalId, true);
        if(selected!=null)
            orgTree = uaaService.getFullOrgTreeBySiteId(this.siteId, selected.getId());
        else
            orgTree = uaaService.getFullOrgTreeBySiteId(this.siteId, null);
        
        try{
            this.selectedNode = (TreeNode)orgTree.getData();
        }
        catch(Exception ex){
        }
    }

    public List<OrgInfo> getHospitalListBySiteId(){
        return uaaService.getHospitalListBySiteId(siteId);
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
    
    public void prepareCreateHospital(){
        OrgInfo org = new OrgInfo();
        org.setSiteId(siteId);
        this.selected = org;
    }
    
    public void prepareCreateDepartment(){
        OrgInfo hospital = null;
        OrgInfo org = this.selected;
        while( org!=null) {
            hospital = org;
            org = org.getParentOrg();
        }
        
        OrgInfo newOrg = new OrgInfo();
        newOrg.setSiteId(siteId);
        newOrg.setHospitalId(hospitalId);
        newOrg.setParentOrg(hospital);
        
        this.selected = newOrg;
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
        this.siteId = selected.getSiteId();
        this.hospitalId = selected.getHospitalId();
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
}