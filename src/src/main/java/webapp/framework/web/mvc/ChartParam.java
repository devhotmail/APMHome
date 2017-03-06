package webapp.framework.web.mvc;

import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author 212547631
 */
public class ChartParam {
    protected static final Logger logger = Logger.getLogger(ChartParam.class);

    public static final String CHART_PIE ="Pie";
    public static final String CHART_BAR ="Bar";
    public static final String CHART_LINE ="Line";
    
    private String chartType;
    private String chartTitle;
    private String legendPosition;
    private String legendCols;
    private String legendRows;
    private boolean zoomEnabled;
    private String seriesDataField;
    private String xAxisLabel;
    private String xAxisDataField;
    private Object xAxisMin;
    private Object xAxisMax;
    private Integer xAxisTickAngle;
    private Integer xAxisTickCount;
    private String xAxisTickFormat;
    private String xAsixTickInterval;

    private String yAxisLabel;
    private String yAxisDataField;
    private Object yAxisMin;
    private Object yAxisMax;

    private boolean addDateAxis;

    private String chartDataSql;
    
    public String getChartType() {
        return chartType;
    }

    public String getChartDataSql() {
        return chartDataSql;
    }

    public void setChartDataSql(String chartDataSql) {
        this.chartDataSql = chartDataSql;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
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

    public String getLegendCols() {
        return legendCols;
    }

    public void setLegendCols(String legendCols) {
        this.legendCols = legendCols;
    }

    public String getLegendRows() {
        return legendRows;
    }

    public void setLegendRows(String legendRows) {
        this.legendRows = legendRows;
    }

    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    public void setZoomEnabled(boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
    }

    public String getSeriesDataField() {
        return seriesDataField;
    }

    public void setSeriesDataField(String seriesDataField) {
        this.seriesDataField = seriesDataField;
    }

    public String getxAxisLabel() {
        return xAxisLabel;
    }

    public void setxAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public String getxAxisDataField() {
        return xAxisDataField;
    }

    public void setxAxisDataField(String xAxisDataField) {
        this.xAxisDataField = xAxisDataField;
    }

    public Object getxAxisMin() {
        return xAxisMin;
    }

    public void setxAxisMin(Object xAxisMin) {
        this.xAxisMin = xAxisMin;
    }

    public Object getxAxisMax() {
        return xAxisMax;
    }

    public void setxAxisMax(Object xAxisMax) {
        this.xAxisMax = xAxisMax;
    }

    public Integer getxAxisTickAngle() {
        return xAxisTickAngle;
    }

    public void setxAxisTickAngle(Integer xAxisTickAngle) {
        this.xAxisTickAngle = xAxisTickAngle;
    }

    public Integer getxAxisTickCount() {
        return xAxisTickCount;
    }

    public void setxAxisTickCount(Integer xAxisTickCount) {
        this.xAxisTickCount = xAxisTickCount;
    }

    public String getxAxisTickFormat() {
        return xAxisTickFormat;
    }

    public void setxAxisTickFormat(String xAxisTickFormat) {
        this.xAxisTickFormat = xAxisTickFormat;
    }

    public String getxAsixTickInterval() {
        return xAsixTickInterval;
    }

    public void setxAsixTickInterval(String xAsixTickInterval) {
        this.xAsixTickInterval = xAsixTickInterval;
    }

    public String getyAxisLabel() {
        return yAxisLabel;
    }

    public void setyAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    public String getyAxisDataField() {
        return yAxisDataField;
    }

    public void setyAxisDataField(String yAxisDataField) {
        this.yAxisDataField = yAxisDataField;
    }

    public Object getyAxisMin() {
        return yAxisMin;
    }

    public void setyAxisMin(Object yAxisMin) {
        this.yAxisMin = yAxisMin;
    }

    public Object getyAxisMax() {
        return yAxisMax;
    }

    public void setyAxisMax(Object yAxisMax) {
        this.yAxisMax = yAxisMax;
    }

    public boolean isAddDateAxis() {
        return addDateAxis;
    }

    public void setAddDateAxis(boolean addDateAxis) {
        this.addDateAxis = addDateAxis;
    }

    private String getParamAsString(Map<String, Object> chartParam, String paramName){
        Object value = chartParam.get(paramName);
        if(value!=null)
            return value.toString();
        else
            return null;
    }

    private Integer getParamAsInt(Map<String, Object> chartParam, String paramName){
        String value = getParamAsString(chartParam, paramName);

        if(value!=null){
            try{
                return Integer.parseInt(value);
            }
            catch(Exception ex){
                logger.error("error parsing chart param: "+paramName + "="+value);
            }
        }
        
        return null;
    }
    
    public ChartParam(Map<String, Object> chartParam) {
        chartType = getParamAsString(chartParam, "chart_type");
        
        chartTitle = getParamAsString(chartParam, "chart_title");
        legendPosition = getParamAsString(chartParam, "legend_position");
        zoomEnabled = "0".equals(getParamAsString(chartParam, "zoom_enabled"));

        seriesDataField = getParamAsString(chartParam, "series_data_field");

        xAxisLabel = getParamAsString(chartParam, "x_axis_label");
        xAxisDataField = getParamAsString(chartParam, "x_axis_data_field");
        xAxisMin = getParamAsString(chartParam, "x_axis_min");
        xAxisMax = getParamAsString(chartParam, "x_axis_max");
        xAxisTickAngle = getParamAsInt(chartParam, "x_axis_tick_angle");
        xAxisTickCount = getParamAsInt(chartParam, "x_axis_tick_count");
        xAxisTickFormat = getParamAsString(chartParam, "x_axis_tick_format");
        xAsixTickInterval = getParamAsString(chartParam, "x_asix_tick_interval");

        yAxisLabel = getParamAsString(chartParam, "y_axis_label");
        yAxisDataField = getParamAsString(chartParam, "y_axis_data_field");
        yAxisMin = getParamAsString(chartParam, "y_axis_min");
        yAxisMax = getParamAsString(chartParam, "y_axis_max");

        addDateAxis = "0".equals(getParamAsString(chartParam, "add_date_axis"));
        
        chartDataSql = getParamAsString(chartParam, "chart_data_sql");
    }
}
