package com.ge.apm.view.wechat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.impl.WxUserServiceImpl;
import javax.annotation.PostConstruct;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

@ManagedBean
@ViewScoped
public class WxUserController {

    private UserAccount currentUser;
    private String confirmPassword;
    private WxUserServiceImpl wxUserService;
    private String oldPassword;
    private String newPassword;
    private Boolean showResult = false;
    private String msg;

    @PostConstruct
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

    public String getHospitalName() {
        return wxUserService.getHospitalName(currentUser);
    }

    public void resetPassword() {
        showResult = true;
        if (!wxUserService.validateOldPassword(currentUser, oldPassword)) {
            this.msg = WebUtil.getMessage("security_error");
        } else if (!wxUserService.resetPassword(currentUser, newPassword)) {
            this.msg = WebUtil.getMessage("PersistenceErrorOccured");
        }
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Boolean getShowResult() {
        return showResult;
    }

    public String getMsg() {
        return msg;
    }

}
