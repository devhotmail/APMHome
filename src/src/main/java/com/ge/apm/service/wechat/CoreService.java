package com.ge.apm.service.wechat;

import com.ge.apm.dao.FileUploadDao;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.WorkOrderPhotoRepository;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkOrderPhoto;
import com.ge.apm.domain.WorkOrderStep;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.Digests;
import com.ge.apm.service.wo.WorkOrderService;
import java.io.File;
import java.io.FileInputStream;
import com.ge.apm.web.wechat.WeChatCoreController;
import com.google.common.base.Strings;
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
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.camel.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.broker.SiBroker;
import webapp.framework.context.ExternalLoginHandler;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

/**
 * Created by FirenzesEagle on 2016/5/30 0030.
 * Email:liumingbo2008@gmail.com
 */
@Service
@Configuration
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
    @Autowired 
    protected WxMpMessageHandler textHandler;
    private WxMpMessageRouter router;
    @Autowired
    protected SupplierRepository supplierDao;
    @Autowired
    protected WorkOrderPhotoRepository photoDao;
    
    @PostConstruct
    public void init() {
    	System.out.println("1================"+System.getenv("webContextUrl"));
        this.refreshRouter();
        if (!Strings.isNullOrEmpty(System.getenv("webContextUrl"))) {
        	System.out.println("2================"+System.getenv("webContextUrl"));
            webContextUrl = System.getenv("webContextUrl");
        }
    }

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

        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils.toString(entity);

        httpget.releaseConnection();
    }

    public void requestPost(String url, List<NameValuePair> params) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        CloseableHttpResponse response = httpclient.execute(httppost);

        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils.toString(entity, "utf-8");

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
    public int bindingUserInfo(HttpServletRequest request,HttpServletResponse response, String openId, String username, 
    																	String password, String newPwd, String confirmPwd) {
    	UserAccount ua = userValidate(username, password);
//    	if(StringUtils.isEmpty(newPwd) || StringUtils.isEmpty(confirmPwd) || (!newPwd.equals(confirmPwd))){
//    		return 1;//
//    	}
    	if (ua == null ){//账号密码校验
    		return 1;//绑定失败
    	}
        WxMpUser user = getUserInfo(openId, null);
        if(user == null){//微信校验
        	return 1;
        }
       //更新密码 
        try {
        	ua.setPlainPassword(newPwd);
            ua.entryptPassword();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        ua.setWeChatId(openId);
        userDao.save(ua);
        //主动登录
        loginByWeChatOpenId(openId, request, response);
        return 0;//绑定成功
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
        workOrder.setCurrentStepId(1);
        workOrder.setTotalManHour(0);
        workOrder.setTotalPrice(0.0);
        
//        selected.setAssetId(Integer.parseInt(assetId));
//        selected.setAssetName((String) UrlEncryptController.getValueFromMap(encodeStr,"assetName"));
//        selected.setCaseOwnerId(Integer.parseInt((String)UrlEncryptController.getValueFromMap(encodeStr,"assetOwnerId")));
//        selected.setCaseOwnerName((String) UrlEncryptController.getValueFromMap(encodeStr,"assetOwnerName"));
//        selected.setHospitalId(getHospitalIdFromAsset(Integer.parseInt(assetId)));

        WorkOrderStep step = woService.initWorkOrderCurrentStep(workOrder);
        String serverId = null;
        woService.createWorkOrderStep(workOrder, step);
        //保存完成后，再把上传的图片保存
        if (serverId != null)
            uploadImage(workOrder, serverId);
    }
    
    @Transactional
    public void uploadVoice(WorkOrder workOrder, String serverId) throws WxErrorException {
        File file = wxMpService.getMaterialService().mediaDownload(serverId);
        if (file == null)
            return;
        Integer uploadFileId = uploadFile(file);
        if (uploadFileId == 0) 
            return;
        workOrder.setRequestReasonVoice(uploadFileId);
        file.delete();
    }
    
    public void uploadImage(WorkOrder workOrder, String serverId) throws WxErrorException {
        File file = wxMpService.getMaterialService().mediaDownload(serverId);
        if (file == null)
            return;
        Integer uploadFileId = uploadFile(file);
        if (uploadFileId > 0) {
            WorkOrderPhoto photo = new WorkOrderPhoto();
            photo.setPhotoId(uploadFileId);
            photo.setSiteId(workOrder.getSiteId());
            photo.setWorkOrderId(workOrder.getId());
            photoDao.save(photo);
        }
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
    
    public Object[] getFile(int fileId) throws Exception{
        FileUploadDao fileUploaddao = new FileUploadDao();
        return fileUploaddao.getAttachmentFile(fileId);
    }
    
    public UserAccount getLoginUser(HttpServletRequest request) {
        return userDao.getByLoginName(UserContext.getUsername(request));
    }
    
    public List<I18nMessage> getMsg(String msgType) {
        return msgDao.getByMsgType(msgType);
    }
    
    public Object getMsgValue(String msgType, String key) {
        List<I18nMessage> msgs = msgDao.getByMsgTypeAndSiteIdAndMsgKey(msgType, -1, key);
        if (msgs != null && !msgs.isEmpty())
            return msgs.get(0).getValueZh();
        return null;
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
    
    public List<Map<String, Object>> getUsersWithAssetStaffRole(HttpServletRequest request){
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        List<UserAccount> uas = uaaService.getUsersWithAssetStaffRole(getLoginUser(request).getHospitalId());
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
    
    public void refreshRouter() {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(
            this.wxMpService);
        // 关注事件
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_TEXT).handler(this.textHandler).end();
        this.router = newRouter;
    }

    public WxMpXmlOutMessage route(WxMpXmlMessage inMessage) {
        try {
            return this.router.route(inMessage);
        } catch (Exception ex) {
            Logger.getLogger(WeChatCoreController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    public void saveVoice(String serverId) throws Exception{
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(38);
        uploadVoice(workOrder, serverId);
    }
    public String uploadMediaToWechat(InputStream inputStream) throws Exception{
        WxMediaUploadResult res = wxMpService.getMaterialService().mediaUpload(WxConsts.MEDIA_VOICE, WxConsts.FILE_AMR, inputStream);
        return res.getMediaId();
    }
    
    public String getSupplierName(int id) {
        Supplier s = supplierDao.findById(id);
        return s == null ? null : s.getName();
    }

//    @Value("#{wxProperties.webContextUrl}")
    private String webContextUrl;
    
    public String getWoDetailUrl(Integer woId) {
        return "/web/menu/34?woId="+ woId;
    }
    
    public void sendWxTemplateMessage(String userWeChatId, String wxTemplateId, String msgTitle, String msgBrief, String msgDetails, String msgDateTime, String linkUrl){
        HashMap<String, Object> params = new HashMap<>();

        params.put("userWeChatId", userWeChatId);
        params.put("wxTemplateId", wxTemplateId);
        params.put("msgTitle", msgTitle);
        params.put("msgBrief", msgBrief);
        params.put("msgDetails", msgDetails);
        params.put("msgDateTime", msgDateTime);

        if(linkUrl==null || "".equals(linkUrl.trim())) 
            linkUrl = null;
        else
            linkUrl = linkUrl.trim();
        
        params.put("linkUrl", linkUrl);

        //let camel route call doSendWxTemplateMessage in async mode and retry 3 times
        SiBroker.sendMessageWithHeaders("direct:wxSendMessage", null, params);
    }
    
    public void doSendWxTemplateMessage(@Headers Map<String, Object> params) throws WxErrorException{
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(params.get("userWeChatId").toString()).templateId(params.get("wxTemplateId").toString()).build();

        String textColor = "#000000";
        templateMessage.addWxMpTemplateData( new WxMpTemplateData("first", params.get("msgTitle").toString(),textColor));
        templateMessage.addWxMpTemplateData( new WxMpTemplateData("performance", params.get("msgBrief").toString(),textColor));
        templateMessage.addWxMpTemplateData( new WxMpTemplateData("remark", params.get("msgDetails").toString(),textColor));
        templateMessage.addWxMpTemplateData( new WxMpTemplateData("time", params.get("msgDateTime").toString(),textColor));

        Object linkUrl = params.get("linkUrl");
        if( linkUrl!=null && !"".equals(linkUrl) )
            templateMessage.setUrl(wxMpService.oauth2buildAuthorizationUrl(webContextUrl+params.get("linkUrl").toString(), WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
        else
            templateMessage.setUrl("");
        
        wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
    }    
}
