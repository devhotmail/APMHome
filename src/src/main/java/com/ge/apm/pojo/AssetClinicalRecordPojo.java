package com.ge.apm.pojo;

import java.util.Date;

/**
 * Created by lsg on 22/2/2017.
 */
public class AssetClinicalRecordPojo {

    private int siteIds;
    private int hospitalIds;
    private  int assetIds;
    private Date examDate;
    private Long examCount;

    private Long examDurations;
    private Double priceAmounts;
    private Double injectCounts;
    private Double exposeCounts;
    private Long filmCounts;

    public AssetClinicalRecordPojo(int siteIds, int hospitalIds, int assetIds,Date examDate,long examCount, Long examDurations, Double priceAmounts, Double injectCounts, Double exposeCounts, Long filmCounts) {
        this.siteIds = siteIds;
        this.hospitalIds = hospitalIds;
        this.assetIds = assetIds;
        this.examDate=examDate;
        this.examCount=examCount;

        this.examDurations = examDurations;
        this.priceAmounts = priceAmounts;
        this.injectCounts = injectCounts;
        this.exposeCounts = exposeCounts;
        this.filmCounts = filmCounts;

    }

    public Long getExamCount() {
        return examCount;
    }

    public void setExamCount(Long examCount) {
        this.examCount = examCount;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public int getSiteIds() {
        return siteIds;
    }

    public void setSiteIds(int siteIds) {
        this.siteIds = siteIds;
    }

    public int getHospitalIds() {
        return hospitalIds;
    }

    public void setHospitalIds(int hospitalIds) {
        this.hospitalIds = hospitalIds;
    }

    public int getAssetIds() {
        return assetIds;
    }

    public void setAssetIds(int assetIds) {
        this.assetIds = assetIds;
    }

    public Long getExamDurations() {
        return examDurations;
    }

    public void setExamDurations(Long examDurations) {
        this.examDurations = examDurations;
    }

    public Double getPriceAmounts() {
        return priceAmounts;
    }

    public void setPriceAmounts(Double priceAmounts) {
        this.priceAmounts = priceAmounts;
    }

    public Double getInjectCounts() {
        return injectCounts;
    }

    public void setInjectCounts(Double injectCounts) {
        this.injectCounts = injectCounts;
    }

    public Double getExposeCounts() {
        return exposeCounts;
    }

    public void setExposeCounts(Double exposeCounts) {
        this.exposeCounts = exposeCounts;
    }

    public Long getFilmCounts() {
        return filmCounts;
    }

    public void setFilmCounts(Long filmCounts) {
        this.filmCounts = filmCounts;
    }
}
