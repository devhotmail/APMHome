package com.ge.apm.view.i18n;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.ProcedureNameMapingRepository;
import com.ge.apm.domain.ApmProcedure;
import com.ge.apm.domain.ProcedureNameMaping;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class ProcedureNameMapingController extends JpaCRUDController<ProcedureNameMaping> {

	private static final long serialVersionUID = 1L;
	private ProcedureNameMapingRepository dao = null;
    private static  List<ApmProcedure> apList = new ArrayList<ApmProcedure>(6);
    private UserAccount user;
    
    static {
		for(int i = 1 ; i < 7 ; i++){
	   		ApmProcedure ap = new ApmProcedure();
	   		ap.setApmProcedureId(i);
	   		if(i == 1){
	   			ap.setApmProcedureName("头部");
	   		}else if(i == 2){
	   			ap.setApmProcedureName("胸部");
	   		}else if(i == 3){
	   			ap.setApmProcedureName("腹部");
	   		}else if(i == 4){
	   			ap.setApmProcedureName("四肢");
	   		}else if(i == 5){
	   			ap.setApmProcedureName("其他");
	   		}else if(i == 6){
	   			ap.setApmProcedureName("颈部");
	   		}
	   		apList.add(ap);
			}
    }
    
    @Override
    protected void init() {
    	this.filterBySite = true;
    	this.filterByHospital = true;
        dao = WebUtil.getBean(ProcedureNameMapingRepository.class);
        user = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected ProcedureNameMapingRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ProcedureNameMaping> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<ProcedureNameMaping> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

	public List<ApmProcedure> getApList() {
		return apList;
	}

/*	public void setApList(List<ApmProcedure> apList) {
		ProcedureNameMapingController.apList = apList;
	}*/
	
	public static List<ApmProcedure> initList(){
		 List<ApmProcedure> aps = new ArrayList<ApmProcedure>(6);
		for(int i = 1 ; i < 7 ; i++){
    		ApmProcedure ap = new ApmProcedure();
    		ap.setApmProcedureId(i);
    		if(i == 1){
    			ap.setApmProcedureName("头部");
    		}else if(i == 2){
    			ap.setApmProcedureName("胸部");
    		}else if(i == 3){
    			ap.setApmProcedureName("腹部");
    		}else if(i == 4){
    			ap.setApmProcedureName("四肢");
    		}else if(i == 5){
    			ap.setApmProcedureName("其他");
    		}else if(i == 6){
    			ap.setApmProcedureName("颈部");
    		}
    		aps.add(ap);
			}
		return aps;
		}
	
	@Override
	public void onBeforeNewObject(ProcedureNameMaping pnm){
		pnm.setHospitalId(user.getHospitalId());
		pnm.setSiteId(user.getSiteId());
		super.onBeforeNewObject(pnm);
	}
	
	public void setAPI(){
		if(!CollectionUtils.isEmpty(apList)){
			for (ApmProcedure ap : apList) {
				if(selected.getApmProcedureName().equals(ap.getApmProcedureName())){
					selected.setApmProcedureId(ap.getApmProcedureId());
				}
			}
		}
	}

/*
    @Override
    public void onBeforeNewObject(ProcedureNameMaping object) {
    }
    
    @Override
    public void onAfterNewObject(ProcedureNameMaping object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(ProcedureNameMaping object) {
    }
    
    @Override
    public void onAfterUpdateObject(ProcedureNameMaping object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(ProcedureNameMaping object) {
    }
    
    @Override
    public void onAfterDeleteObject(ProcedureNameMaping object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(ProcedureNameMaping object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
    
    
}