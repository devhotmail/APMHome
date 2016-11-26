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
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InspectionChecklist;
import com.ge.apm.domain.InspectionOrder;
import com.ge.apm.domain.InspectionOrderDetail;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

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
    InspectionChecklistRepository checkListDao;
    @Autowired
    InspectionOrderDetailRepository detailDao;
    @Autowired
    UserAccountRepository userDao;

    public UserAccount getUserAccountById(Integer id) {
        return userDao.getById(id);
    }

    public boolean deleteOrder(InspectionOrder order) {
        List<InspectionOrderDetail> details = detailDao.findByOrderId(order.getId());
        for (InspectionOrderDetail item : details) {
            detailDao.delete(item);
        }
        orderDao.delete(order);
        return true;
    }

    public boolean createOrder(InspectionOrder order, int orderType, List<Object[]> checkItemList) {

        List<InspectionOrderDetail> detailList = new ArrayList();
        for (Object[] item : checkItemList) {
            InspectionOrderDetail detailitem = new InspectionOrderDetail();
            detailitem.setAssetId(((AssetInfo) item[1]).getId());
            detailitem.setAssetName(((AssetInfo) item[1]).getName());
            detailitem.setDeptId(((OrgInfo) item[2]).getId());
            detailitem.setDeptName(((OrgInfo) item[2]).getName());
            detailitem.setIsPassed(false);
            detailitem.setItemId(((InspectionChecklist) item[0]).getId());
            detailitem.setItemName(((InspectionChecklist) item[0]).getItem());
            detailitem.setSiteId(order.getSiteId());
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
            detailDao.save(detailList);

        }

        return true;
    }

    public List<InspectionChecklist> getCheckItemList(Integer assetId, Integer type) {

        List<SearchFilter> searchFilters = new ArrayList();
        searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, UserContextService.getSiteId()));
        searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, assetId));
        searchFilters.add(new SearchFilter("checklistType", SearchFilter.Operator.EQ, type));
        return checkListDao.findBySearchFilter(searchFilters);
    }

    public List<InspectionOrderDetail> getDetailList(Integer id) {
        List<?> list = detailDao.searchByOrder(id);
        List<InspectionOrderDetail> detailList = new ArrayList();
        for (Object item : list) {
            Object[] objectArray = (Object[]) item;
            detailList.add((InspectionOrderDetail) objectArray[0]);
        }
        return detailList;
    }

    public void excuteOrder(InspectionOrder selected, List<InspectionOrderDetail> orderDetailItemList) {
        detailDao.save(orderDetailItemList);
        orderDao.save(selected);
    }

    public void updateOrder(InspectionOrder selected, List<InspectionOrderDetail> orderDetailItemList) {

        for (InspectionOrderDetail item : orderDetailItemList) {
            item.setDeptId(selected.getOwnerOrgId());
            item.setDeptName(selected.getOwnerOrgName());
            detailDao.save(item);
        }
        orderDao.save(selected);
    }

    public TreeNode getInspectionOrderDetailAsTree(int orderId, boolean expandAll) {
        List<InspectionOrderDetail> orderDetail = detailDao.findByOrderId(orderId);
        Map<Integer, TreeNode> deptNodes = new HashMap();
        Map<Integer, TreeNode> assetNodes = new HashMap();

        DefaultTreeNode root = new DefaultTreeNode("Root", null);

        for (InspectionOrderDetail item : orderDetail) {
            TreeNode deptNode = deptNodes.get(item.getDeptId());
            if (deptNode == null) {
                deptNode = new DefaultTreeNode("org",item.getDeptName(), root);
                deptNodes.put(item.getDeptId(), deptNode);

                deptNode.setExpanded(expandAll);
            }

            TreeNode assetNode = assetNodes.get(item.getAssetId());
            if (assetNode == null) {
                assetNode = new DefaultTreeNode("asset",item.getAssetName(), deptNode);
                assetNodes.put(item.getAssetId(), assetNode);

                assetNode.setExpanded(expandAll);
            }

            TreeNode node = new DefaultTreeNode("checklist",item, assetNode);
        }

        return root;
    }

    public TreeNode getPlanTree(Integer orderType) {
        UaaService uuaService = WebUtil.getBean(UaaService.class);
        TreeNode planTree = uuaService.getOrgAssetTree(UserContextService.getCurrentUserAccount().getHospitalId());
        addCheckListItem(planTree, orderType);
        return planTree;
    }

    private void addCheckListItem(TreeNode node, Integer type) {

        Object nodeData = node.getData();
        if (nodeData instanceof AssetInfo) {

            List<InspectionChecklist> itemList = getCheckItemList(((AssetInfo) nodeData).getId(), type);
            for (InspectionChecklist item : itemList) {
                new DefaultTreeNode("checklist", item, node);
            }
        } else {
            for (TreeNode item : node.getChildren()) {
                addCheckListItem(item, type);
            }
        }
    }
}
