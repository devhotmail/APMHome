package com.ge.apm.view.uaa;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.ge.apm.domain.AccountApplication;
import com.ge.apm.service.uaa.AccountApplicationService;
import com.ge.apm.service.utils.WeiXinUtils;

import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class UserRegistrationController extends JpaCRUDController<AccountApplication>{

	private static final long serialVersionUID = 1L;
	private AccountApplicationService accountApplicationService;
	private AccountApplication accountApplication;
	
    @Override
    protected void init() {
        filterBySite = true;
        accountApplicationService = WebUtil.getBean(AccountApplicationService.class);
        accountApplication = new AccountApplication();
    }
    
    public void submit(){
    	System.out.println(accountApplication);
    	if(accountApplication != null){
    		accountApplication.setWechatId(WeiXinUtils.getWxUserOpenId());
//    		accountApplication.setRoleId(1);
    	}
    	accountApplicationService.applyRegistration(this.accountApplication);
    	
    }
    
	
	@Override
	protected GenericRepository<AccountApplication> getDAO() {
		return null;
	}

	public AccountApplication getAccountApplication() {
		return accountApplication;
	}

	public void setAccountApplication(AccountApplication accountApplication) {
		this.accountApplication = accountApplication;
	}
}