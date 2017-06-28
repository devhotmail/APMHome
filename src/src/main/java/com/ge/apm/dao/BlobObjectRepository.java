package com.ge.apm.dao;

import com.ge.apm.domain.V2_BlobObject;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;


public interface BlobObjectRepository extends GenericRepositoryUUID<V2_BlobObject> {
    
    public V2_BlobObject findByObjectStorageId(String objectId);

}
