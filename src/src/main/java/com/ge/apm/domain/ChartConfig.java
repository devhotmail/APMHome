/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "chart_config")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class ChartConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String CHART_PIE ="pie";
    public static final String CHART_BAR ="bar";
    public static final String CHART_LINE ="line";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(max = 30)
    @Column(name = "chart_name")
    private String chartName;
    @Basic(optional = false)
    @NotNull
    @Size(max = 500)
    @Column(name = "chart_data_sql")
    private String chartDataSql;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_default_empty_result")
    private boolean isDefaultEmptyResult;
    @Basic(optional = false)
    @NotNull
    @Size(max = 20)
    @Column(name = "chart_type")
    private String chartType;
    @Basic(optional = false)
    @NotNull
    @Size(max = 20)
    @Column(name = "series_data_field")
    private String seriesDataField;
    @Basic(optional = false)
    @NotNull
    @Size(max = 20)
    @Column(name = "x_axis_data_field")
    private String xAxisDataField;
    @Basic(optional = false)
    @NotNull
    @Size(max = 20)
    @Column(name = "y_axis_data_field")
    private String yAxisDataField;
    @Size(max = 40)
    @Column(name = "chart_title")
    private String chartTitle;
    @Size(max = 1)
    @Column(name = "legend_position")
    private String legendPosition;
    @Column(name = "legend_rows")
    private Integer legendRows;
    @Column(name = "legend_cols")
    private Integer legendCols;
    @Column(name = "zoom_enabled")
    private boolean zoomEnabled;
    @Size(max = 20)
    @Column(name = "x_axis_label")
    private String xAxisLabel;
    @Size(max = 20)
    @Column(name = "x_axis_min")
    private String xAxisMin;
    @Size(max = 20)
    @Column(name = "x_axis_max")
    private String xAxisMax;
    @Size(max = 20)
    @Column(name = "x_axis_tick_angle")
    private String xAxisTickAngle;
    @Size(max = 20)
    @Column(name = "x_axis_tick_count")
    private String xAxisTickCount;
    @Size(max = 20)
    @Column(name = "x_axis_tick_format")
    private String xAxisTickFormat;
    @Size(max = 20)
    @Column(name = "x_axis_tick_interval")
    private String xAxisTickInterval;
    @Size(max = 20)
    @Column(name = "y_axis_label")
    private String yAxisLabel;
    @Size(max = 20)
    @Column(name = "y_axis_min")
    private String yAxisMin;
    @Size(max = 20)
    @Column(name = "y_axis_max")
    private String yAxisMax;
    @Basic(optional = false)
    @NotNull
    @Column(name = "add_date_axis")
    private boolean addDateAxis;

    public ChartConfig() {
    }

    public ChartConfig(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public String getChartDataSql() {
        return chartDataSql;
    }

    public void setChartDataSql(String chartDataSql) {
        this.chartDataSql = chartDataSql;
    }

    public boolean getIsDefaultEmptyResult() {
        return isDefaultEmptyResult;
    }

    public void setIsDefaultEmptyResult(boolean isDefaultEmptyResult) {
        this.isDefaultEmptyResult = isDefaultEmptyResult;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getSeriesDataField() {
        return seriesDataField;
    }

    public void setSeriesDataField(String seriesDataField) {
        this.seriesDataField = seriesDataField;
    }

    public String getxAxisDataField() {
        return xAxisDataField;
    }

    public void setxAxisDataField(String xAxisDataField) {
        this.xAxisDataField = xAxisDataField;
    }

    public String getyAxisDataField() {
        return yAxisDataField;
    }

    public void setyAxisDataField(String yAxisDataField) {
        this.yAxisDataField = yAxisDataField;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public String getLegendPosition() {
        return legendPosition;
    }

    public void setLegendPosition(String legendPosition) {
        this.legendPosition = legendPosition;
    }

    public Integer getLegendRows() {
        return legendRows;
    }

    public void setLegendRows(Integer legendRows) {
        this.legendRows = legendRows;
    }

    public Integer getLegendCols() {
        return legendCols;
    }

    public void setLegentCols(Integer legendCols) {
        this.legendCols = legendCols;
    }

    public boolean getZoomEnabled() {
        return zoomEnabled;
    }

    public void setZoomEnabled(boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
    }

    public String getxAxisLabel() {
        return xAxisLabel;
    }

    public void setxAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public String getxAxisMin() {
        return xAxisMin;
    }

    public void setxAxisMin(String xAxisMin) {
        this.xAxisMin = xAxisMin;
    }

    public String getxAxisMax() {
        return xAxisMax;
    }

    public void setxAxisMax(String xAxisMax) {
        this.xAxisMax = xAxisMax;
    }

    public String getxAxisTickAngle() {
        return xAxisTickAngle;
    }

    public void setxAxisTickAngle(String xAxisTickAngle) {
        this.xAxisTickAngle = xAxisTickAngle;
    }

    public String getxAxisTickCount() {
        return xAxisTickCount;
    }

    public void setxAxisTickCount(String xAxisTickCount) {
        this.xAxisTickCount = xAxisTickCount;
    }

    public String getxAxisTickFormat() {
        return xAxisTickFormat;
    }

    public void setxAxisTickFormat(String xAxisTickFormat) {
        this.xAxisTickFormat = xAxisTickFormat;
    }

    public String getxAxisTickInterval() {
        return xAxisTickInterval;
    }

    public void setxAxisTickInterval(String xAxisTickInterval) {
        this.xAxisTickInterval = xAxisTickInterval;
    }

    public String getyAxisLabel() {
        return yAxisLabel;
    }

    public void setyAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    public String getyAxisMin() {
        return yAxisMin;
    }

    public void setyAxisMin(String yAxisMin) {
        this.yAxisMin = yAxisMin;
    }

    public String getyAxisMax() {
        return yAxisMax;
    }

    public void setyAxisMax(String yAxisMax) {
        this.yAxisMax = yAxisMax;
    }

    public boolean getAddDateAxis() {
        return addDateAxis;
    }

    public void setAddDateAxis(boolean addDateAxis) {
        this.addDateAxis = addDateAxis;
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
        if (!(object instanceof ChartConfig)) {
            return false;
        }
        ChartConfig other = (ChartConfig) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "ChartConfig[ id=" + id + " ]";
    }
    
}
