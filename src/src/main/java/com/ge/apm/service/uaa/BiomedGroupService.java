package com.ge.apm.service.uaa;

import com.ge.apm.dao.BiomedGroupRepository;
import com.ge.apm.dao.BiomedGroupUserRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.BiomedGroup;
import com.ge.apm.domain.BiomedGroupUser;
import com.ge.apm.domain.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 212605082 on 2017/5/15.
 */
@Component
public class BiomedGroupService {


    @Autowired
    private BiomedGroupRepository biomedGroupDao;
    @Autowired
    private BiomedGroupUserRepository biomedGroupUserDao;
    @Autowired
    private UserAccountRepository userDao;

    public List<UserAccount> getBiomedGroupList(UserAccount queryUserAccount) {

        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, queryUserAccount.getSiteId()));
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, queryUserAccount.getHospitalId()));

        return userDao.findBySearchFilter(OrgInfoFilters);
    }

    public List<UserAccount> getTargetUserAccount(BiomedGroup tempBiomedGroup){
        List<UserAccount> tempTargetUserAccount = new ArrayList<>();

        if(tempBiomedGroup.getId() != null){
            for(BiomedGroupUser tempBiomedGroupUser : biomedGroupUserDao.findByGroupId(tempBiomedGroup.getId())){
                tempTargetUserAccount.add(tempBiomedGroupUser.getUserAccount());
            }
        }

        return tempTargetUserAccount;
    }

    @Transactional
    public void saveBiomedGroup(BiomedGroup tempBiomedGroup, List<UserAccount> targetUserAccount){

        if(tempBiomedGroup.getId() != null){
            biomedGroupUserDao.deleteByGroupId(tempBiomedGroup.getId());
        }

        for(UserAccount tempUserAccount : targetUserAccount){
            BiomedGroupUser tempBiomedGroupUser = new BiomedGroupUser();
            tempBiomedGroupUser.setGroupId(tempBiomedGroup.getId());
            tempBiomedGroupUser.setUserId(tempUserAccount.getId());
            tempBiomedGroupUser.setUserName(tempUserAccount.getName());
            biomedGroupUserDao.save(tempBiomedGroupUser);
        }

    }

}
