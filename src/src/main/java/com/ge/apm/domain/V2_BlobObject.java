package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "v2_blob_object")
public class V2_BlobObject implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String BO_TYPE_ServiceRequest = "ServiceRequest";
    public static final String BO_TYPE_WorkOrder = "WorkOrder";
    public static final String BO_TYPE_WorkOrderStep = "WorkOrderStep";
    public static final String BO_TYPE_WorkOrderDetail = "WorkOrderDetail";
    public static final String BO_TYPE_InspectionOrder = "InspectionOrder";
    public static final String BO_TYPE_AssetInfo = "AssetInfo";
    public static final String BO_TYPE_AssetContract = "AssetContract";
    public static final String BO_TYPE_QRCodeLib = "QRCodeLib";
    public static final String BO_TYPE_PmOrder = "PmOrder";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "bo_uuid")
    private String boUuid;

    @Column(name = "bo_id")
    private Integer boId;

    @Column(name = "bo_type")
    private String boType;
    
    @Column(name = "bo_sub_type")
    private String boSubType;

    @Column(name = "object_storage_id")
    private String objectStorageId;

    @Column(name = "object_name")
    private String objectName;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "object_size")
    private Long objectSize;

    @Column(name = "object_source")
    private V2_BlobObject_Source objectSource = V2_BlobObject_Source.STORED_IN_DB;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoUuid() {
        return boUuid;
    }

    public void setBoUuid(String boUuid) {
        this.boUuid = boUuid;
    }

    public Integer getBoId() {
        return boId;
    }

    public void setBoId(Integer boId) {
        this.boId = boId;
    }

    public String getObjectStorageId() {
        return objectStorageId;
    }

    public void setObjectStorageId(String objectStorageId) {
        this.objectStorageId = objectStorageId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Long getObjectSize() {
        return objectSize;
    }

    public void setObjectSize(Long objectSize) {
        this.objectSize = objectSize;
    }

    public V2_BlobObject_Source getObjectSource() {
        return objectSource;
    }

    public void setObjectSource(V2_BlobObject_Source objectSource) {
        this.objectSource = objectSource;
    }

    public String getBoType() {
        return boType;
    }

    public void setBoType(String boType) {
        this.boType = boType;
    }

    public String getBoSubType() {
        return boSubType;
    }

    public void setBoSubType(String boSubType) {
        this.boSubType = boSubType;
    }

    
    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_BlobObject)) {
            return false;
        }
        V2_BlobObject other = (V2_BlobObject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
