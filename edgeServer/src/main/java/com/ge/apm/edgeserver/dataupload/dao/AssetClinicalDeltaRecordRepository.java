package com.ge.apm.edgeserver.dataupload.dao;

import com.ge.apm.edgeserver.dataupload.entity.AssetClinicalRecordDelta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 212547631
 */
@Repository
public interface AssetClinicalDeltaRecordRepository extends CrudRepository<AssetClinicalRecordDelta, Integer> {
}