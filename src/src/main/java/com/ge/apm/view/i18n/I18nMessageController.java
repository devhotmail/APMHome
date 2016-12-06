package com.ge.apm.view.i18n;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.FieldCodeType;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.service.DbMessageSource;

@ManagedBean
@ViewScoped
public class I18nMessageController extends JpaCRUDController<I18nMessage> {

	private static final long serialVersionUID = 1L;
	public static final Integer SYS_SITE = -1 ;
	private I18nMessageRepository dao = null;
	private UserAccount currentUser = null;
	private FieldCodeType fieldCodeType = null;
	private String msgType;
	private List<I18nMessage> sysConfig;
	private List<I18nMessage> customConfig;
	
    @Override
    protected void init() {
        dao = WebUtil.getBean(I18nMessageRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        fieldCodeType = new FieldCodeType();
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
    public void onAfterDataChanged(){
        DbMessageSource.reLoadMessages();
    };
    
    @Override
    public void delete(){
    	super.delete();
    	customConfig = getFieldValueList(this.currentUser.getSiteId());
    }
    
    @Override
    public void save(){
    	super.save();
    	customConfig = getFieldValueList(this.currentUser.getSiteId());
    }
    
    @Override
    public void prepareEdit(){
    	super.prepareEdit();
    	customConfig = getFieldValueList(this.currentUser.getSiteId());
    }


	public List<I18nMessage> getFieldValueList(Integer siteId) {
		if (siteId == null) {
			logger.error("fetch i18n message list error,siteId is null, current user is " + this.currentUser);
			return null;
		}
		List<SearchFilter> filter = new ArrayList<SearchFilter> ();
		filter.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, siteId));
		filter.add(new SearchFilter("msgType", SearchFilter.Operator.EQ, msgType));
		 List<I18nMessage> result = dao.findBySearchFilter(filter);
		if (!CollectionUtils.isEmpty(result)) {
			for (I18nMessage i18nMessage : result) {
				logger.info("fetch i18n message ,result is :" + i18nMessage);
			}
			return result;
		}
		return null;
	}
	
	public List<I18nMessage> fetchI18nMessageList(Integer siteId){
		List<SearchFilter> filter = new ArrayList<SearchFilter> ();
		filter.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, siteId));
		filter.add(new SearchFilter("msgType", SearchFilter.Operator.EQ, msgType));
		List<I18nMessage> result = dao.findBySearchFilter(filter);
		return result;
	}

   public void prepareCreate() throws InstantiationException, IllegalAccessException{
	   this.selected = null;
	   super.prepareCreate();
	   this.selected.setSiteId(this.currentUser.getSiteId());
	   this.selected.setMsgType(msgType);
	   this.selected.setMsgKey(this.currentUser.getName());
	   customConfig = getFieldValueList(this.currentUser.getSiteId());
   }
   
   public void batchImport(){
	   List<I18nMessage> result = fetchI18nMessageList(-1);
	   if(!CollectionUtils.isEmpty(result)){
		   if(logger.isInfoEnabled()){
			   logger.info("msgType is "+msgType+ "get "+result.size()+"条system记录");
		   }
		   for (I18nMessage i18nMessage : result) {
			   i18nMessage.setSiteId(this.currentUser.getSiteId());
			   i18nMessage.setMsgKey(i18nMessage.getMsgKey().concat(currentUser.getName()));
			   i18nMessage.setId(null);
			   dao.save(i18nMessage);
		}
		   customConfig = getFieldValueList(this.currentUser.getSiteId());
	   }
   }
   
   public void clearAll(){
	   List<I18nMessage> result = fetchI18nMessageList(this.currentUser.getSiteId());
	   if(!CollectionUtils.isEmpty(result)){
		   if(logger.isInfoEnabled()){
			   logger.info("msgType is "+msgType+ "get "+result.size()+"条system记录");
		   }
		   for (I18nMessage i18nMessage : result) {
			   i18nMessage.setSiteId(this.currentUser.getSiteId());
			   i18nMessage.setMsgKey(i18nMessage.getMsgKey().concat(currentUser.getName()));
			  dao.delete(i18nMessage);
		}
		   customConfig = getFieldValueList(this.currentUser.getSiteId());
	   }
   }
   
   
	public void onSelect() {
		sysConfig = getFieldValueList(SYS_SITE);
		customConfig = getFieldValueList(this.currentUser.getSiteId());
	}
	
    @Override
    protected I18nMessageRepository getDAO() {
        return dao;
    }
   
	public FieldCodeType getSelectedType() {
		return fieldCodeType;
	}

	public void setSelectedType(FieldCodeType fieldCodeType) {
		this.fieldCodeType = fieldCodeType;
	}
    
	public UserAccount getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccount currentUser) {
		this.currentUser = currentUser;
	}

	public List<I18nMessage> getSysConfig() {
		return sysConfig;
	}

	public void setSysConfig(List<I18nMessage> sysConfig) {
		this.sysConfig = sysConfig;
	}

	public List<I18nMessage> getCustomConfig() {
		return customConfig;
	}

	public void setCustomConfig(List<I18nMessage> customConfig) {
		this.customConfig = customConfig;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
    
}