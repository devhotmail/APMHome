package com.ge.apm.view;

import com.ge.apm.domain.ChartConfig;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.chart.PieChartModel;
import webapp.framework.web.mvc.SqlConfigurableChartController;

@ManagedBean
@ViewScoped
public class DemoChartController extends SqlConfigurableChartController {

    @Override
    public String onBeforeGetChartData(String chartKey, String sql, Map<String, Object> sqlParam) {
        if ("dashboard_pie".equals(chartKey)) {
            sqlParam.put("id", 0);
        }
        
        return sql;
    }
    
    @Override
    public void onBeforeBuildChart(String chartName, List<Map<String, Object>> chartData, ChartConfig chartConfig){
    }
    
    @Override
    public void onAfterBuildChart(String chartName, Object chartModel, ChartConfig chartConfig){
        if(chartModel instanceof PieChartModel)
            ((PieChartModel) chartModel).setTitle("Pie Chart Demo");
    }
}
