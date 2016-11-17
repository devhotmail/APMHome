package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import org.primefaces.model.chart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableChartController;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@ManagedBean
@ViewScoped
public class DeviceOperationMonitorController extends SqlConfigurableChartController {
    private static final Logger log = LoggerFactory.getLogger(DeviceOperationMonitorController.class);
    public final static String HEAD = "头部";
    public final static String CHEST = "胸部";
    public final static String ABDOMEN = "腹部";
    public final static String LIMBS = "四肢";
    public final static String OTHER = "其他";
    private final Map<String, String> queries;
    private final Map<String, Object> parameters;
    private final JdbcTemplate jdbcTemplate;
    private int hospitalId = 0;
    private BarChartModel headerBarModel;
    private LineChartModel tabAreaModel;
    private BarChartModel tabBarModel;
    private BarChartModel selectedBarModel;


    public DeviceOperationMonitorController() {
        queries = new HashMap<>();
        parameters = new HashMap<>();
        jdbcTemplate = WebUtil.getServiceBean("jdbcTemplate", JdbcTemplate.class);
    }

    @PostConstruct
    public void init() {
        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        parameters.put("hospitalId", hospitalId);
        tabAreaModel = initLineCharModel(null, true, "ne", initTable());
        headerBarModel = initBarModel(new HorizontalBarChartModel(), null, true, 0, "ne", null, ImmutableMap.of(HEAD, 800, CHEST, 900, ABDOMEN, 2700, LIMBS, 900, OTHER, 900));
        tabBarModel = initBarModel(new BarChartModel(), null, true, 50, "ne", null, ImmutableMap.of(HEAD, 800, CHEST, 900, ABDOMEN, 2700, LIMBS, 900, OTHER, 900));
        selectedBarModel = initBarModel(new BarChartModel(), null, true, 50, "ne", null, ImmutableMap.of(HEAD, 800, CHEST, 900, ABDOMEN, 2700, LIMBS, 900, OTHER, 900));
    }

    public int getHospitalId() {
        return hospitalId;
    }


    public BarChartModel getHeaderBarModel() {
        return headerBarModel;
    }

    public LineChartModel getTabAreaModel() {
        return tabAreaModel;
    }

    public BarChartModel getTabBarModel() {
        return tabBarModel;
    }

    public BarChartModel getSelectedBarModel() {
        return selectedBarModel;
    }

    private <T extends ChartSeries> T initChartSeries(T chartSeries, String label, boolean fill, ImmutableMap<String, Integer> pairs) {
        for (Map.Entry<String, Integer> entry : pairs.entrySet()) {
            chartSeries.set(entry.getKey(), entry.getValue());
        }
        if (chartSeries instanceof LineChartSeries) {
            ((LineChartSeries) chartSeries).setFill(fill);
        }
        chartSeries.setLabel(label);
        return chartSeries;
    }

    private LineChartModel initLineCharModel(String title, boolean stacked, String legendPosition, ImmutableTable<String, String, Integer> table) {
        LineChartModel areaModel = new LineChartModel();
        areaModel.setTitle(title);
        areaModel.setStacked(stacked);
        areaModel.setLegendPosition(legendPosition);
        areaModel.addSeries(initChartSeries(new LineChartSeries(), HEAD, true, table.row(HEAD)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), CHEST, true, table.row(CHEST)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), CHEST, true, table.row(ABDOMEN)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), LIMBS, true, table.row(LIMBS)));
        areaModel.addSeries(initChartSeries(new LineChartSeries(), OTHER, true, table.row(OTHER)));
        Axis xAxis = areaModel.getAxis(AxisType.X);
        Axis yAxis = areaModel.getAxis(AxisType.Y);
        xAxis.setLabel("时间");
        yAxis.setLabel("次数");

        return areaModel;
    }

    private ImmutableTable<String, String, Integer> initTable() {
        ImmutableTable.Builder<String, String, Integer> table = new ImmutableTable.Builder<>();
        Random random = new Random();
        for (String part : ImmutableList.of(HEAD, CHEST, ABDOMEN, LIMBS, OTHER)) {
            for (int i = 1; i <= 12; i++)
                table.put(part, String.valueOf(i), random.nextInt(1000) + 500);
        }
        return table.build();
    }


    private <T extends BarChartModel> T initBarModel(T barChartModel, String title, boolean stacked, int barWidth, String legendPostion, String skin, Map<String, Integer> renderData) {
        barChartModel.setTitle(title);
        barChartModel.setStacked(stacked);
        barChartModel.setBarWidth(barWidth);
        barChartModel.setLegendPosition(legendPostion);
        barChartModel.setExtender(skin);
        barChartModel.setShowDatatip(false);
        barChartModel.setShadow(false);
        barChartModel.addSeries(initChartSeries(new ChartSeries(), HEAD, false, ImmutableMap.of(HEAD, renderData.get(HEAD))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), CHEST, false, ImmutableMap.of(CHEST, renderData.get(CHEST))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), ABDOMEN, false, ImmutableMap.of(ABDOMEN, renderData.get(ABDOMEN))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), LIMBS, false, ImmutableMap.of(LIMBS, renderData.get(LIMBS))));
        barChartModel.addSeries(initChartSeries(new ChartSeries(), OTHER, false, ImmutableMap.of(OTHER, renderData.get(OTHER))));
        Axis xAxis = barChartModel.getAxis(AxisType.X);
        Axis yAxis = barChartModel.getAxis(AxisType.Y);
        xAxis.setLabel("时间");
        yAxis.setLabel("次数");

        return barChartModel;
    }


}
