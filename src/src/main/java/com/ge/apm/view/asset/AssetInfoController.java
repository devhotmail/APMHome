package com.ge.apm.view.asset;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.UserAccountService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import webapp.framework.web.WebUtil;
import webapp.framework.dao.SearchFilter;

@ManagedBean
@SessionScoped
public class AssetInfoController extends JpaCRUDController<AssetInfo> {

    AssetInfoRepository dao = null;

    UserAccountRepository userDao = null;

    private boolean resultStatus;
    UserAccount currentUser;

    @Override
    protected void init() {
        System.out.println("=========AssetInfoController======init");
        dao = WebUtil.getBean(AssetInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.filterBySite = true;
        this.setSiteFilter();

//        String actionName = WebUtil.getRequestParameter("actionName");
//        if ("Create".equalsIgnoreCase(actionName)) {
//            try {
//                prepareCreate();
//            } catch (InstantiationException | IllegalAccessException ex) {
//                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else if ("Delete".equalsIgnoreCase(actionName)) {
//            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
//            prepareDelete();
//        }
        UserAccountService service = (UserAccountService) WebUtil.getBean(UserAccountService.class);
        ownerList = service.getUserList();

    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetInfo> getItemList() {
        //to do: change the code if necessary
        return dao.findBySearchFilter(searchFilters);
    }

    /*
    @Override
    public void onBeforeNewObject(AssetInfo object) {
    }
    
    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(AssetInfo object) {
    }
    
    @Override
    public void onAfterUpdateObject(AssetInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(AssetInfo object) {
    }
    
    @Override
    public void onAfterDeleteObject(AssetInfo object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(AssetInfo object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
     */
    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }

    @Override
    public void onAfterUpdateObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }

    @Override
    public void onAfterDeleteObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }

    public String returnList() {
        this.selected = null;
        this.owner = null;
        return "List?faces-redirect=true";
    }

    public String removeOne() {
        this.delete();
        if (resultStatus) {
            return "List";
        } else {
            return "";
        }
    }

    public String applyChange() {
        if ("Create".equalsIgnoreCase(this.crudActionName)) {
            selected.setSiteId(currentUser.getSiteId());
            selected.setHospitalId(2);
            selected.setAssetDeptId(123);
            selected.setAssetOwnerName("aaa");
        }
        this.save();
        if (resultStatus) {
            return "List";
        } else {
            return "";
        }

    }

    public List<String> completeText(String query) {
        List<String> results = new ArrayList<String>();
        List<SearchFilter> usersSearchFilters = new ArrayList<>();
        usersSearchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, currentUser.getSiteId()));
        usersSearchFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, query));
        List<UserAccount> userList = userDao.findBySearchFilter(usersSearchFilters);
        for (int i = 0; i < (userList.size() > 10 ? 10 : userList.size()); i++) {
            results.add(userList.get(i).getName());
        }
        return results;
    }

    private UserAccount owner;
    private List<UserAccount> ownerList;

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public List<UserAccount> getOwnerList() {
        return ownerList;
    }

    public void setOwnerList(List<UserAccount> ownerList) {
        this.ownerList = ownerList;
    }

    public void onOwnerChange() {
        if (null != owner) {
            selected.setAssetOwnerId(owner.getId());
            selected.setAssetOwnerName(owner.getName());
            selected.setAssetOwnerTel(owner.getTelephone());
        }

    }

}
