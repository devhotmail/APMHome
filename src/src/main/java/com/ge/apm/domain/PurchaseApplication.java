package com.ge.apm.domain;

import com.ge.apm.service.utils.TimeUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author 121601104
 */
@Entity
@Table(name = "purchase_application")
public class PurchaseApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Basic(optional = false)
    @Column(name = "apply_clinical")
    private String applyClinical;

    @Basic(optional = false)
    @Column(name = "fill_date")
    @Temporal(TemporalType.DATE)
    private Date fillDate;

    @Basic(optional = false)
    @Column(name = "clinical_contact")
    private String clinicalContact;

    @Basic(optional = false)
    @Column(name = "telephone")
    private String telephone;

    @Basic(optional = false)
    @Column(name = "device")
    private String device;

    @Basic(optional = false)
    @Column(name = "quantity")
    private Integer quantity;

    @Basic(optional = false)
    @Column(name = "intent")
    private Integer intent;

    @Basic(optional = false)
    @Column(name = "predicate_price")
    private Double predicatePrice;

    @Basic(optional = false)
    @Column(name = "needy")
    private boolean needy;

    @Basic(optional = false)
    @Column(name = "apply_feature")
    private int applyFeature;

    @Basic(optional = false)
    @Column(name = "clinical_responser")
    private String clinicalResponser;
    @Basic(optional = false)
    @Column(name = "funding_resource")
    private int fundingResource;
    @Basic(optional = false)
    @Column(name = "recom_ad1")
    private String recomAd1;
    @Basic(optional = false)
    @Column(name = "recom_ad1_tel")
    private String recomAd1Tel;
    @Basic(optional = false)
    @Column(name = "recom_ad2")
    private String recomAd2;
    @Basic(optional = false)
    @Column(name = "recom_ad2_tel")
    private String recomAd2Tel;
    @Basic(optional = false)
    @Column(name = "recom_ad3")
    private String recomAd3;
    @Basic(optional = false)
    @Column(name = "recom_ad3_tel")
    private String recomAd3Tel;

    @Basic(optional = false)
    @Column(name = "info_sign_date")
    @Temporal(TemporalType.DATE)
    private Date infoSignDate;

    @Basic(optional = false)
    @Column(name = "dev_sign_date")
    @Temporal(TemporalType.DATE)
    private Date devSignDate;


    @Basic(optional = false)
    @Column(name = "leader_sign_date")
    @Temporal(TemporalType.DATE)
    private Date leaderSignDate;


    @Basic(optional = false)
    @Column(name = "apply_reason")
    private String applyReason;

    @Basic(optional = false)
    @Column(name = "special_requirement")
    private String specialRequirement;

    @Basic(optional = false)
    @Column(name = "funding_resource_others")
    private String fundingResourceOthers;

////
@Basic(optional = false)
@Column(name = "info_sign")
private String infoSign;

    @Basic(optional = false)
    @Column(name = "dev_sign")
    private String devSign;

    @Basic(optional = false)
    @Column(name = "leader_sign")
    private String leaderSign;


    @Basic(optional = false)
    @Column(name = "info_advice")
    private String infoAdvice;

    @Basic(optional = false)
    @Column(name = "dev_advice")
    private String devAdvice;

    @Basic(optional = false)
    @Column(name = "leader_advice")
    private String leaderAdvice;

    @Column(name = "site_id")
    private Integer siteId;

    @Column(name = "hospital_id")
    private Integer hospitalId;



    public String getFundingResourceOthers() {
        return fundingResourceOthers;
    }

    public void setFundingResourceOthers(String fundingResourceOthers) {
        this.fundingResourceOthers = fundingResourceOthers;
    }

    public PurchaseApplication() {
    }



    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    public String getSpecialRequirement() {
        return specialRequirement;
    }

    public void setSpecialRequirement(String specialRequirement) {
        this.specialRequirement = specialRequirement;
    }

    public Date getInfoSignDate() {
        return infoSignDate;
    }

    public void setInfoSignDate(Date infoSignDate) {
        this.infoSignDate = infoSignDate;
    }

    public Date getDevSignDate() {
        return devSignDate;
    }

    public void setDevSignDate(Date devSignDate) {
        this.devSignDate = devSignDate;
    }

    public Date getLeaderSignDate() {
        return leaderSignDate;
    }

    public void setLeaderSignDate(Date leaderSignDate) {
        this.leaderSignDate = leaderSignDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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
        if (!(object instanceof PurchaseApplication)) {
            return false;
        }
        PurchaseApplication other = (PurchaseApplication) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccountApplication [id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

    public String getApplyClinical() {
        return applyClinical;
    }

    public void setApplyClinical(String applyClinical) {
        this.applyClinical = applyClinical;
    }

    public Date getFillDate() {
        return fillDate;
    }

    public void setFillDate(Date fillDate) {
        this.fillDate = fillDate;
    }

    public String getClinicalContact() {
        return clinicalContact;
    }

    public void setClinicalContact(String clinicalContact) {
        this.clinicalContact = clinicalContact;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPredicatePrice() {
        return predicatePrice;
    }

    public void setPredicatePrice(Double predicatePrice) {
        this.predicatePrice = predicatePrice;
    }

    public boolean getNeedy() {
        return needy;
    }

    public void setNeedy(boolean needy) {
        this.needy = needy;
    }

    public String getClinicalResponser() {
        return clinicalResponser;
    }

    public void setClinicalResponser(String clinicalResponser) {
        this.clinicalResponser = clinicalResponser;
    }

    public String getInfoAdvice() {
        return infoAdvice;
    }

    public void setInfoAdvice(String infoAdvice) {
        this.infoAdvice = infoAdvice;
    }

    public String getDevAdvice() {
        return devAdvice;
    }

    public void setDevAdvice(String devAdvice) {
        this.devAdvice = devAdvice;
    }

    public String getLeaderAdvice() {
        return leaderAdvice;
    }

    public void setLeaderAdvice(String leaderAdvice) {
        this.leaderAdvice = leaderAdvice;
    }

    public int getApplyFeature() {
        return applyFeature;
    }

    public void setApplyFeature(int applyFeature) {
        this.applyFeature = applyFeature;
    }

    public String getRecomAd1() {
        return recomAd1;
    }

    public void setRecomAd1(String recomAd1) {
        this.recomAd1 = recomAd1;
    }

    public String getRecomAd1Tel() {
        return recomAd1Tel;
    }

    public void setRecomAd1Tel(String recomAd1Tel) {
        this.recomAd1Tel = recomAd1Tel;
    }

    public String getRecomAd2() {
        return recomAd2;
    }

    public void setRecomAd2(String recomAd2) {
        this.recomAd2 = recomAd2;
    }

    public String getRecomAd2Tel() {
        return recomAd2Tel;
    }

    public void setRecomAd2Tel(String recomAd2Tel) {
        this.recomAd2Tel = recomAd2Tel;
    }

    public String getRecomAd3() {
        return recomAd3;
    }

    public void setRecomAd3(String recomAd3) {
        this.recomAd3 = recomAd3;
    }

    public String getRecomAd3Tel() {
        return recomAd3Tel;
    }

    public void setRecomAd3Tel(String recomAd3Tel) {
        this.recomAd3Tel = recomAd3Tel;
    }

    public int getFundingResource() {
        return fundingResource;
    }

    public void setFundingResource(int fundingResource) {
        this.fundingResource = fundingResource;
    }

    public Integer getIntent() {
        return intent;
    }

    public void setIntent(Integer intent) {
        this.intent = intent;
    }

    public String getInfoSign() {
        return infoSign;
    }

    public void setInfoSign(String infoSign) {
        this.infoSign = infoSign;
    }

    public String getDevSign() {
        return devSign;
    }

    public void setDevSign(String devSign) {
        this.devSign = devSign;
    }

    public String getLeaderSign() {
        return leaderSign;
    }

    public void setLeaderSign(String leaderSign) {
        this.leaderSign = leaderSign;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }
}