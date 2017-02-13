/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.sysutil;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
public class OrgTreeService {

    OrgInfoRepository dao = null;

    private TreeNode root;

    UserAccount currentUser;

    private OrgInfo selectedOrg;

/*    
    @PostConstruct
    public void init() {
        dao = WebUtil.getBean(OrgInfoRepository.class);
        root = new DefaultTreeNode("Root", null);
        currentUser = UserContextService.getCurrentUserAccount();
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, currentUser.getHospitalId()));
        List<OrgInfo> orgRootList = dao.findBySearchFilter(searchFilters);

        Map<Integer, TreeNode> treeMap = new HashMap();
        for (OrgInfo item : orgRootList) {
            TreeNode newnode;
            if (treeMap.containsKey(item.getId())) {
                newnode = treeMap.get(item.getId());
            } else {
                newnode = new DefaultTreeNode(item);
            }

            if (null == item.getParentOrg()) {
                root.getChildren().add(newnode);
            } else if (treeMap.containsKey(item.getParentOrg().getId())) {
                treeMap.get(item.getParentOrg().getId()).getChildren().add(newnode);
            } else {
                TreeNode pnode = new DefaultTreeNode(item.getParentOrg());
                treeMap.put(item.getParentOrg().getId(), pnode);
                pnode.getChildren().add(newnode);
            }
        }
    }
*/

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public void onNodeSelect(NodeSelectEvent event) {
        selectedOrg = (OrgInfo) event.getTreeNode().getData();
    }

/*
    public TreeNode getRoot() {
        return root;
    }
*/
    
    public TreeNode getOrgTree(){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        return uaaService.getOrgTree(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    public TreeNode getOrgAssetTree(){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        return uaaService.getOrgAssetTree(UserContextService.getCurrentUserAccount().getHospitalId());
    }
    
}
