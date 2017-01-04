package com.ge.apm.view.i18n;

import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.List;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.service.DbMessageSource;

@ManagedBean
@ViewScoped
public class I18nMessageController extends JpaCRUDController<I18nMessage> {

    private static final long serialVersionUID = 1L;
    private I18nMessageRepository dao = null;
    private UserAccount currentUser = null;
    private String fieldCodeType;
	
    @Override
    protected void init() {
        dao = WebUtil.getBean(I18nMessageRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        if (searchFilters == null) searchFilters = new ArrayList<> ();
        searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, -1));
    }

    @Override
    protected Page<I18nMessage> loadData(PageRequest pageRequest) {
        this.selected = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }
    
    @Override 
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        addFilterParams();
    } 
    
    @Override
    public void delete(){
    	super.delete();
    }
    
/*    
    @Override
    public void save(){
        //判断key是否为唯一
        List<I18nMessage> i18n = dao.getByMsgTypeAndSiteIdAndMsgKey(selected.getMsgType(), selected.getSiteId(), selected.getMsgKey());
        if (i18n != null && i18n.size() > 0) {
            WebUtil.addErrorMessage("编码值重复。");
            return;
        }
    	super.save();
    }
*/
    
    @Override
    public void prepareEdit(){
    	super.prepareEdit();
    }

   public void prepareCreate() throws InstantiationException, IllegalAccessException{
        this.selected = null;
        super.prepareCreate();
        this.selected.setSiteId(this.currentUser.getSiteId());
        this.selected.setMsgType(fieldCodeType);
   }
   
    public void onSelect() {
        if (searchFilters == null) searchFilters = new ArrayList<> ();
        searchFilters.clear();
        addFilterParams();
    }
    private void addFilterParams() {
        searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, currentUser.getSiteId()));
        if (fieldCodeType != null && !"".contains(fieldCodeType))
            searchFilters.add(new SearchFilter("msgType", SearchFilter.Operator.EQ, fieldCodeType));
        this.selected = null;
    }
	
    @Override
    protected I18nMessageRepository getDAO() {
        return dao;
    }
   
    public String getFieldCodeType() {
            return fieldCodeType;
    }

    public void setFieldCodeType(String fieldCodeType) {
            this.fieldCodeType = fieldCodeType;
    }

    public UserAccount getCurrentUser() {
            return currentUser;
    }

    public void setCurrentUser(UserAccount currentUser) {
            this.currentUser = currentUser;
    }

    @Override
    public void onAfterDataChanged() {
        DbMessageSource.reLoadMessages();
    }
    
    
}