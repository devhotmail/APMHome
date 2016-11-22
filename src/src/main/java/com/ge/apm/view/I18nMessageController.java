package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.FieldCodeType;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.service.DbMessageSource;

@ManagedBean
@ViewScoped
public class I18nMessageController extends JpaCRUDController<I18nMessage> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(I18nMessageController.class);
	I18nMessageRepository dao = null;
    UserAccount currentUser = null;
	FieldCodeType selectedType = null;
	I18nMessage i18nMessage = null;
    @Override
    protected void init() {
    	System.out.println("=========================i18n init()");
        dao = WebUtil.getBean(I18nMessageRepository.class);
        this.filterBySite = true;
        this.filterByHospital = true;
        this.filterByLoginUser = true;
        currentUser = UserContextService.getCurrentUserAccount();
        selectedType = new FieldCodeType();
        i18nMessage =new I18nMessage();
        //this.searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, currentUser.getSiteId()));
    }

    @Override
    protected I18nMessageRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<I18nMessage> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<I18nMessage> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    @Override
    public void onAfterDataChanged(){
        DbMessageSource.reLoadMessages();
    };
    
/*   @Override
	public void onBeforeSave(I18nMessage i18nMessage) {
    	if(i18nMessage.hashCode() == 0){
    		i18nMessage.setSiteId(this.currentUser.getSiteId());
    		i18nMessage.setMsgType(this.selectedType.getMsgType());
    		i18nMessage.setMsgKey("personal");
    	}
	}*/
    
/*    @Override
	public void onBeforeNewObject(I18nMessage i18nMessage) {
		if(i18nMessage != null){
			i18nMessage.setMsgType(this.selectedType.getMsgType());
			i18nMessage.setSiteId(this.currentUser.getSiteId());
			//TODO msgKey
		}
	}*/

	/***
	 * get system config
	 * @param fieldName
	 * @return
	 */
   public List<I18nMessage> getSysFieldValueList(String fieldName){
   	// -1 stand for a config by system
	  
	   List<I18nMessage> result = FieldValueMessageController.doGetFieldValueList(fieldName, -1);
	   for (I18nMessage i18nMessage : result) {
		   System.out.println("sys query result is :"+i18nMessage) ;
	   }
       return FieldValueMessageController.doGetFieldValueList(fieldName, -1);
   }
   
	/***
	 * get personal config
	 * @param fieldName
	 * @return
	 */
   public List<I18nMessage> getCustomerFieldValueList(String fieldName){
	   List<I18nMessage> result = FieldValueMessageController.doGetFieldValueList(fieldName,currentUser.getSiteId());
	   for (I18nMessage i18nMessage : result) {
		   System.out.println("personal query result is :"+i18nMessage) ;
	   }
       return FieldValueMessageController.doGetFieldValueList(fieldName,currentUser.getSiteId());
   }
   
   public void getMsgType(String msgType) throws InstantiationException, IllegalAccessException{
	   if(this.i18nMessage.hashCode() == 0){
		  this.i18nMessage.setSiteId(this.currentUser.getSiteId());
		  this.i18nMessage.setMsgType(msgType);
		  this.i18nMessage.setMsgKey("personal");
		  this.i18nMessage.setMsgType(msgType);
   		}
   }
   
   public void onSelect(){
   	if(LOGGER.isDebugEnabled()){
   		LOGGER.debug("invoke by ajax listener : "+ selectedType);
   	}
   }
   
	public FieldCodeType getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(FieldCodeType selectedType) {
		this.selectedType = selectedType;
	}
    
    
    
}