package com.ge.apm.view.wo;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkOrderStep;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.wo.WorkOrderService;
import java.util.ArrayList;
import javax.faces.bean.ManagedProperty;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class WorkOrderStepController extends JpaCRUDController<WorkOrderStep> {

    WorkOrderStepRepository dao = null;
    
    @ManagedProperty("#{attachmentFileService}")
    private AttachmentFileService fileService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(WorkOrderStepRepository.class);
    }

    @Override
    protected WorkOrderStepRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<WorkOrderStep> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) 
            searchFilters = new ArrayList<SearchFilter>();

        searchFilters.clear();
        
        if(workOrderFilter!=null)
            searchFilters.add(workOrderFilter);

        return dao.findBySearchFilter(this.searchFilters, pageRequest);

    }

    List<WorkOrderStep> workOrderStepList;
    public List<WorkOrderStep> getWorkOrderStepList() {
        return workOrderStepList;
    }
    
    public  void loadWorkOrderSteps(int workId) {
        workOrderStepList = dao.getByWorkOrderIdOrderByIdAsc(workId);
    }

    private SearchFilter workOrderFilter;
    public void setWorkOrderIdFilter(int workOrderId){
        workOrderFilter = new SearchFilter("workOrderId", SearchFilter.Operator.EQ, workOrderId);
    }
    
    public boolean getHasStepDetails(){
        return (this.selected!=null) && (this.selected.getStepDetails()!=null) && (!this.selected.getStepDetails().isEmpty());
    }
    
    private WorkOrder wo;
    public void setSelectedByWorkOrder(WorkOrder wo){
        this.selected = null;
        this.wo = wo;
        if(wo!=null){
            List<WorkOrderStep> woStepList = null;
            if(wo.getId()!=null)
                 woStepList = dao.getByWorkOrderIdAndStepId(wo.getId(), wo.getCurrentStep());

            if( (woStepList==null) || woStepList.isEmpty()){
                WorkOrderService woService = WebUtil.getBean(WorkOrderService.class);
                selected = woService.initWorkOrderCurrentStep(wo);
                
                if(wo.getId()!=null)
                    try{
                        woService.saveWorkOrderStep(wo, selected, null);
                    }
                    catch(Exception ex){
                        logger.error(ex.getMessage(), ex);
                        WebUtil.addErrorMessageKey("PersistenceErrorOccured");
                    }
            }
            else{
                this.selected = woStepList.get(0);
            }
        }
    }

    public void createWorkOrderStep(){
        WorkOrderService woService = WebUtil.getBean(WorkOrderService.class);
        try{
            woService.createWorkOrderStep(wo, this.selected);
            WebUtil.navigateTo("woSaved");
        }
        catch(Exception ex){
            logger.error(ex.getMessage(), ex);
            WebUtil.addErrorMessageKey("PersistenceErrorOccured");
        }
    }

    public void finishWorkOrderStep(){
        WorkOrderService woService = WebUtil.getBean(WorkOrderService.class);
        try{
            woService.finishWorkOrderStep(wo, this.selected);
            WebUtil.navigateTo("woSaved");
        }
        catch(Exception ex){
            logger.error(ex.getMessage(), ex);
            WebUtil.addErrorMessageKey("PersistenceErrorOccured");
        }
    }
    
    public void closeWorkOrder(){
        WorkOrderService woService = WebUtil.getBean(WorkOrderService.class);
        try{
            woService.closeWorkOrder(wo, this.selected);
            WebUtil.navigateTo("woSaved");
        }
        catch(Exception ex){
            logger.error(ex.getMessage(), ex);
            WebUtil.addErrorMessageKey("PersistenceErrorOccured");
        }
    }

    public void transferWorkOrder(){
        WorkOrderService woService = WebUtil.getBean(WorkOrderService.class);
         try{
            woService.transferWorkOrder(wo, this.selected);
             WebUtil.navigateTo("woSaved");
        }
        catch(Exception ex){
            logger.error(ex.getMessage(), ex);
            WebUtil.addErrorMessageKey("PersistenceErrorOccured");
        }
   }
    
    public void setFileService(AttachmentFileService fileService) {
        this.fileService = fileService;
    }
    
    public List<WorkOrderStep> getAttachList(Integer orderStepId) {
        if (orderStepId == null) return null;
        WorkOrderStep step = dao.findById(orderStepId);

        List<WorkOrderStep> result = new ArrayList();
        if (step != null && step.getFileId() != null) {
            result.add(step);
        }
        return result;
    }
    
    public void removeAttachment(Integer attachid) {
        if (attachid == null) return;
        fileService.deleteAttachment(attachid);
        WorkOrderStep step = dao.findById(this.selected.getId());
        step.setFileId(null);
        step.setAttachmentUrl(null);
        dao.save(step);
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        Integer uploadFileId = fileService.uploadFile(event.getFile());
        String fileName = fileService.getFileName(event.getFile());
        WorkOrderStep step = dao.findById(this.selected.getId());
        if (step == null) return;
        //如果有文件则删除以前的文件
        if (step.getFileId() != null) {
            fileService.deleteAttachment(step.getFileId());
        }
        step.setAttachmentUrl(fileName);
        if (uploadFileId > 0) {
            step.setFileId(uploadFileId);
            WebUtil.addSuccessMessage("文件上传成功。", fileName + " is uploaded.");
        }
        dao.save(step);
    }
    
}