package com.ge.apm.dao;

import com.ge.apm.domain.PurchaseApplication;
import com.ge.apm.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface PurchaseApplicationRepository extends GenericRepositoryUUID<PurchaseApplication> {


}
