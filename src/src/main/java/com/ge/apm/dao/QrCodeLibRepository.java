package com.ge.apm.dao;

import com.ge.apm.domain.QrCodeLib;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * Created by 212605082 on 2017/3/22.
 */
public interface QrCodeLibRepository extends GenericRepository<QrCodeLib> {

    public QrCodeLib findByQrCode(String qrCodeLib);

}
