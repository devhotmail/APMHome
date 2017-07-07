package com.ge.apm.dao;

import com.ge.apm.domain.AssetFileAttachment;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;
import java.util.List;

public interface AssetFileAttachmentRepository extends GenericRepository<AssetFileAttachment> {

    @Query("select att from AssetFileAttachment att where att.fileUrl is null or  att.fileUrl='' ")
    public List<AssetFileAttachment> getNeedMigrateAttachments();
}
