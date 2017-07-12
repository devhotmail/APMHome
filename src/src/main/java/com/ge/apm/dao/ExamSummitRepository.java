package com.ge.apm.dao;

import com.ge.apm.domain.ExamSummit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.Date;
import java.util.List;

public interface ExamSummitRepository extends GenericRepository<ExamSummit> {

    @Query("select exam from ExamSummit exam where exam.assetId = :assetId and exam.created = :created and exam.hospitalId = :hospitalId and exam.partId = :partId and exam.siteId = :siteId")
    public ExamSummit getExamSummitByAssetIdAndCreated(@Param("assetId") int assetId,@Param("hospitalId") int hospitalId,@Param("partId") int partId,@Param("siteId") int siteId, @Param("created") Date date);



}