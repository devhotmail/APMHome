package com.ge.apm.pojo;

/**
 * Created by lsg on 22/2/2017.
 */
public class AssetClinicalRecordPojo {




    private int siteIds;
    private int hospitalIds;
    private  int assetIds;

    private Long examDurations;
    private Double priceAmounts;
    private Double injectCounts;
    private Double exposeCounts;
    private Long filmCounts;

    public AssetClinicalRecordPojo(int siteIds, int hospitalIds, int assetIds, Long examDurations, Double priceAmounts, Double injectCounts, Double exposeCounts, Long filmCounts) {
        this.siteIds = siteIds;
        this.hospitalIds = hospitalIds;
        this.assetIds = assetIds;
        this.examDurations = examDurations;
        this.priceAmounts = priceAmounts;
        this.injectCounts = injectCounts;
        this.exposeCounts = exposeCounts;
        this.filmCounts = filmCounts;
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
