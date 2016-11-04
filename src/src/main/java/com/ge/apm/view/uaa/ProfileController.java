package com.ge.apm.view.uaa;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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

    @PostConstruct
    public void init(){
        myAccount = UserContextService.getCurrentUserAccount();
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
        userAccountDao.save(myAccount);
        WebUtil.addSuccessMessage(WebUtil.getMessage("UserAccount")+WebUtil.getMessage("Updated"));
    }
    
    public void resetPassword(){
        myAccount.setPassword(newPassword);
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        userAccountDao.save(myAccount);
        
        newPassword = "";
        newPassword2 = "";
        
        WebUtil.addSuccessMessage(WebUtil.getMessage("Password")+WebUtil.getMessage("Updated"));
    }
}
