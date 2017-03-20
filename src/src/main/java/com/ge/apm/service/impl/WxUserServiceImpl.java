package com.ge.apm.service.impl;

import com.ge.apm.dao.OrgInfoRepository;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.Digests;
import org.apache.commons.codec.DecoderException;

@Service
public class WxUserServiceImpl {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    OrgInfoRepository orgDao;

    public UserAccount getUser(String openId) {
        return userAccountRepository.getByWeChatId(openId);
    }

    public String getHospitalName(UserAccount user) {
        OrgInfo org = orgDao.findById(user.getHospitalId());
        return (null == org) ? null : org.getName();
    }
    
    public Boolean validateOldPassword(UserAccount user,String plainPassword){
        if(user!=null) {
            try {
                byte[] salt = Digests.decodeHex(user.getPwdSalt());
                byte[] hashPassword;
                hashPassword = Digests.sha1(plainPassword.getBytes(), salt, UserAccount.HASH_INTERATIONS);
                String encryptedPassword = Digests.encodeHex(hashPassword);
                if (user.getPassword().equals(encryptedPassword)) {
                    return true;
                }
            } catch (DecoderException | NoSuchAlgorithmException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return false;
    }

    public Boolean resetPassword(UserAccount ua, String newPassword) {
        if (ua == null) {
            return false;
        }
        ua.setPlainPassword(newPassword);
        try {
            ua.entryptPassword();
        } catch (NoSuchAlgorithmException e) {
            logger.error("encry password error !");
            e.printStackTrace();
            return false;
        }
        userAccountRepository.save(ua);
        return true;
    }

}
