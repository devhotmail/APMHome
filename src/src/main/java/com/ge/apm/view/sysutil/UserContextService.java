package com.ge.apm.view.sysutil;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SiteInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import javaslang.control.Option;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

@ManagedBean(name = "userContextService")
@SessionScoped
public class UserContextService implements Serializable {
  private static final long serialVersionUID = 1L;

  private UserAccount userAccount = null;

  public void processAfterLogin() {
    getLoginUser();
  }

  public boolean isLoggedIn() {
    return !isAnonymousUser();
  }

  public String getUserLoginName() {
    return UserContext.getUsername();
  }

  public String getUserName() {
    if (userAccount == null)
      return "";
    else
      return userAccount.getName();
  }

  public boolean isAnonymousUser() {
    return UserContext.ANONYMOUS_USER.equalsIgnoreCase(getUserLoginName());
  }

  private boolean isFirstRequestAfterLogin = true;

  public boolean isIsFirstRequestAfterLogin() {
    return isFirstRequestAfterLogin;
  }

  public void checkIsSuperAdminAndFirstRequest() {
    if (!getLoginUser().getIsSuperAdmin()) return;

    if (!isFirstRequestAfterLogin) return;
    isFirstRequestAfterLogin = false;

    //navigate to super admin's home page
    FacesContext context = FacesContext.getCurrentInstance();
    ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();
    handler.performNavigation("admin/adminHome.xhtml?faces-redirect=true");
  }

  public static List<String> getRoles() {
    return UserContext.getRoles();
  }

  public boolean hasRole(String role) {
    return UserContextService.checkRole(role);
  }

  public static boolean checkRole(String role) {
    if (UserContextService.isSuperAdmin()) return true;
    else if ("SuperAdmin".equals(role)) return false;

    if (UserContextService.isSiteAdmin()) return true;
    else if ("SiteAdmin".equals(role)) return false;

    if (UserContextService.isLocalAdmin()) return true;
    else if ("LocalAdmin".equals(role)) return false;
    return UserContext.getRoles().contains(role);
  }

  public UserAccount getLoginUser() {
    if (userAccount == null) {
      String userName = getUserLoginName();
      if (userName == null) return null; // user is not logined

      UserAccountRepository dao = WebUtil.getBean(UserAccountRepository.class);
      userAccount = dao.getByLoginName(getUserLoginName());
    }
    else{
        // check if user has switch account by WeChat Client
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String userName = UserContext.getUsername(request);
        
        if(!userAccount.getLoginName().equals(userName)){
            userAccount = WebUtil.getUserAccountFromRequest();
        }
    }

    return userAccount;
  }

  public int getLoginUserId() {
    return Option.of(getLoginUser()).map(UserAccount::getId).getOrElse(-1);
  }

  public String getLoginUserName() {
    return Option.of(getLoginUser()).map(UserAccount::getLoginName).getOrElse("");
  }

  public int getLoginUserSiteId() {
    return Option.of(getLoginUser()).map(UserAccount::getSiteId).getOrElse(-1);
  }

  public String getLoginUserSiteName() {
    return Option.of(getLoginUser()).map(u -> WebUtil.getBean(SiteInfoRepository.class).findById(getLoginUser().getSiteId()).getName()).getOrElse("");
  }

  public int getLoginUserHospitalId() {
    return Option.of(getLoginUser()).map(UserAccount::getHospitalId).getOrElse(-1);
  }

  public int getLoginUserOrgInfoId() {
    return Option.of(getLoginUser()).map(UserAccount::getOrgInfoId).getOrElse(-1);
  }

  public OrgInfo getLoginUserOrgInfo() {
    return Option.of(getLoginUser()).map(u -> WebUtil.getBean(OrgInfoRepository.class).findById(u.getOrgInfoId())).getOrElseThrow(() -> new IllegalStateException(String.format("unable to get orgInfo for user.orgInfoId = %s", userAccount.getOrgInfoId())));
  }

  public boolean getIsSuperAdmin() {
    return UserContextService.isSuperAdmin();
  }

  public boolean getIsTenantAdmin() {
    return UserContextService.isSiteAdmin();
  }

  public boolean getIsLocalAdmin() {
    return UserContextService.isLocalAdmin();
  }

  public static UserAccount getCurrentUserAccount() {
    UserContextService userContextService = WebUtil.getBean(UserContextService.class);
    return userContextService.getLoginUser();
  }

  public static boolean isSuperAdmin() {
    UserAccount userAccount = getCurrentUserAccount();
    return userAccount.getIsSuperAdmin();
  }

  public static boolean isSiteAdmin() {
    UserAccount userAccount = getCurrentUserAccount();
    return userAccount.getIsSiteAdmin();
  }

  public static boolean isLocalAdmin() {
    UserAccount userAccount = getCurrentUserAccount();
    return userAccount.getIsLocalAdmin();
  }

  public static int getUserID() {
    UserAccount userAccount = getCurrentUserAccount();
    if (userAccount != null) return userAccount.getId();

    return -1;
  }

  public static int getSiteId() {
    UserAccount userAccount = getCurrentUserAccount();
    if (userAccount != null) return userAccount.getSiteId();

    return -1;
  }

  public static void setSiteFilter(List<SearchFilter> filters) {
    filters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, UserContextService.getSiteId()));
  }

  public static void setHospitalFilter(List<SearchFilter> filters) {
    filters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getHospitalId()));
  }

  public String getUserDefaultHomePage() {
    this.getLoginUser();

    if (userAccount == null)
      return "/login.xhtml";
    else {
      UaaService uaaService = WebUtil.getBean(UaaService.class);
      return uaaService.getUserDefaultHomePage(userAccount);
    }
  }
}
