package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.WorkOrder;
import org.primefaces.event.SelectEvent;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class WorkOrderController extends JpaCRUDController<WorkOrder> {

    WorkOrderRepository dao = null;

    @Override
    protected void init() {
        this.filterByHospital = true;
        dao = WebUtil.getBean(WorkOrderRepository.class);
    }

    @Override
    protected WorkOrderRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<WorkOrder> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<WorkOrder> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    public void onSelectWorkOrder(){
        
    }
    public void onAssetSelected(SelectEvent event) {
        onServerEvent("assetSelected", event.getObject());
    }
    
    @Override
    public void onServerEvent(String eventName, Object eventObject){
        AssetInfo asset = (AssetInfo) eventObject;
        if(asset==null) return;
        
        if(this.selected==null) return;
        this.selected.setAssetId(asset.getId());
        this.selected.setAssetName(asset.getName());
        this.selected.setCaseOwnerId(asset.getAssetOwnerId());
        this.selected.setCaseOwnerName(asset.getAssetOwnerName());
    }
    
}