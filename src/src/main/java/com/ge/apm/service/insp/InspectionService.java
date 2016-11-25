/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.insp;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.InspectionChecklistRepository;
import com.ge.apm.dao.InspectionOrderDetailRepository;
import com.ge.apm.dao.InspectionOrderRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.InspectionChecklist;
import com.ge.apm.domain.InspectionOrder;
import com.ge.apm.domain.InspectionOrderDetail;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

/**
 *
 * @author 212579464
 */
@Component
public class InspectionService {
    @Autowired
    AssetInfoRepository assetInfoDao;
    @Autowired
    InspectionOrderRepository orderDao;
    @Autowired
    InspectionChecklistRepository checkListdao;
    @Autowired
    InspectionOrderDetailRepository detailDao;
    @Autowired
    UserAccountRepository userDao;
    
    public UserAccount getUserAccountById(Integer id){
       return userDao.getById(id);
    }
    
    public boolean deleteOrder(InspectionOrder order){
//        detaildao.deleteInspectionOrderDetails(order.getId());
        List<InspectionOrderDetail> details = detailDao.findByOrderId(order.getId());
        for(InspectionOrderDetail item: details){
            detailDao.delete(item);
        }
        orderDao.delete(order);
        return true;
    }
    
    public boolean createOrder(InspectionOrder order, int orderType) {
        return false;
/*
        AssetInfo asset = assetInfoDao.findById(order.getAssetId());

        List<InspectionChecklist> itemList = getCheckItemList(order.getAssetId(), order.getOrderType());
        List<InspectionOrderDetail> detailList = new ArrayList();
        for (InspectionChecklist item : itemList) {
            InspectionOrderDetail detailitem = new InspectionOrderDetail();
            detailitem.setSiteId(order.getSiteId());
            detailitem.setItemId(item.getId());
            detailitem.setDeptId(asset.getClinicalDeptId());
            detailitem.setDeptName(asset.getClinicalDeptName());
            detailitem.setItemName(item.getItem());
            detailitem.setIsPassed(false);
            detailList.add(detailitem);
        }

        Calendar calender = Calendar.getInstance();
        Date startDate = order.getStartTime();
        Date endDate = order.getEndTime();
        Date itemstartDate = startDate;
        Date itemendDate = startDate;
        while (itemendDate.before(endDate)) {
            calender.setTime(itemendDate);
            switch (orderType) {
                case 1:
                    calender.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                case 2:
                    calender.add(Calendar.WEEK_OF_YEAR, 1);
                    break;
                case 3:
                    calender.add(Calendar.WEEK_OF_YEAR, 2);
                    break;
                case 4:
                    calender.add(Calendar.MONTH, 1);
                    break;
                case 5:
                    calender.add(Calendar.MONTH, 3);
                    break;
            }
            order.setStartTime(itemstartDate);
            itemendDate = calender.getTime();
            itemstartDate = itemendDate;
            order.setEndTime(itemendDate);
            order.setId(null);
            order = orderDao.save(order);

            for (InspectionOrderDetail item : detailList) {
                item.setId(null);
                item.setOrderId(order.getId());
            }
            detaildao.save(detailList);

        }

        return true;
*/
    }

    public List<InspectionChecklist> getCheckItemList(Integer assetId, Integer type) {

        List<SearchFilter> searchFilters = new ArrayList();
        searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, UserContextService.getSiteId()));
        searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, assetId));
        searchFilters.add(new SearchFilter("checklistType", SearchFilter.Operator.EQ, type));
        return checkListdao.findBySearchFilter(searchFilters);
    }

    public List<InspectionOrderDetail> getDetailList(Integer id) {
        List<?> list = detailDao.searchByOrder(id);
        List<InspectionOrderDetail> detailList = new ArrayList();
        for(Object item :list){
            Object[] objectArray = (Object[])item;
            detailList.add((InspectionOrderDetail)objectArray[0]);
        }
        return detailList;
    }

    public void excuteOrder(InspectionOrder selected, List<InspectionOrderDetail> orderDetailItemList) {
        
        for(InspectionOrderDetail item: orderDetailItemList){
            detailDao.save(item);
        }
        orderDao.save(selected);
    }
    
    public void updateOrder(InspectionOrder selected, List<InspectionOrderDetail> orderDetailItemList) {
        
        for(InspectionOrderDetail item: orderDetailItemList){
            item.setDeptId(selected.getOwnerOrgId());
            item.setDeptName(selected.getOwnerOrgName());
            detailDao.save(item);
        }
        orderDao.save(selected);
    }

    public TreeNode getInspectionOrderDetailAsTree(int orderId, boolean expandAll){
        List<InspectionOrderDetail> orderDetail = detailDao.findByOrderId(orderId);
        Map<Integer, TreeNode> deptNodes = new HashMap();
        Map<Integer, TreeNode> assetNodes = new HashMap();

        DefaultTreeNode root = new DefaultTreeNode("Root", null);
        
        for(InspectionOrderDetail item: orderDetail){
            TreeNode deptNode = deptNodes.get(item.getDeptId());
            if(deptNode==null){
                deptNode = new DefaultTreeNode(item.getDeptName(), root);
                deptNodes.put(item.getDeptId(), deptNode);

                deptNode.setExpanded(expandAll);
            }
            
            TreeNode assetNode = assetNodes.get(item.getAssetId());
            if(assetNode==null){
                assetNode = new DefaultTreeNode(item.getAssetName(), deptNode);
                assetNodes.put(item.getAssetId(), assetNode);

                assetNode.setExpanded(expandAll);
            }
            
            TreeNode node = new DefaultTreeNode(item.getItemName(), assetNode);
        }

        return root;
    }
}
