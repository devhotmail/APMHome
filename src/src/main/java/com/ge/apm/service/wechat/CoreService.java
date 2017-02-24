package com.ge.apm.service.wechat;

import com.ge.apm.dao.FileUploadDao;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkOrderStep;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.Digests;
import com.ge.apm.service.wo.WorkOrderService;
import java.io.File;
import java.io.FileInputStream;
import com.ge.apm.web.WeChatCoreController;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.util.TimeUtil;
import webapp.framework.context.ExternalLoginHandler;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

/**
 * Created by FirenzesEagle on 2016/5/30 0030.
 * Email:liumingbo2008@gmail.com
 */
@Service
public class CoreService {

    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected UserAccountRepository userDao;
    @Autowired
    protected WorkOrderRepository woDao;
    @Autowired
    protected WorkOrderService woService;
    @Autowired
    protected WorkOrderStepRepository stepDao;
    @Autowired
    protected I18nMessageRepository msgDao;
    @Autowired
    protected UaaService uaaService;

    public void requestGet(String urlWithParams) throws IOException {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httpget = new HttpGet(urlWithParams);
        httpget.addHeader("Content-Type", "text/html;charset=UTF-8");
        //配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(50)
            .setConnectTimeout(50)
            .setSocketTimeout(50).build();
        httpget.setConfig(requestConfig);

        CloseableHttpResponse response = httpclient.execute(httpget);
        System.out.println("StatusCode -> " + response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils.toString(entity);
        System.out.println(jsonStr);

        httpget.releaseConnection();
    }

    public void requestPost(String url, List<NameValuePair> params) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        CloseableHttpResponse response = httpclient.execute(httppost);
        System.out.println(response.toString());

        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils.toString(entity, "utf-8");
        System.out.println(jsonStr);

        httppost.releaseConnection();
    }

    public WxMpUser getUserInfo(String openid, String lang) {
        WxMpUser wxMpUser = null;
        try {
            wxMpUser = this.wxMpService.getUserService().userInfo(openid, lang);
        } catch (WxErrorException ex) {
            Logger.getLogger(CoreService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wxMpUser;
    }
    
    @Transactional
    public int bindingUserInfo(HttpServletRequest request,HttpServletResponse response, String openId, String username, String password) {
        WxMpUser user = getUserInfo(openId, null);
        UserAccount ua = userValidate(username, password);
        if (ua == null || user == null){
            return 1;//绑定失败
        } else {
            ua.setWeChatId(openId);
            userDao.save(ua);
            //主动登录
            loginByWeChatOpenId(openId, request, response);
            return 0;//绑定成功
        }
    }
    
    private UserAccount userValidate(String username, String password) {
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        UserAccount user = userDao.getByLoginName(username);
        if(user!=null) {
            try {
                byte[] salt = Digests.decodeHex(user.getPwdSalt());
                byte[] hashPassword;
                
                hashPassword = Digests.sha1(password.getBytes(), salt, UserAccount.HASH_INTERATIONS);
                String saltedPassword = Digests.encodeHex(hashPassword);
                
                if (user.getPassword().equals(saltedPassword)) {
                    return user;
                }
            } catch (Exception ex) {
                Logger.getLogger(CoreService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    @Transactional
    public void saveWorkOrder(WorkOrder workOrder, HttpServletRequest request) throws Exception{
        UserAccount loginUser =getLoginUser(request);
        workOrder.setSiteId(loginUser.getSiteId());
        workOrder.setCreatorId(loginUser.getId());
        workOrder.setCreateTime(TimeUtil.now());
        workOrder.setCurrentStepId(1);
        workOrder.setTotalManHour(0);
        workOrder.setTotalPrice(0.0);
        workOrder.setIsClosed(false);
        
//        selected.setAssetId(Integer.parseInt(assetId));
//        selected.setAssetName((String) UrlEncryptController.getValueFromMap(encodeStr,"assetName"));
//        selected.setCaseOwnerId(Integer.parseInt((String)UrlEncryptController.getValueFromMap(encodeStr,"assetOwnerId")));
//        selected.setCaseOwnerName((String) UrlEncryptController.getValueFromMap(encodeStr,"assetOwnerName"));
//        selected.setHospitalId(getHospitalIdFromAsset(Integer.parseInt(assetId)));

        WorkOrderStep step = woService.initWorkOrderCurrentStep(workOrder);
        String serverId = workOrder.getCloseReason();
        workOrder.setCloseReason(null);
        woService.createWorkOrderStep(workOrder, step);
        //保存完成后，再把上传的图片保存
        if (serverId != null)
            upload(workOrder, serverId);
    }
    
    @Transactional
    public void upload(WorkOrder workOrder, String serverId) throws WxErrorException {
        File file = wxMpService.getMaterialService().mediaDownload(serverId);
        if (file == null)
            return;
        Integer uploadFileId = uploadFile(file);
        String fileName = getFileName(file);
        List<WorkOrderStep> steps = stepDao.getByWorkOrderIdAndStepId(workOrder.getId(), 1);
        if (steps == null || steps.size() == 0) return;
        //如果有文件则删除以前的文件
        WorkOrderStep step = steps.get(0);
        if (step.getFileId() != null) {
            FileUploadDao fileUploaddao = new FileUploadDao();
            fileUploaddao.deleteUploadFile(step.getFileId());
        }
        step.setAttachmentUrl(fileName);
        if (uploadFileId > 0) {
            step.setFileId(uploadFileId);
        }
        stepDao.save(step);
        file.delete();
    }
    
    public int uploadFile(File file) {
        Integer returnId = 0;
        InputStream is = null;
        try {
            FileUploadDao fileUploaddao = new FileUploadDao();
            is = new FileInputStream(file);
            returnId = fileUploaddao.saveUploadFile(is, Integer.valueOf(String.valueOf(file.length())), getFileName(file));
        } catch (SQLException | IOException ex) {
            Logger.getLogger(CoreService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(is != null) {
                try{
                    is.close();
                }catch (IOException ex) {
                    Logger.getLogger(CoreService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return returnId;
    }
    public String getFileName(File file) {
        String fileName = "";
        try {
            fileName = new String(file.getName().getBytes(), "utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CoreService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileName;
    }
    
    public UserAccount getLoginUser(HttpServletRequest request) {
        return userDao.getByLoginName(UserContext.getUsername(request));
    }
    
    public List<I18nMessage> getMsg(String msgType) {
        return msgDao.getByMsgType(msgType);
    }
    
    public List<Map<String, Object>> getUsersInHospital(HttpServletRequest request){
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        List<UserAccount> uas = uaaService.getUserList(getLoginUser(request).getHospitalId());
        for(UserAccount ua :uas) {
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id", ua.getId());
            map.put("name", ua.getName());
            list.add(map);
        }
        return list;
    }
    
    public List<Map<String, Object>> getUsersWithAssetHeadOrStaffRole(HttpServletRequest request){
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        List<UserAccount> uas = uaaService.getUsersWithAssetHeadOrStaffRole(getLoginUser(request).getHospitalId());
        for(UserAccount ua :uas) {
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id", ua.getId());
            map.put("name", ua.getName());
            list.add(map);
        }
        return list;
    }

    public boolean loginByWeChatOpenIdForJSF(){
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse reponse = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
        return loginByWeChatOpenId(request, reponse);
    }

    public boolean loginByWeChatOpenId(HttpServletRequest request,HttpServletResponse response){
        if(UserContext.isLoggedIn(request)) return true; // user already logged in.

        String code = request.getParameter("code");
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken;
        try {
            //获得token
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            //获得用户基本信息
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            String openId = wxMpUser.getOpenId();
            
            ExternalLoginHandler loginHandler = WebUtil.getServiceBean(ExternalLoginHandler.class);
            return loginHandler.doLoginByWeChatOpenId(openId, request, response);
        } catch (WxErrorException ex) {
            Logger.getLogger(WeChatCoreController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public boolean loginByWeChatOpenId(String weChatOpenId, HttpServletRequest request,HttpServletResponse response){
        if(UserContext.isLoggedIn(request)) return true; // user already logged in.

        ExternalLoginHandler loginHandler = WebUtil.getServiceBean(ExternalLoginHandler.class);
        return loginHandler.doLoginByWeChatOpenId(weChatOpenId, request, response);
    }
    
}
