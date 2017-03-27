package com.ge.apm.view.uaa;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.UserRegistrationRepository;
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
	private UserRegistrationRepository userRegistrationRepository;
	private String condition;
	private Integer applyId;
	
    @Override
    protected void init() {
        filterBySite = false;
        accountApplicationService = WebUtil.getBean(AccountApplicationService.class);
        String applyId = WebUtil.getRequestParameter("applyId");
        if(applyId != null){
        	accountApplication = accountApplicationService.getApplyById(Integer.parseInt(applyId));
        }else{
        	accountApplication = new AccountApplication();
        }
        userRegistrationRepository = WebUtil.getBean(UserRegistrationRepository.class);
    }
    
    public void submit(){
//    	System.out.println(accountApplication);
//    	if(accountApplication != null){
//    		accountApplication.setWechatId(WeiXinUtils.getWxUserOpenId());
//    	}
//    	accountApplicationService.applyRegistration(this.accountApplication);
    	
    }
    
    public List<AccountApplication> filterByDate(){
    	String date = "20170322";
    	String openId = "";
    	System.out.println(condition);
    	List<AccountApplication> result = accountApplicationService.getApplyByDate(date);
    	System.out.println(result.size());
    	return result;
    }
    
    public List<AccountApplication> getApplyList(){
    	String openId = "openId609";
    	return accountApplicationService.getApplyList(openId);
    }
	
	@Override
	protected GenericRepository<AccountApplication> getDAO() {
		return userRegistrationRepository;
	}

	public AccountApplication getAccountApplication() {
		return accountApplication;
	}

	public void setAccountApplication(AccountApplication accountApplication) {
		this.accountApplication = accountApplication;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Integer getApplyId() {
		return applyId;
	}

	public void setApplyId(Integer applyId) {
		this.applyId = applyId;
	}
	
	
}
