package com.ge.apm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "v2_work_order_detail")
public class V2_WorkOrder_Detail implements Serializable {

    private static final long serialVersionUID = 1L;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Basic(optional = false)
//    @Column(name = "id")
//    private Integer id;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @NotNull
    @Column(name = "wo_id")
    private String woId;

    @Column(name = "man_hours")
    private Integer manHours;

    @Size(max = 60)
    @Column(name = "parts")
    private String parts;

    @Column(name = "parts_quantity")
    private Integer apartsQuantity;

    @Column(name = "parts_price")
    private Double partsPrice;

    @Column(name = "other_expense")
    private Double otherExpense;

    @Column(name = "cowoker_user_id")
    private Integer cowokerUserId;

    @Column(name = "cowoker_user_name")
    private String cowokerUserName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWoId() {
        return woId;
    }

    public void setWoId(String woId) {
        this.woId = woId;
    }

    public Integer getManHours() {
        return manHours;
    }

    public void setManHours(Integer manHours) {
        this.manHours = manHours;
    }

    public String getParts() {
        return parts;
    }

    public void setParts(String parts) {
        this.parts = parts;
    }

    public Integer getApartsQuantity() {
        return apartsQuantity;
    }

    public void setApartsQuantity(Integer apartsQuantity) {
        this.apartsQuantity = apartsQuantity;
    }

    public Double getPartsPrice() {
        return partsPrice;
    }

    public void setPartsPrice(Double partsPrice) {
        this.partsPrice = partsPrice;
    }

    public Double getOtherExpense() {
        return otherExpense;
    }

    public void setOtherExpense(Double otherExpense) {
        this.otherExpense = otherExpense;
    }

    public Integer getCowokerUserId() {
        return cowokerUserId;
    }

    public void setCowokerUserId(Integer cowokerUserId) {
        this.cowokerUserId = cowokerUserId;
    }

    public String getCowokerUserName() {
        return cowokerUserName;
    }

    public void setCowokerUserName(String cowokerUserName) {
        this.cowokerUserName = cowokerUserName;
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
        if (!(object instanceof V2_WorkOrder_Detail)) {
            return false;
        }
        V2_WorkOrder_Detail other = (V2_WorkOrder_Detail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
