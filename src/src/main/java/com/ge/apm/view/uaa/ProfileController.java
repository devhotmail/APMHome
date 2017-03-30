package com.ge.apm.view.uaa;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.web.WebUtil;

/**
 *
 * @author 212547631
 */
@ManagedBean
@ViewScoped
public class ProfileController implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private UserAccount myAccount;
    
    private String newPassword;
    private String newPassword2;
    UserAccountService userAccountService;
    
    @PostConstruct
    public void init(){
        myAccount = UserContextService.getCurrentUserAccount();
        if(myAccount != null && myAccount.getWeChatId() != null){
        	myAccount.setIsUnbunding(true);
        }
    }
    
    public UserAccount getMyAccount() {
        return myAccount;
    }

    public void setMyAccount(UserAccount myAccount) {
        this.myAccount = myAccount;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }
    
    public void saveProfile(){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        if(!myAccount.getIsUnbunding()){
        	myAccount.setWeChatId(null);
        }
        userAccountDao.save(myAccount);
        WebUtil.addSuccessMessage(WebUtil.getMessage("UserAccount")+WebUtil.getMessage("Updated"));
    }
    
    public void resetPassword() throws NoSuchAlgorithmException{
        myAccount.setPlainPassword(newPassword);
        myAccount.entryptPassword();
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        userAccountDao.save(myAccount);
        
        newPassword = "";
        newPassword2 = "";
        
        WebUtil.addSuccessMessage(WebUtil.getMessage("Password")+WebUtil.getMessage("Updated"));
    }
}
