package com.ge.apm.view.asset;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.cxf.common.util.CollectionUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class AssetCheckController extends JpaCRUDController<AssetInfo>{
	private static final long serialVersionUID = 1L;
	private TreeNode deviceNode;
    private UserAccount currentUser;
    private List<TreeNode> unValidDeviceList;
    private Date lastStockTakeDate;
    private AssetInfoRepository assetInfoDao = null;
    private List<TreeNode> assetTreeNodes ;
    private UaaService uuaService;
    
    @Override
    protected void init() {
    	uuaService = WebUtil.getBean(UaaService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        deviceNode = initDeviceNode();
        unValidDeviceList = new ArrayList<TreeNode>();
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        assetTreeNodes = toNodeList(deviceNode);
    }
    
	@Override
	protected GenericRepository<AssetInfo> getDAO() {
		return assetInfoDao;
	}
	
    /***
     * put all asset node in list
     * @param treeNode
     * @return
     */
    private List<TreeNode> toNodeList(TreeNode treeNode) {
    	List<TreeNode> nodes = new ArrayList<TreeNode>();
		//if(treeNode.getType().equals("default")){
    	logger.warn("1current node type is "+ treeNode.getType());
    	logger.warn("1current rowkey is "+ treeNode.getRowKey());
			List<TreeNode> orgNodes = treeNode.getChildren();
			if(!CollectionUtils.isEmpty(orgNodes)){
				for (TreeNode orgNode : orgNodes) {
					logger.warn("2current node type is "+ orgNode.getType());
					logger.warn("2current rowkey is "+ orgNode.getRowKey());
					if(orgNode.getType().equals("org")){
						List<TreeNode> assetNodes = orgNode.getChildren();
						if(!CollectionUtils.isEmpty(assetNodes)){
							for (TreeNode assetNode : assetNodes) {
								logger.warn("3current node type is "+ assetNode.getType());
								logger.warn("3current rowkey is "+ assetNode.getRowKey());
								nodes.add(assetNode);
							}
						}
					}
				}
			}
		//}
		return nodes;
	}
    
    /**
     *  
     * @param node
     * @param isSelected
     * @return
     */
    public void recursion(TreeNode node,boolean isSelected){
    	if(node == null || node.isLeaf()){
    		return ;
    	}
    	if(!CollectionUtils.isEmpty(node.getChildren())){
    		List<TreeNode> childNodes = node.getChildren();
    		for (TreeNode treeNode : childNodes) {
				if(isSelected){
					treeNode.setSelected(true);
					recursion(treeNode,true);
				}
			}
    	}
    }
    
    public void onSelectDeviceTreeNode(NodeSelectEvent event){
    	TreeNode selectedNode = event.getTreeNode();
    	if(selectedNode.getType().equals("org")){
    		if(selectedNode.getChildCount() > 0){
    			List<TreeNode> childNode = selectedNode.getChildren();
    			for (TreeNode treeNode : childNode) {
    	        	if(unValidDeviceList.contains(treeNode)){
    	        		unValidDeviceList.remove(treeNode);
    	        	}
    				recursion(treeNode,true);
				}
    		}
    	}else if(selectedNode.getType().equals("asset")){
        	if(unValidDeviceList.contains(selectedNode)){
        		unValidDeviceList.remove(selectedNode);
        	}
    	}
    	selectedNode.setSelected(true);
    }
    
    public void onUnSelectDeviceTreeNode(NodeUnselectEvent event){
    	TreeNode unSelectedNode = event.getTreeNode();
    	if(unSelectedNode.getType().equals("org")){
    		if(unSelectedNode.getChildCount() > 0){
    			List<TreeNode> chlid = unSelectedNode.getChildren();
    			for (TreeNode treeNode : chlid) {
    				unValidDeviceList.add(treeNode);
    				treeNode.setSelected(false);
				}
    		}
    	}else if(unSelectedNode.getType().equals("asset")){
    		unValidDeviceList.add(unSelectedNode);
    		unSelectedNode.setSelected(false);
    	}
    }
    
    public void deviceCheck(Date lastStocktakeDate,List<TreeNode> unValidDevice){
    		updateDeviceCheck(unValidDevice,lastStocktakeDate,false);
    		updateDeviceCheck((List<TreeNode>) CollectionUtils.diff(this.assetTreeNodes, unValidDevice),lastStocktakeDate,true);
    }
    
    public void updateDeviceCheck(List<TreeNode> treeNodes , Date date,boolean isValid){
    	if(!CollectionUtils.isEmpty(treeNodes)){
    		if(date == null){
    			date = new Date();
    		}
    		for (TreeNode treeNode : treeNodes) {
				AssetInfo assetInfo = (AssetInfo) treeNode.getData();
				assetInfo.setIsValid(isValid);
				assetInfo.setLastStockTakeDate(date);
				assetInfoDao.save(assetInfo);
			}
    	}
    
    }
    
	public TreeNode initDeviceNode(){
    	TreeNode deviceNode = uuaService.getOrgAssetTree(this.currentUser.getHospitalId());
    	if(deviceNode != null && deviceNode.getChildCount() > 0){
    		recursion(deviceNode,true);
    	}
    	return deviceNode;
    }
	
	  public void removeSelectedAssetDevice(TreeNode node) {
		  if(unValidDeviceList.contains(node)){
			  unValidDeviceList.remove(node);
		  }
		  node.setSelected(true);
	  }

	public TreeNode getDeviceNode() {
		return deviceNode;
	}

	public void setDeviceNode(TreeNode deviceNode) {
		this.deviceNode = deviceNode;
	}

	public List<TreeNode> getUnValidDeviceList() {
		return unValidDeviceList;
	}

	public void setUnValidDeviceList(List<TreeNode> unValidDeviceList) {
		this.unValidDeviceList = unValidDeviceList;
	}

	public Date getLastStockTakeDate() {
		return lastStockTakeDate;
	}

	public void setLastStockTakeDate(Date lastStockTakeDate) {
		this.lastStockTakeDate = lastStockTakeDate;
	}

	public List<TreeNode> getAssetTreeNodes() {
		return assetTreeNodes;
	}

	public void setAssetTreeNodes(List<TreeNode> assetTreeNodes) {
		this.assetTreeNodes = assetTreeNodes;
	}

	public UaaService getUuaService() {
		return uuaService;
	}

	public void setUuaService(UaaService uuaService) {
		this.uuaService = uuaService;
	}
    
	
}
