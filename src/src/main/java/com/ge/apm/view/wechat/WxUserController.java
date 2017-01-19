package com.ge.apm.view.wechat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.WxUserService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepository;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class WxUserController  extends JpaCRUDController<UserAccount>{

	private static final long serialVersionUID = -1L;
	private UserAccount currentUser;
	private String newPassword;
	
	@Autowired
    private WxUserService wxUserService;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    protected void init() {
    	currentUser = UserContextService.getCurrentUserAccount();
//    	wxUserService = WebUtil.getBean(WxUserServiceImpl.class);
    	if(currentUser == null){
    		currentUser = new UserAccount();
    		currentUser.setName("gzp");
    		currentUser.setLoginName("user");
    		currentUser.setEmail("gaozhanpeng999@163.com");
    		currentUser.setTelephone("13262569125");
    		currentUser.setWeChatId("helloworld");
    	}
    }

   public UserAccount getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccount currentUser) {
		this.currentUser = currentUser;
	}

	public UserAccount getUser(){
		
	   return currentUser;
   } 
   
   public void resetPassword(){
	   logger.info("newPwd is {}",newPassword);
	   wxUserService.resetPassword(currentUser.getWeChatId(), newPassword);
   }
    
	@Override
	protected GenericRepository<UserAccount> getDAO() {
		return null;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
}
