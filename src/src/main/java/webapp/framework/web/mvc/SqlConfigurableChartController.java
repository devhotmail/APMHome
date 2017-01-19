package webapp.framework.web.mvc;

import com.ge.apm.dao.ChartConfigRepository;
import com.ge.apm.domain.ChartConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.PieChartModel;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

public class SqlConfigurableChartController implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final Logger logger = Logger.getLogger(SqlConfigurableChartController.class);

    protected List<SearchFilter> searchFilterList;

    public void setSearchFilter() {
        charts.clear();
        searchFilterList = SearchFilter.getSearchFiltersFromHttpRequest(SearchFilter.searchFilterPrefix);
    }
    
    public Map<String, Object> charts = new HashMap<>();

    public static Map<String, ChartConfig> chartConfigData = new HashMap<>();
    
    public static void reLoadChartConfig(){
        ChartConfigRepository daoChartConfig = WebUtil.getBean(ChartConfigRepository.class);
        List<ChartConfig> chartConfigList = daoChartConfig.loadChartConfigData();

        chartConfigData.clear();
        for(ChartConfig item: chartConfigList){
            chartConfigData.put(item.getChartName(), item);
        }
    }
    
    public String onBeforeGetChartData(String chartName, String sql, Map<String, Object> sqlParam){
        return sql;
    }
    
    public void onBeforeBuildChart(String chartName, List<Map<String, Object>> chartData, ChartConfig chartConfig){
    }
    public void onAfterBuildChart(String chartName, Object chartModel, ChartConfig chartConfig){
    }

    public ChartConfig getChartConfig(String chartName){
        if(chartConfigData.size()<1) reLoadChartConfig();
        
        return chartConfigData.get(chartName);
    }

    public String getChartType(String chartName){
        return getChartConfig(chartName).getChartType();
    }
    
    public String getSearchFilterSQL(boolean isDefaultEmptyResult, List<SearchFilter> searchFilterList){
        String searchFilterSQL = SearchFilter.buildSearchFilterSql(searchFilterList);
        if( isDefaultEmptyResult && "".equals(searchFilterSQL))
            return " and false";
        else
            return searchFilterSQL;
    }
    
    public Object buildChartModel(String chartName){
        ChartConfig chartConfig = getChartConfig(chartName);
        String sql = chartConfig.getChartDataSql();

        if(searchFilterList==null) searchFilterList = new ArrayList<SearchFilter>();
        
        String searchFilterSQL = getSearchFilterSQL(chartConfig.getIsDefaultEmptyResult(), searchFilterList);
        Map<String, Object> sqlParam = SearchFilter.buildSearchFilterSqlParam(searchFilterList);
        sql = sql.replace(":#searchFilter", searchFilterSQL);
                
        sql = onBeforeGetChartData(chartName, sql, sqlParam);
        List<Map<String, Object>> chartData = NativeSqlUtil.queryForList(sql, sqlParam);
        
        onBeforeBuildChart(chartName, chartData, chartConfig);

        Object chartModel;
        switch(chartConfig.getChartType()){
            case ChartConfig.CHART_BAR:
                chartModel = new BarChartModel();
                buildCartesianChart(chartConfig.getChartType(), (CartesianChartModel)chartModel, chartData, chartConfig);
                break;
            case ChartConfig.CHART_LINE:
                chartModel = new LineChartModel();
                buildCartesianChart(chartConfig.getChartType(), (CartesianChartModel)chartModel, chartData, chartConfig);
                break;
            default:
                chartModel = buildPieChart(chartData, chartConfig);
                break;
        }
        
        onAfterBuildChart(chartName, chartModel, chartConfig);
        return chartModel;
    }
    
    public Object getChartModel(String chartName){
        Object chartModel = charts.get(chartName);
        if(chartModel==null){
            chartModel = buildChartModel(chartName);

            charts.put(chartName, chartModel);
        }
        return chartModel;
    }

    public static Object buildCartesianChart(String chartType, CartesianChartModel chartModel, List<Map<String, Object>> chartData, ChartConfig chartConfig){
        // for line, bar chart
        chartModel.setTitle(chartConfig.getChartTitle());
        chartModel.setZoom(chartConfig.getZoomEnabled());

        chartModel.setLegendPosition(chartConfig.getLegendPosition());
        if(chartConfig.getLegendRows()!=null)
                chartModel.setLegendRows(chartConfig.getLegendRows());
        if(chartConfig.getLegendCols()!=null)
                chartModel.setLegendCols(chartConfig.getLegendCols());

        if(chartConfig.getxAxisLabel()!=null){
            Axis xAxis = chartModel.getAxis(AxisType.X);
            xAxis.setLabel(chartConfig.getxAxisLabel());
        }

        if(chartConfig.getyAxisLabel()!=null){
            Axis yAxis = chartModel.getAxis(AxisType.Y);
            yAxis.setLabel(chartConfig.getyAxisLabel());
        }

        if(chartConfig.getAddDateAxis()){
            DateAxis axis = new DateAxis(chartConfig.getxAxisLabel());
            axis.setTickAngle(-50);
            axis.setTickFormat("%Y-%m-%d");
            chartModel.getAxes().put(AxisType.X, axis);
        }
        
        Map<String, ChartSeries> mapChartSeries = new HashMap<>();
        for(Map<String, Object> row: chartData){
            try{
                String seriesName = row.get(chartConfig.getSeriesDataField()).toString();
                Object seriesValueX = row.get(chartConfig.getxAxisDataField());
                Object seriesValueY = row.get(chartConfig.getyAxisDataField());
                
                ChartSeries chartSeries = mapChartSeries.get(seriesName);
                if(chartSeries==null){
                    if(ChartConfig.CHART_LINE.equals(chartType)){
                        chartSeries = new LineChartSeries();
                    }
                    else
                        chartSeries = new ChartSeries();

                    chartSeries.setLabel(seriesName);
                    chartModel.addSeries(chartSeries);
                    mapChartSeries.put(seriesName, chartSeries);
                }
                chartSeries.set(seriesValueX, Double.parseDouble(seriesValueY.toString()));
            }
            catch(Exception ex){
                logger.warn("Error adding series data: buildLineChart", ex);
            }
        }
        
        // in case no data for chart, then create an empty chart
        if(chartModel.getSeries().size()<1){
            ChartSeries chartSeries = new ChartSeries();
            chartSeries.set(0, 0);
            chartModel.addSeries(chartSeries);
        }
        
        return chartModel;
    }

    public static Object buildPieChart(List<Map<String, Object>> chartData, ChartConfig chartConfig){
        PieChartModel model = new PieChartModel();
        model.setTitle(chartConfig.getChartTitle());
        model.setLegendPosition(chartConfig.getLegendPosition());
        model.setLegendCols(0);

        if(chartConfig.getLegendRows()!=null)
                model.setLegendRows(chartConfig.getLegendRows());
        if(chartConfig.getLegendCols()!=null)
                model.setLegendCols(chartConfig.getLegendCols());

        for(Map<String, Object> row: chartData){
            try{
                Object piePartName = row.get(chartConfig.getSeriesDataField());
                Object piePartValue = row.get(chartConfig.getxAxisDataField());

                model.set(piePartName.toString(), Double.parseDouble(piePartValue.toString()));
            }
            catch(Exception ex){
                logger.warn("Error adding series data in buildLineChart", ex);
            }
        }
        
        return model;
    }
}
