package com.ge.apm.domain;

import java.io.Serializable;

/**
 * Created by lsg on 25/3/2017.
 */
public class AssetSummitMaxMinPojo implements Serializable {
    Integer assetId;
    double npMax;
    double npMin;
    double icMax;
    double icMin;
    double ecMax;
    double ecMin;
    double fmMax;
    double fmMin;
    private static final long serialVersionUID = -1L;

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    /*public AssetSummitMaxMinPojo(Integer assetId) {
        this.assetId = assetId;
    }*/

    public double getNpMax() {
        return npMax;
    }

    public void setNpMax(double npMax) {
        this.npMax = npMax;
    }

    public double getNpMin() {
        return npMin;
    }

    public void setNpMin(double npMin) {
        this.npMin = npMin;
    }

    public double getIcMax() {
        return icMax;
    }

    public void setIcMax(double icMax) {
        this.icMax = icMax;
    }

    public double getIcMin() {
        return icMin;
    }

    public void setIcMin(double icMin) {
        this.icMin = icMin;
    }

    public double getEcMax() {
        return ecMax;
    }

    public void setEcMax(double ecMax) {
        this.ecMax = ecMax;
    }

    public double getEcMin() {
        return ecMin;
    }

    public void setEcMin(double ecMin) {
        this.ecMin = ecMin;
    }

    public double getFmMax() {
        return fmMax;
    }

    public void setFmMax(double fmMax) {
        this.fmMax = fmMax;
    }

    public double getFmMin() {
        return fmMin;
    }

    public void setFmMin(double fmMin) {
        this.fmMin = fmMin;
    }

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AssetSummitMaxMinPojo [id=");
        builder.append(id);
        builder.append(", assetId=");
        builder.append(assetId);


        builder.append("]");
        return builder.toString();
    }
}
