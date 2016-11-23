package com.ge.apm.view.uaa;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class OrgInfoController extends JpaCRUDController<OrgInfo> {

    OrgInfoRepository dao = null;
    UaaService uaaService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        siteId = loginUser.getSiteId();
        hospitalId =  loginUser.getHospitalId();
    }

    @Override
    protected OrgInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<OrgInfo> loadData(PageRequest pageRequest) {
        return dao.getByHospitalId(pageRequest, hospitalId);
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
    }
    public int getHospitalId() {
        return hospitalId;
    }
    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }
    
    public void buildOrdTree(){
        orgTree = uaaService.getOrgAssetTree(this.hospitalId);
    }

    public List<OrgInfo> getHospitalListBySiteId(){
        return uaaService.getHospitalListBySiteId(siteId);
    }
    
    private TreeNode orgTree;
    public TreeNode getOrgTree(){
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
        node.setExpanded(true);
        
        OrgInfo selectedOrg = (OrgInfo)node.getData();
        this.siteId = selectedOrg.getSiteId();
        this.hospitalId = selectedOrg.getHospitalId();
    }
 
    @Override
    public void onBeforeNewObject(OrgInfo object) {
        object.setHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
    }
  
}