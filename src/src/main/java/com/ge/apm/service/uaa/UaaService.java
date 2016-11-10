package com.ge.apm.service.uaa;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserRoleRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserRole;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212547631
 */
@Component
public class UaaService {
    private static final Logger logger = Logger.getLogger(UaaService.class);
    
    private static Map<String, SysRole> sysRoles;

    private void loadSysRoles(){
        if(sysRoles!=null) return;
            
        SysRoleRepository sysRoleDao = WebUtil.getBean(SysRoleRepository.class);
        List<SysRole> sysRoleList = sysRoleDao.find();

        sysRoles = new HashMap<String, SysRole>();
        for(SysRole sysRole: sysRoleList){
            sysRoles.put(sysRole.getName(), sysRole);
        }
    }
    
    public String getUserDefaultHomePage(UserAccount user){
        String url;
        UserContextService userContextService = (UserContextService) WebUtil.getBean(UserContextService.class);
        if(userContextService.hasRole("HospitalHead"))
            url = getDefautHomePage("HospitalHead");
        else if(userContextService.hasRole("AssetHead"))
            url = getDefautHomePage("AssetHead");
        else if(userContextService.hasRole("DeptHead"))
            url = getDefautHomePage("DeptHead");
        else if(userContextService.hasRole("DeptStuff"))
            url = getDefautHomePage("DeptStuff");
        else
            url = "/home.xhtml";
        
        return url;
    }
    
    private String getDefautHomePage(String roleName){
        loadSysRoles();
        
        SysRole sysRole = sysRoles.get(roleName);
        if(sysRole!=null)
            return sysRole.getHomePage();
        else
            return "/home.xhtml";
    }
    
    public List<SysRole> getSysRoles(){
        List<SysRole> roleList = new ArrayList<SysRole>();
        roleList.addAll(sysRoles.values());

        return roleList;
    }

    public List<String> getSysRoleNames(){
        List<String> roleList = new ArrayList<String>();

        for(Map.Entry<String, SysRole> item: sysRoles.entrySet()){
            roleList.add(item.getValue().getRoleDesc());
        }

        return roleList;
    }
    
    @Transactional
    public void setUserRoles(UserAccount userAccount, List<String> newRoleNames){
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);

        //first remove all existing user roles
        UserRoleRepository userRoleDao = WebUtil.getBean(UserRoleRepository.class);
        userRoleDao.deleteByUserAccount(userAccount);
        
        //then set user roles
        SysRoleRepository sysRoleDao = WebUtil.getBean(SysRoleRepository.class);
        for(String newRoleName: newRoleNames){
            SysRole role = sysRoleDao.getByRoleDesc(newRoleName);
            if (role==null){
                logger.warn("Assign role to user: Role not found, roleName="+newRoleName);
            }
            else {
                UserRole userRole = new UserRole();
                userRole.setUserId(userAccount.getId());
                userRole.setRoleId(role.getId());
                userRoleDao.save(userRole);
            }
        }
    }
    
    public TreeNode getOrgTree(int hospitalId){
        OrgInfoRepository orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        List<OrgInfo> orgList = orgInfoDao.getByHospitalId(hospitalId);

        //first create all nodes
        Map<Integer, TreeNode> treeMap = new HashMap<>();
        TreeNode root = new DefaultTreeNode("Root", null);
        for(OrgInfo org: orgList){
            TreeNode node;
            node = new DefaultTreeNode("org", org, root);

            treeMap.put(org.getId(), node);
        }

        //then update node's parent by org's parentOrg
        for(Map.Entry<Integer, TreeNode> nodeItem: treeMap.entrySet()){
            TreeNode node = nodeItem.getValue();
            OrgInfo org = (OrgInfo)node.getData();
            if(org.getParentOrg()!=null){
                TreeNode parentNode = treeMap.get(org.getParentOrg().getId());
                if(parentNode!=null){
                    parentNode.getChildren().add(node);
                }
            }
        }

        return root;
    }

    private void addAssetToOrgNode(TreeNode node, Map<Integer, List<AssetInfo>> assetMap){
        if("org".equals(node.getType())){
            OrgInfo org = (OrgInfo)node.getData();
            List<AssetInfo> assets = assetMap.get(org.getId());
            
            if(assets!=null) {
                for(AssetInfo asset: assets){
                    TreeNode assetNode = new DefaultTreeNode("asset", asset, node);
                }
            }
        }
        
        for(TreeNode aNode: node.getChildren()){
            addAssetToOrgNode(aNode, assetMap);
        }
    }
    
    public TreeNode getOrgAssetTree(int hospitalId){
        AssetInfoRepository assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        List<AssetInfo> assetList = assetInfoDao.getByHospitalId(hospitalId);
        
        Map<Integer, List<AssetInfo>> assetMap = new HashMap<>();
        for(AssetInfo asset: assetList){
            List<AssetInfo> assets = assetMap.get(asset.getClinicalDeptId());

            if(assets==null){
                assets = new ArrayList<>();
                assetMap.put(asset.getClinicalDeptId(), assets);
            }
            
            assets.add(asset);
        }
        
        TreeNode root = getOrgTree(hospitalId);
        addAssetToOrgNode(root, assetMap);
        
        return root;
    }
    
    public List<UserAccount> getUserList(int hospitalId){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.getByHospitalId(hospitalId);
    }
    public List<UserAccount> getUserListWithAssetStaffRole(int hospitalId){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.getUserListByHospitalIdWithAssetStuffRole(hospitalId);
    }
    
}
