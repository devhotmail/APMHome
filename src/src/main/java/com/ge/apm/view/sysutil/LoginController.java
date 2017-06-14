package com.ge.apm.view.sysutil;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.Digests;
import com.ge.apm.service.utils.PasswordUtil;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.apache.commons.codec.DecoderException;
import org.primefaces.context.RequestContext;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.LoginService;

@ManagedBean
@RequestScoped
public class LoginController extends LoginService {
	private Boolean isSuccess;

    private String plainPassword;

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }
    
    public void validatePassword(){
        UserAccount user = UserContextService.getCurrentUserAccount();
        if(user==null){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        
        try {
            byte[] salt = Digests.decodeHex(user.getPwdSalt());
            byte[] hashPassword;

            hashPassword = Digests.sha1(plainPassword.getBytes(), salt, UserAccount.HASH_INTERATIONS);
            String saltedPassword = Digests.encodeHex(hashPassword);

            if(!user.getPassword().equals(saltedPassword)){
                WebUtil.addErrorMessageKey("PasswordNotMatch");
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            }

            plainPassword = "";
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DecoderException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String onPasswordEncrypted(String loginName, String plainPassword){
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        UserAccount user = userDao.getByLoginName(loginName);
        if(user!=null) {
            try {
                byte[] salt = Digests.decodeHex(user.getPwdSalt());
                byte[] hashPassword;
                
                hashPassword = Digests.sha1(plainPassword.getBytes(), salt, UserAccount.HASH_INTERATIONS);
                String saltedPassword = Digests.encodeHex(hashPassword);
                
                return saltedPassword;
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DecoderException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return plainPassword;        
    }
    
    @Override
    protected void afterLogin() {
        // called after user logined.
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        userContextService.processAfterLogin();
        isSuccess = true;
        UserAccount user = userContextService.getLoginUser();
        String saltedPassword = null;
        try {
            byte[] salt = Digests.decodeHex(user.getPwdSalt());
            byte[] hashPassword = Digests.sha1(PasswordUtil.DEFAULT_PWD.getBytes(), salt, UserAccount.HASH_INTERATIONS);
            saltedPassword = Digests.encodeHex(hashPassword);
        } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DecoderException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (user.getPassword().equals(saltedPassword)) {
            WebUtil.redirectTo("/resetPassword2.xhtml");
        } else {
            WebUtil.redirectTo(userContextService.getUserDefaultHomePage());
        }
    }
    
    public String getHomePage() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        return userContextService.getUserDefaultHomePage();
    }

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

}
