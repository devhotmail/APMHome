/*
 */
package com.ge.apm.domain;

import com.ge.apm.service.utils.TimeUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author 121601104
 */
@Entity
@Table(name = "account_application")
public class PurchaseApplication implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "apply_clinical")
    private Date applyClinical;

    @Basic(optional = false)
     @Temporal(TemporalType.DATE)
    @Column(name = "fill_date")
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
    private int quantity;


@Basic(optional = false)
    @Column(name = "predicate_price")
    private Double predicatePrice;

    @Basic(optional = false)
    @Column(name = "needy")
    private String needy;

    @Basic(optional = false)
	@Column(name = "clinical_responser")
    private String clinicalResponser;





    @Basic(optional = false)
    @Column(name = "info_advice")
    private String infoAdvice;


    @Basic(optional = false)
    @Column(name = "dev_advice")
    private String devAdvice;

    @Basic(optional = false)
    @Column(name = "leader_advice")
    private String leaderAdvice;





    public PurchaseApplication() {
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Date getApplyClinical() {
        return applyClinical;
    }

    public void setApplyClinical(Date applyClinical) {
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getPredicatePrice() {
        return predicatePrice;
    }

    public void setPredicatePrice(Double predicatePrice) {
        this.predicatePrice = predicatePrice;
    }

    public String getNeedy() {
        return needy;
    }

    public void setNeedy(String needy) {
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
}
