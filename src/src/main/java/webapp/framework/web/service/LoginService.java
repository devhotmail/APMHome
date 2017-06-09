package webapp.framework.web.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;

import webapp.framework.context.WrappedRequest;
import webapp.framework.web.WebUtil;

public class LoginService implements Serializable, ApplicationEventPublisherAware {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
	public static final int PASSWORD_ERROR_TIME_LIMIT = 6;
	private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;
	private SessionAuthenticationStrategy sessionAuthenticationStrategy;
	private SecurityContextRepository securityContextRepository;

	private MessageUtil messageUtil;
	private UserAccountRepository userAccountDao;

	private ApplicationEventPublisher applicationEventPublisher;

	static {
		IdFuzzyResolver.registerResolver();

		// Global setting: change default timezone to UTC+0
		// TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
	}

	@PostConstruct
	private void init() {
		userAccountDao = WebUtil.getBean(UserAccountRepository.class);
		WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
		usernamePasswordAuthenticationFilter = ctx.getBean(UsernamePasswordAuthenticationFilter.class);
		sessionAuthenticationStrategy = ctx.getBean(SessionAuthenticationStrategy.class);
		securityContextRepository = ctx.getBean(SecurityContextRepository.class);
		messageUtil = ctx.getBean(MessageUtil.class);
	}

	public void setSessionExpired(String value) {
		messageUtil.error("session_expired");
	}

	public String getSessionExpired() {
		return null;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void forgetPassword() {
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
		String loginName = request.getParameter("j_username");

		FacesMessage msg = new FacesMessage();
		msg.setSummary("Password reset");
		msg.setDetail("Password reset");
		FacesContext.getCurrentInstance().addMessage("forgetPassword", msg);
	}

	public String onPasswordEncrypted(String loginName, String plainPassword) {
		return plainPassword;
	}

	protected void afterLogin() {

	}

	protected void setCookieForUser(HttpServletResponse res, String userName, String password) throws Exception {
		String body = "{\"loginName\": \"" + userName + "\",\"password\": \"" + password + "\"}";
		Properties pro = new Properties();
		pro.load(PropertyUtils.class.getResourceAsStream("/url.properties"));
		String authenticateBasic = pro.getProperty("authenticateBasic").trim();

		String jsonStr = requestPost(authenticateBasic, body);
		JSONObject obj = new JSONObject(jsonStr);
		if (obj == null)
			return;
		JSONObject data = obj.getJSONObject("data");
		if (data == null)
			return;
		Cookie cookie = new Cookie("Authorization", data.getString("id_token"));
		res.addCookie(cookie);
	}

	public String requestPost(String url, String body) {
		CloseableHttpClient httpclient = null;
		HttpPost httppost = null;
		try {
			httpclient = HttpClientBuilder.create().build();
			httppost = new HttpPost(url);
			httppost.setEntity(new StringEntity(body));
			httppost.setHeader("Content-Type", "application/json");

			CloseableHttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, "utf-8");
		} catch (Exception e) {
			messageUtil.error("security_cookie_error");
		} finally {
			try {
				if (httppost != null) {
					httppost.releaseConnection();
				}
				if (httpclient != null) {
					httpclient.close();
				}
			} catch (IOException e) {
				messageUtil.error("security_cookie_error");
			}
		}
		return "";
	}

	public void doLogin() {
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
		HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
		HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
		SecurityContext securityContext = securityContextRepository.loadContext(holder);
		SecurityContextHolder.setContext(securityContext);
		String userName = "";
		String password = "";
		try {
			Map<String, String[]> extraParams = new TreeMap<String, String[]>();
			userName = request.getParameter("j_username");
			password = request.getParameter("j_password");
			String originPwd = password;

			if ("".equals(userName) || userName == null) {
				userName = request.getParameter("input_j_username");
				password = request.getParameter("input_j_password");
				extraParams.put("j_username", new String[] { userName });
				extraParams.put("j_password", new String[] { password });
				HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
				request = wrappedRequest;
			}
			if (password == null)
				password = "";

			password = onPasswordEncrypted(userName, password);
			extraParams.put("j_username", new String[] { userName });
			extraParams.put("j_password", new String[] { password });
			HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
			request = wrappedRequest;

			Authentication authResult = usernamePasswordAuthenticationFilter.attemptAuthentication(request, response);
			if (authResult == null) {
				updateLoginStatus(userName, false);
				messageUtil.error("security_error");
				return;
			}
			sessionAuthenticationStrategy.onAuthentication(authResult, request, response);
			// below : do the same thing as in
			// AbstractAuthenticationProcessingFilter.successfulAuthentication(),
			// except for the redirection to the login success URL that is
			// managed by JSF
			securityContext.setAuthentication(authResult);
			usernamePasswordAuthenticationFilter.getRememberMeServices().loginSuccess(request, response, authResult);
			if (applicationEventPublisher != null) {
				applicationEventPublisher
						.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, getClass()));
			}
//			try {
//				setCookieForUser(response, userName, originPwd);
//			} catch (Exception e) {
//				messageUtil.error("security_cookie_error");
//			}
			afterLogin();

                        updateLoginStatus(userName, true);
			// redirects to the home page
			/*
			 * FacesContext context = FacesContext.getCurrentInstance();
			 * ConfigurableNavigationHandler handler =
			 * (ConfigurableNavigationHandler)
			 * context.getApplication().getNavigationHandler();
			 * handler.performNavigation("/home.xhtml?faces-redirect=true");
			 */
		} catch (UsernameNotFoundException e) {
			messageUtil.error("security_username_not_found");
		} catch (DisabledException e) {
			messageUtil.error("security_account_disabled");
		} catch (LockedException e) {
			messageUtil.error("AccountLocked");
		} catch (AccountExpiredException e) {
			messageUtil.error("security_account_expired");
		} catch (CredentialsExpiredException e) {
			messageUtil.error("security_account_credentials_expired");
		} catch (AccountStatusException e) {
			messageUtil.error("security_account_status_invalid");
		} catch (BadCredentialsException e) {
			updateLoginStatus(userName, false);
			//messageUtil.error("security_bad_credentials");
		} catch (AuthenticationException e) {
			updateLoginStatus(userName, false);
			//messageUtil.error("security_error");
		} finally {
			securityContextRepository.saveContext(securityContext, holder.getRequest(), holder.getResponse());
		}
	}

	private void updateLoginStatus(String userName, boolean loginStatus) {
		if(userName ==  null || StringUtils.isEmpty(userName)){
			logger.error("loginName:{} invalid !", userName);
			messageUtil.error("security_bad_credentials");
			return;
		}
		UserAccount user = userAccountDao.getByLoginName(userName);
		if (user == null) {
			messageUtil.error("security_bad_credentials");
			logger.error("can't find user by loginName:{}", userName);
			return;
		}
                if(user.getIsLocked()!=null && user.getIsLocked().booleanValue()==true){
                    messageUtil.error("AccountLocked");
                    return;
                }

		if (loginStatus) {
			user.setLastLoginTime(new Date());
			user.setPasswordErrorCount(0);
                        user.setIsLocked(false);
			userAccountDao.save(user);
		} else {
			Integer errorTimes = user.getPasswordErrorCount();
			if (errorTimes == null) {
				errorTimes = 0;
			}
			AtomicInteger ai = new AtomicInteger(errorTimes);
			ai.getAndAdd(1);
			errorTimes = ai.get();
			if (errorTimes >= PASSWORD_ERROR_TIME_LIMIT) {
				user.setIsLocked(true);
                                user.setLockTime(new Date());
        			userAccountDao.save(user);
				logger.error("user:{} is locked because of password error times reach the limit !", userName);
				messageUtil.error("AccountLocked");
				return;
			}
			user.setPasswordErrorCount(errorTimes);
			userAccountDao.save(user);

                        messageUtil.error("security_bad_credentials");
		}
	}
}
