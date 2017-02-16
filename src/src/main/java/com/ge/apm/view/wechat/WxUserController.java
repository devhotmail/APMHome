package com.ge.apm.view.wechat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.impl.WxUserServiceImpl;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.service.UserContext;

@ManagedBean
@ViewScoped
public class WxUserController extends JpaCRUDController<UserAccount>{

	private static final long serialVersionUID = -1L;
	private UserAccount currentUser;
//	private String newPassword;
	private String confirmPassword;
	
    private WxUserServiceImpl wxUserService;
	
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Override
    protected void init() {
    	currentUser = UserContext.getCurrentLoginUser();
    	wxUserService = WebUtil.getBean(WxUserServiceImpl.class);
    }

   public UserAccount getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccount currentUser) {
		this.currentUser = currentUser;
	}
   
   public void resetPassword(){
	   logger.info("confirmPwd is {}",confirmPassword);
//	   if(!newPassword.equals(confirmPassword)){
//		   WebUtil.addSuccessMessage(WebUtil.getMessage("PasswordNotMatch"));
//		   return;
//	   }
	   wxUserService.resetPassword(currentUser.getWeChatId(), confirmPassword);
//	   newPassword = null;
	   confirmPassword = null;
   }
    
	@Override
	protected GenericRepository<UserAccount> getDAO() {
		return null;
	}

//	public String getNewPassword() {
//		return newPassword;
//	}
//
//	public void setNewPassword(String newPassword) {
//		this.newPassword = newPassword;
//	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
}
