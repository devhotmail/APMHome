package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class TagSubscriberController extends JpaCRUDController<AssetTagMsgSubscriber> {

    private static final long serialVersionUID = 1L;

    private AssetTagMsgSubscriberRepository dao = null;
    private AssetTagRepository assetTagRepository;
    private I18nMessageRepository i18nMessageRepository;
    private UserAccount currentUser;

    private List<I18nMessage>   msgModeList;
    private List<AssetTag> assetTags;

    List<AssetTagMsgSubscriber> assetTagMsgSubscribers;
    HashMap<Integer,String> hmMsgMode = new HashMap<Integer,String>();
    private Integer receiveMsgMode;

    @Override
    protected void init() {
        this.filterBySite =false;
        dao= WebUtil.getBean(AssetTagMsgSubscriberRepository.class);
        assetTagRepository =WebUtil.getBean(AssetTagRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        assetTagMsgSubscribers = dao.getAssetTagMsgSubscriber(currentUser.getId());
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        FieldValueMessageController fieldMsg = WebUtil.getBean(FieldValueMessageController.class, "fieldMsg");
        msgModeList = fieldMsg.getFieldValueList("SubscriberOption");

        for (I18nMessage local1 : msgModeList) {
            hmMsgMode.put(Integer.parseInt(local1.getMsgKey()), local1.getValue());
        }
        assetTags =  assetTagRepository.getUnSbuscriberTag(currentUser.getId());
        System.out.println();
    }
    public String getTagName(Integer tagId){
        return assetTagRepository.findById(tagId).getName();
    }


    public String getMsgMode(int msgmode){
        return hmMsgMode.get(msgmode);
    }

    @Override
    protected AssetTagMsgSubscriberRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetTagMsgSubscriber> loadData(PageRequest pageRequest) {
        selected = null;

        List<SearchFilter> searchFilters =  new ArrayList<>();
        SearchFilter subscribeUserIdFilter = new SearchFilter("subscribeUserId", SearchFilter.Operator.EQ, currentUser.getId());
        searchFilters.add(subscribeUserIdFilter);

        if(searchFilters.size() <= 0){
            return dao.findAll(pageRequest);
        }else{
            return dao.findBySearchFilter(searchFilters, pageRequest);
        }
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.selected.setReceiveMsgMode(null);
        assetTags =  assetTagRepository.getUnSbuscriberTag(currentUser.getId());
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        assetTags = assetTagRepository.find();

    }

    @Override
    public void onBeforeNewObject(AssetTagMsgSubscriber object) {
        super.onBeforeNewObject(object);
        object.setSubscribeUserId(currentUser.getId());
        object.setIsReceiveChatMsg(true);
    }

    public HashMap<Integer, String> getHmMsgMode() {
        return hmMsgMode;
    }

    public void setHmMsgMode(HashMap<Integer, String> hmMsgMode) {
        this.hmMsgMode = hmMsgMode;
    }

    public List<I18nMessage> getMsgModeList() {
        return msgModeList;
    }

    public void setMsgModeList(List<I18nMessage> msgModeList) {
        this.msgModeList = msgModeList;
    }

    public List<AssetTag> getAssetTags() {
        return assetTags;
    }

    public void setAssetTags(List<AssetTag> assetTags) {
        this.assetTags = assetTags;
    }

    public Integer getReceiveMsgMode() {
        if(this.selected!=null)
        {
            return  this.selected .getReceiveMsgMode();
        }else

            return receiveMsgMode;
    }

    public void setReceiveMsgMode(Integer receiveMsgMode) {
        this.receiveMsgMode = receiveMsgMode;
    }
}