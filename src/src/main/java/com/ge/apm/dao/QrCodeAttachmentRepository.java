package com.ge.apm.dao;

import com.ge.apm.domain.QrCodeAttachment;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

/**
 * Created by 212605082 on 2017/3/22.
 */
public interface QrCodeAttachmentRepository extends GenericRepository<QrCodeAttachment> {

    @Query("select att from QrCodeAttachment att , QrCodeLib lib where att.qrCodeId=lib.id and lib.status='2' ")
    public List<QrCodeAttachment> getNoneAssetAttachments();
    
    
}
