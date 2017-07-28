/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "asset_info")
public class AssetInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Column(columnDefinition = "CHAR(32)")
    private String uid;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private int siteId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 64)
    @Column(name = "alias_name")
    private String aliasName;
    @Column(name = "function_group")
    private Integer functionGroup;
    @Column(name = "function_type")
    private String functionType;
    @Column(name = "function_grade")
    private Integer functionGrade;
    @Size(max = 64)
    @Column(name = "manufacture")
    private String manufacture;
    @Size(max = 64)
    @Column(name = "vendor")
    private String vendor;
    @Column(name = "supplier_id")
    private Integer supplierId;
    @Size(max = 64)
    @Column(name = "maitanance")
    private String maitanance;
    @Size(max = 32)
    @Column(name = "maitanance_tel")
    private String maitananceTel;
    @Size(max = 64)
    @Column(name = "serial_num")
    private String serialNum;
    @Size(max = 64)
    @Column(name = "depart_num")
    private String departNum;
    @Size(max = 64)
    @Column(name = "financing_num")
    private String financingNum;
    @Size(max = 64)
    @Column(name = "barcode")
    private String barcode;
    @Size(max = 64)
    @Column(name = "modality_id")
    private String modalityId;
    @Size(max = 64)
    @Column(name = "location_code")
    private String locationCode;
    @Size(max = 64)
    @Column(name = "location_name")
    private String locationName;
    @Column(name = "clinical_dept_id")
    private Integer clinicalDeptId;//设备使用科室
    @Column(name = "clinical_dept_uid" , columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备使用科室
    @Size(max = 64)
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;
    @Column(name = "asset_group")
    private Integer assetGroup;
    @Column(name = "asset_dept_id")
    private int assetDeptId;//设备所属科室
    @Column(name = "asset_owner_id")
    private Integer assetOwnerId;
    @Size(min = 1, max = 32)
    @Column(name = "asset_owner_name")
    private String assetOwnerName;
    @Size(max = 16)
    @Column(name = "asset_owner_tel")
    private String assetOwnerTel;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_valid")
    private boolean isValid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private int status;
    @Column(name = "manufact_date")
    @Temporal(TemporalType.DATE)
    private Date manufactDate;
    @Column(name = "purchase_date")
    @Temporal(TemporalType.DATE)
    private Date purchaseDate;
    @Column(name = "arrive_date")
    @Temporal(TemporalType.DATE)
    private Date arriveDate;
    @Column(name = "install_date")
    @Temporal(TemporalType.DATE)
    private Date installDate;
    @Column(name = "warranty_date")
    @Temporal(TemporalType.DATE)
    private Date warrantyDate;
    @Column(name = "terminate_date")
    @Temporal(TemporalType.DATE)
    private Date terminateDate;
    @Column(name = "last_pm_date")
    @Temporal(TemporalType.DATE)
    private Date lastPmDate;
    @Column(name = "last_metering_date")
    @Temporal(TemporalType.DATE)
    private Date lastMeteringDate;
    @Column(name = "last_qa_date")
    @Temporal(TemporalType.DATE)
    private Date lastQaDate;
    @Column(name = "last_stocktake_date")
    @Temporal(TemporalType.DATE)
    private Date lastStockTakeDate;
    @Column(name = "clinical_owner_id")
    private Integer clinicalOwnerId;
    @Size(max = 32)
    @Column(name = "clinical_owner_name")
    private String clinicalOwnerName;
    @Size(max = 16)
    @Column(name = "clinical_owner_tel")
    private String clinicalOwnerTel;
    @Size(max = 64)
    @Column(name = "registration_no")
    private String registrationNo;
    @Column(name = "factory_warranty_date")
    @Temporal(TemporalType.DATE)
    private Date factoryWarrantyDate;
    @Size(max = 256)
    @Column(name = "qr_code")
    private String qrCode;
    
    @Column(name = "asset_owner_id2")
    private Integer assetOwnerId2;
    @Column(name = "asset_owner_name2")
    private String assetOwnerName2;
    @Column(name = "asset_owner_tel2")
    private String assetOwnerTel2;
    @Column(name = "fe_user_id")
    private Integer feUserId;
    @Column(name = "dispatch_mode")
    private Integer dispatchMode;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "purchase_price")
    private Double purchasePrice;
    @Column(name = "salvage_value")
    private Double salvageValue;
    @Column(name = "lifecycle")
    private Integer lifecycle;
    @Column(name = "depreciation_method")
    private Integer depreciationMethod;

    @Column(name = "eam_id")
    private String eamId;
    @Column(name = "system_id")
    private String systemId;
    @Column(name = "system_num1")
    private String systemNum1;
    @Column(name = "system_num2")
    private String systemNum2;
    @Column(name = "system_num3")
    private String systemNum3;
    @Column(name = "system_num4")
    private String systemNum4;
    @Column(name = "system_num5")
    private String systemNum5;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "parent_asset_id")
    private Integer parentAssetId;

    @Column(name = "inventory_status")
    private Integer inventoryStatus;
    @Column(name = "asset_code68")
    private String assetCode68;
    
    @Column(name = "hospital_id")
    @Basic(optional = false)
    @NotNull
    private Integer hospitalId;
    
    @JoinColumn(insertable=false,updatable=false, name = "hospital_id", referencedColumnName = "id")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private OrgInfo hospital;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }
    
    public AssetInfo() {
    }

    public AssetInfo(Integer id) {
        this.id = id;
    }

    public AssetInfo(Integer id, int siteId, String name, int assetDeptId, int assetOwnerId, String assetOwnerName, boolean isValid, int status) {
        this.id = id;
        this.siteId = siteId;
        this.name = name;
        this.assetDeptId = assetDeptId;
        this.assetOwnerId = assetOwnerId;
        this.assetOwnerName = assetOwnerName;
        this.isValid = isValid;
        this.status = status;
    }

    @PrePersist
    public void initializeUid() {
        if(uid==null)
            uid = UUID.randomUUID().toString().replace("-", "");
        
        if(isDeleted == null){
            isDeleted = false;
        }
        if(supplierId==null)
            supplierId = -1;
    }
    
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public OrgInfo getHospital() {
        return hospital;
    }

    public void setHospital(OrgInfo hospital) {
        this.hospital = hospital;
    }

    public Integer getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(Integer functionGroup) {
        this.functionGroup = functionGroup;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public Integer getFunctionGrade() {
        return functionGrade;
    }

    public void setFunctionGrade(Integer functionGrade) {
        this.functionGrade = functionGrade;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getMaitanance() {
        return maitanance;
    }

    public void setMaitanance(String maitanance) {
        this.maitanance = maitanance;
    }

    public String getMaitananceTel() {
        return maitananceTel;
    }

    public void setMaitananceTel(String maitananceTel) {
        this.maitananceTel = maitananceTel;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getDepartNum() {
        return departNum;
    }

    public void setDepartNum(String departNum) {
        this.departNum = departNum;
    }

    public String getFinancingNum() {
        return financingNum;
    }

    public void setFinancingNum(String financingNum) {
        this.financingNum = financingNum;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getModalityId() {
        return modalityId;
    }

    public void setModalityId(String modalityId) {
        this.modalityId = modalityId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getClinicalDeptId() {
        return clinicalDeptId;
    }

    public void setClinicalDeptId(Integer clinicalDeptId) {
        this.clinicalDeptId = clinicalDeptId;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public int getAssetDeptId() {
        return assetDeptId;
    }

    public void setAssetDeptId(int assetDeptId) {
        this.assetDeptId = assetDeptId;
    }

    public Integer getAssetOwnerId() {
        return assetOwnerId;
    }

    public void setAssetOwnerId(Integer assetOwnerId) {
        this.assetOwnerId = assetOwnerId;
    }

    public String getAssetOwnerName() {
        return assetOwnerName;
    }

    public void setAssetOwnerName(String assetOwnerName) {
        this.assetOwnerName = assetOwnerName;
    }

    public String getAssetOwnerTel() {
        return assetOwnerTel;
    }

    public void setAssetOwnerTel(String assetOwnerTel) {
        this.assetOwnerTel = assetOwnerTel;
    }

    public boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getManufactDate() {
        return manufactDate;
    }

    public void setManufactDate(Date manufactDate) {
        this.manufactDate = manufactDate;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public Date getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Date installDate) {
        this.installDate = installDate;
    }

    public Date getWarrantyDate() {
        return warrantyDate;
    }

    public void setWarrantyDate(Date warrantyDate) {
        this.warrantyDate = warrantyDate;
    }

    public Date getTerminateDate() {
        return terminateDate;
    }

    public void setTerminateDate(Date terminateDate) {
        this.terminateDate = terminateDate;
    }

    public Date getLastPmDate() {
        return lastPmDate;
    }

    public void setLastPmDate(Date lastPmDate) {
        this.lastPmDate = lastPmDate;
    }

    public Date getLastMeteringDate() {
        return lastMeteringDate;
    }

    public void setLastMeteringDate(Date lastMeteringDate) {
        this.lastMeteringDate = lastMeteringDate;
    }

    public Date getLastQaDate() {
        return lastQaDate;
    }

    public void setLastQaDate(Date lastQaDate) {
        this.lastQaDate = lastQaDate;
    }

    public Date getLastStockTakeDate() {
        return lastStockTakeDate;
    }

    public void setLastStockTakeDate(Date lastStockTakeDate) {
        this.lastStockTakeDate = lastStockTakeDate;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getSalvageValue() {
        return salvageValue;
    }

    public void setSalvageValue(Double salvageValue) {
        this.salvageValue = salvageValue;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Integer getDepreciationMethod() {
        return depreciationMethod;
    }

    public void setDepreciationMethod(Integer depreciationMethod) {
        this.depreciationMethod = depreciationMethod;
    }

    public Double getLifecycleInYear() {
    	if(lifecycle == null){
    		return null;
    	}
        return new Double(lifecycle)/12.0;
    }

    public void setLifecycleInYear(Double lifecycle) {
    	if(lifecycle == null){
    		return;
    	}
       this.lifecycle = new Double(lifecycle * 12).intValue();
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public Date getFactoryWarrantyDate() {
        return factoryWarrantyDate;
    }

    public void setFactoryWarrantyDate(Date factoryWarrantyDate) {
        this.factoryWarrantyDate = factoryWarrantyDate;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getAssetOwnerId2() {
        return assetOwnerId2;
    }

    public void setAssetOwnerId2(Integer assetOwnerId2) {
        this.assetOwnerId2 = assetOwnerId2;
    }

    public String getAssetOwnerName2() {
        return assetOwnerName2;
    }

    public void setAssetOwnerName2(String assetOwnerName2) {
        this.assetOwnerName2 = assetOwnerName2;
    }

    public String getAssetOwnerTel2() {
        return assetOwnerTel2;
    }

    public void setAssetOwnerTel2(String assetOwnerTel2) {
        this.assetOwnerTel2 = assetOwnerTel2;
    }

    public Integer getFeUserId() {
        return feUserId;
    }

    public void setFeUserId(Integer feUserId) {
        this.feUserId = feUserId;
    }

    public Integer getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(Integer dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

    public Integer getClinicalOwnerId() {
        return clinicalOwnerId;
    }

    public void setClinicalOwnerId(Integer clinicalOwnerId) {
        this.clinicalOwnerId = clinicalOwnerId;
    }

    public String getClinicalOwnerName() {
        return clinicalOwnerName;
    }

    public void setClinicalOwnerName(String clinicalOwnerName) {
        this.clinicalOwnerName = clinicalOwnerName;
    }

    public String getClinicalOwnerTel() {
        return clinicalOwnerTel;
    }

    public void setClinicalOwnerTel(String clinicalOwnerTel) {
        this.clinicalOwnerTel = clinicalOwnerTel;
    }

    public String getEamId() {
        return eamId;
    }

    public void setEamId(String eamId) {
        this.eamId = eamId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSystemNum1() {
        return systemNum1;
    }

    public void setSystemNum1(String systemNum1) {
        this.systemNum1 = systemNum1;
    }

    public String getSystemNum2() {
        return systemNum2;
    }

    public void setSystemNum2(String systemNum2) {
        this.systemNum2 = systemNum2;
    }

    public String getSystemNum3() {
        return systemNum3;
    }

    public void setSystemNum3(String systemNum3) {
        this.systemNum3 = systemNum3;
    }

    public String getSystemNum4() {
        return systemNum4;
    }

    public void setSystemNum4(String systemNum4) {
        this.systemNum4 = systemNum4;
    }

    public String getSystemNum5() {
        return systemNum5;
    }

    public void setSystemNum5(String systemNum5) {
        this.systemNum5 = systemNum5;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getParentAssetId() {
        return parentAssetId;
    }

    public void setParentAssetId(Integer parentAssetId) {
        this.parentAssetId = parentAssetId;
    }

    public Integer getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(Integer inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public String getAssetCode68() {
        return assetCode68;
    }

    public void setAssetCode68(String assetCode68) {
        this.assetCode68 = assetCode68;
    }

    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssetInfo)) {
            return false;
        }
        AssetInfo other = (AssetInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
