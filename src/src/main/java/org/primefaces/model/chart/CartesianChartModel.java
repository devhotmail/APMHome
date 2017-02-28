package org.primefaces.model.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartesianChartModel extends ChartModel {

    private List<ChartSeries> series;
    protected Map<AxisType,Axis> axes;
    private boolean zoom = false;
    private boolean animate = false;
    private boolean showDatatip = true;
    private String datatipFormat;
    private boolean showPointLabels = false;
    
    public CartesianChartModel() {
        series = new ArrayList<ChartSeries>();
        datatipFormat = "%d";
        createAxes();
    }
    
    protected void createAxes() {
        axes = new HashMap<AxisType, Axis>();
        axes.put(AxisType.X, new LinearAxis());
        axes.put(AxisType.Y, new LinearAxis());
    }
    
    public List<ChartSeries> getSeries() {
        return series;
    }

    public void addSeries(ChartSeries chartSeries) {
        this.series.add(chartSeries);
    }

    public void clear() {
        this.series.clear();
    }

    public Map<AxisType, Axis> getAxes() {
        return axes;
    }
    
    public Axis getAxis(AxisType type) {
        return axes.get(type);
    } 

    public boolean isZoom() {
        return zoom;
    }
    public void setZoom(boolean zoom) {
        this.zoom = zoom;
    }

    public boolean isAnimate() {
        return animate;
    }
    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public boolean isShowDatatip() {
        return showDatatip;
    }
    public void setShowDatatip(boolean showDatatip) {
        this.showDatatip = showDatatip;
    }

    public String getDatatipFormat() {
        return datatipFormat;
    }
    public void setDatatipFormat(String datatipFormat) {
        this.datatipFormat = datatipFormat;
    }

    public boolean isShowPointLabels() {
        return showPointLabels;
    }
    public void setShowPointLabels(boolean showPointLabels) {
        this.showPointLabels = showPointLabels;
    }
}