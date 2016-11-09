package com.ge.apm.view.analysis;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import webapp.framework.web.mvc.SqlConfigurableChartController;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class HomeAssetHeadController extends SqlConfigurableChartController {
    private BarChartModel barModel;

    public BarChartModel getBarModel() {
        return barModel;
    }

    @PostConstruct
    public void init() {
        createBarModel();
    }

    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();

        ChartSeries placeholders0 = new ChartSeries();
        placeholders0.setLabel("placeholders");
        placeholders0.set("维修中", 0);
        placeholders0.set("停机中", 0);
        placeholders0.set("保修期到期", 0);
        placeholders0.set("预防性维护", 0);
        placeholders0.set("设备计量", 0);
        placeholders0.set("设备质控", 0);

        ChartSeries placeholders1 = new ChartSeries();
        placeholders1.setLabel("placeholders");
        placeholders1.set("维修中", 0);
        placeholders1.set("停机中", 0);
        placeholders1.set("保修期到期", 0);
        placeholders1.set("预防性维护", 0);
        placeholders1.set("设备计量", 0);
        placeholders1.set("设备质控", 0);

        ChartSeries overviews = new ChartSeries();
        overviews.setLabel("overviews");
        overviews.set("维修中", 120);
        overviews.set("停机中", 100);
        overviews.set("保修期到期", 44);
        overviews.set("预防性维护", 150);
        overviews.set("设备计量", 25);
        overviews.set("设备质控", 25);

        model.addSeries(placeholders0);
        model.addSeries(overviews);
        model.addSeries(placeholders1);

        return model;
    }

    private void createBarModel() {
        barModel = initBarModel();
        Axis xAxis = barModel.getAxis(AxisType.X);
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(200);
        barModel.setExtender("skinBar");
    }

}
