package com.ge.apm.view.analysis;

import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.*;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Longs;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.NativeSqlUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableChartController;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ViewScoped
@ManagedBean
public class HomeAssetHeadController extends SqlConfigurableChartController {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeHeadController.class);
    private static final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    private static final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    private static final String remote_addr = request.getRemoteAddr();
    private static final String page_uri = request.getRequestURI();
    private static final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
    private static final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();
    HashMap<String, Object> sqlParams = new HashMap<>();  

    private static final String STEP_APPLY = "申请";
    private static final String STEP_APPROVE = "审核";
    private static final String STEP_DISPATCH = "派工";
    private static final String STEP_ENGAGE = "领工";
    private static final String STEP_REPAIR = "维修";
    private static final String STEP_CLOSE = "关单";
    private static final String ASSET_STATUS_IN_MT = "维修中";
    private static final String ASSET_STATUS_STOPPED = "停机中";
    private static final String ASSET_STATUS_WARRANTY = "保修期到期";
    private static final String ASSET_STATUS_PM = "预防性维护";
    private static final String ASSET_STATUS_METERING = "设备计量";
    private static final String ASSET_STATUS_QA = "设备质控";
    private final Map<String, String> queries;
    private BarChartModel barModel;
    private Long assetNumberInMt = 0L;
    private List<AssetViewInfo> assetsInMt = ImmutableList.of();
    private Map<String, Integer> stepCounts = ImmutableMap.of();
    private long assetNumberStopped = 0L;
    private List<AssetViewInfo> assetsStopped = ImmutableList.of();
    private long assetNumberWarrantyExpired = 0L;
    private List<AssetViewInfo> assetsWarrantyExpired = ImmutableList.of();
    private long assetNumberInPm = 0L;
    private List<AssetViewInfo> assetsInPm = ImmutableList.of();
    private long assetNumberInMetering = 0L;
    private List<AssetViewInfo> assetsInMetering = ImmutableList.of();
    private long assetNumberInQa = 0L;
    private List<AssetViewInfo> assetsInQa = ImmutableList.of();

    public HomeAssetHeadController() {
        queries = new HashMap<>();
        queries.put("assetsInMt", "select asset_name, current_step_id as step_id, current_step_name as step_name, case_owner_name as owner_name from work_order where is_closed=false and site_id=:#site_id and hospital_id=:#hospital_id");
        queries.put("assetsStopped", "select ai.name as asset_name, wo.create_time as down_time, wo.case_type as case_type from asset_info ai join (select tw.asset_id, tw.create_time, wkod.case_type  from (select asset_id, max(create_time) as create_time from work_order group by asset_id) tw join work_order wkod on tw.asset_id = wkod.asset_id where tw.create_time = wkod.create_time) as wo on ai.id = wo.asset_id where ai.is_valid = true and ai.status = 2 and ai.site_id = :#site_id and ai.hospital_id = :#hospital_id order by down_time,asset_name,case_type");
        queries.put("assetsWarrantyExpired", "select a.name as asset_name, a.warranty_date as warranty_date from asset_info a where a.is_valid = true and a.warranty_date <= (now() + interval '2 months')  and a.site_id = :#site_id and a.hospital_id = :#hospital_id order by a.warranty_date");
        queries.put("assetsInPm", "select a.asset_name,a.start_time as pm_date from pm_order a where a.site_id=:#site_id and a.hospital_id=:#hospital_id and a.is_finished=false and a.start_time<(now() + interval '1 weeks') order by a.start_time");
        queries.put("assetsInMetering", "select distinct d.asset_name, o.start_time from inspection_order o, inspection_order_detail d where o.id=d.order_id and o.site_id=:#site_id and o.hospital_id=:#hospital_id and o.start_time<=(now() + interval '2 months') and o.is_finished=false and o.order_type=2 order by o.start_time");
        queries.put("assetsInQa", "select distinct d.asset_name, o.start_time from inspection_order o, inspection_order_detail d where o.id=d.order_id and o.site_id=:#site_id and o.hospital_id=:#hospital_id and o.start_time<=(now() + interval '2 months') and o.is_finished=false and o.order_type=3 order by o.start_time");
    }

    @PostConstruct
    public void init() {
        initAssetsInfoInMt();
        initAssetsStopped();
        initAssetsWarrantyExpired();
        initAssetsInPm();
        initAssetsInMetering();
        initAssetsInQa();
        createBarModel();
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public Long getAssetNumberInMt() {
        return assetNumberInMt;
    }

    public List<AssetViewInfo> getAssetsInMt() {
        return assetsInMt;
    }

    public Map<String, Integer> getStepCounts() {
        return stepCounts;
    }

    public long getAssetNumberStopped() {
        return assetNumberStopped;
    }

    public List<AssetViewInfo> getAssetsStopped() {
        return assetsStopped;
    }

    public long getAssetNumberWarrantyExpired() {
        return assetNumberWarrantyExpired;
    }

    public List<AssetViewInfo> getAssetsWarrantyExpired() {
        return assetsWarrantyExpired;
    }

    public long getAssetNumberInPm() {
        return assetNumberInPm;
    }

    public List<AssetViewInfo> getAssetsInPm() {
        return assetsInPm;
    }

    public long getAssetNumberInMetering() {
        return assetNumberInMetering;
    }

    public List<AssetViewInfo> getAssetsInMetering() {
        return assetsInMetering;
    }

    public long getAssetNumberInQa() {
        return assetNumberInQa;
    }

    public List<AssetViewInfo> getAssetsInQa() {
        return assetsInQa;
    }

    private int countStep(FluentIterable<Map<String, Object>> assets, final String step) {
        return assets.filter(new Predicate<Map<String, Object>>() {
            @Override
            public boolean apply(Map<String, Object> input) {
                return Optional.fromNullable(input.get("step_name")).or("").toString().equalsIgnoreCase(step);
            }
        }).size();
    }

    private void initAssetsInfoInMt() {

        sqlParams.put("_sql", queries.get("assetsInMt"));
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsInMt"), sqlParams));

        assetNumberInMt = (long) assets.size();
        assetsInMt = assets.limit(2).transform(new Function<Map<String, Object>, AssetViewInfo>() {
            @Override
            public AssetViewInfo apply(Map<String, Object> stringObjectMap) {
                AssetViewInfo assetViewInfo = new AssetViewInfo();
                assetViewInfo.setAsset(Optional.fromNullable(stringObjectMap.get("asset_name")).or("").toString());
                assetViewInfo.setStepId(Optional.fromNullable(stringObjectMap.get("step_id")).or("").toString());
                assetViewInfo.setStepName(Optional.fromNullable(stringObjectMap.get("step_name")).or("").toString());
                assetViewInfo.setOwner(Optional.fromNullable(stringObjectMap.get("owner_name")).or("").toString());
                return assetViewInfo;
            }
        }).toList();

        stepCounts = new ImmutableMap.Builder<String, Integer>()
                .put(STEP_APPLY, countStep(assets, STEP_APPLY))
                .put(STEP_APPROVE, countStep(assets, STEP_APPROVE))
                .put(STEP_DISPATCH, countStep(assets, STEP_DISPATCH))
                .put(STEP_ENGAGE, countStep(assets, STEP_ENGAGE))
                .put(STEP_REPAIR, countStep(assets, STEP_REPAIR))
                .put(STEP_CLOSE, countStep(assets, STEP_CLOSE)).build();
    }

    private void initAssetsStopped() {

        sqlParams.put("_sql", queries.get("assetsStopped"));
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsStopped"), sqlParams));

        assetNumberStopped = (long) assets.size();
        assetsStopped = assets.limit(2).transform(new Function<Map<String, Object>, AssetViewInfo>() {
            @Override
            public AssetViewInfo apply(Map<String, Object> input) {
                AssetViewInfo assetViewInfo = new AssetViewInfo();
                assetViewInfo.setAsset(Optional.fromNullable(input.get("asset_name")).or("").toString());
                assetViewInfo.setDownTime(FluentIterable.from(Splitter.on(" ").split(Optional.fromNullable(input.get("down_time")).or("").toString())).first().or(""));
                assetViewInfo.setDownReason(WebUtil.getMessage(Optional.fromNullable(input.get("case_type")).or("").toString()));
                return assetViewInfo;
            }
        }).toList();
    }

    private void initAssetsWarrantyExpired() {

        sqlParams.put("_sql", queries.get("assetsWarrantyExpired"));
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsWarrantyExpired"), sqlParams));

        assetNumberWarrantyExpired = (long) assets.size();
        assetsWarrantyExpired = assets.limit(2).transform(new Function<Map<String, Object>, AssetViewInfo>() {
            @Override
            public AssetViewInfo apply(Map<String, Object> input) {
                AssetViewInfo assetViewInfo = new AssetViewInfo();
                assetViewInfo.setAsset(Optional.fromNullable(input.get("asset_name")).or("").toString());
                assetViewInfo.setWarrantyDate(FluentIterable.from(Splitter.on(" ").split(Optional.fromNullable(input.get("warranty_date")).or("").toString())).first().or(""));
                return assetViewInfo;
            }
        }).toList();
    }

    private void initAssetsInPm() {

        sqlParams.put("_sql", queries.get("assetsInPm"));
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsInPm"), sqlParams));

        assetNumberInPm = (long) assets.size();
        assetsInPm = assets.limit(2).transform(new Function<Map<String, Object>, AssetViewInfo>() {
            @Override
            public AssetViewInfo apply(Map<String, Object> input) {
                AssetViewInfo assetViewInfo = new AssetViewInfo();
                assetViewInfo.setAsset(Optional.fromNullable(input.get("asset_name")).or("").toString());
                assetViewInfo.setPmDate(FluentIterable.from(Splitter.on(" ").split(Optional.fromNullable(input.get("pm_date")).or("").toString())).first().or(""));
                return assetViewInfo;
            }
        }).toList();
    }

    private void initAssetsInMetering() {

        sqlParams.put("_sql", queries.get("assetsInMetering"));
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsInMetering"), sqlParams));

        assetNumberInMetering = (long) assets.size();
        assetsInMetering = assets.limit(2).transform(new Function<Map<String, Object>, AssetViewInfo>() {
            @Override
            public AssetViewInfo apply(Map<String, Object> input) {
                AssetViewInfo assetViewInfo = new AssetViewInfo();
                assetViewInfo.setAsset(Optional.fromNullable(input.get("asset_name")).or("").toString());
                assetViewInfo.setMeteringDate(FluentIterable.from(Splitter.on(" ").split(Optional.fromNullable(input.get("metering_date")).or("").toString())).first().or(""));
                return assetViewInfo;
            }
        }).toList();
    }

    private void initAssetsInQa() {

        sqlParams.put("_sql", queries.get("assetsInQa"));
        logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams); 
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsInQa"), sqlParams));

        assetNumberInQa = (long) assets.size();
        assetsInQa = assets.limit(2).transform(new Function<Map<String, Object>, AssetViewInfo>() {
            @Override
            public AssetViewInfo apply(Map<String, Object> input) {
                AssetViewInfo assetViewInfo = new AssetViewInfo();
                assetViewInfo.setAsset(Optional.fromNullable(input.get("asset_name")).or("").toString());
                assetViewInfo.setQaDate(FluentIterable.from(Splitter.on(" ").split(Optional.fromNullable(input.get("qa_date")).or("").toString())).first().or(""));
                return assetViewInfo;
            }
        }).toList();
    }

    private ChartSeries initChartSeries(ImmutableMap<String, Long> pairs) {
        ChartSeries chartSeries = new ChartSeries();
        for (Map.Entry<String, Long> entry : pairs.entrySet()) {
            chartSeries.set(entry.getKey(), entry.getValue());
        }
        return chartSeries;
    }


    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
        ImmutableMap<String, Long> placeholders = FluentIterable.of(ASSET_STATUS_IN_MT, ASSET_STATUS_STOPPED, ASSET_STATUS_WARRANTY, ASSET_STATUS_PM, ASSET_STATUS_METERING, ASSET_STATUS_QA).toMap(new Function<String, Long>() {
            @Override
            public Long apply(String input) {
                return 0L;
            }
        });
        ImmutableMap<String, Long> pairs = new ImmutableMap.Builder<String, Long>()
                .put(ASSET_STATUS_IN_MT, assetNumberInMt)
                .put(ASSET_STATUS_STOPPED, assetNumberStopped)
                .put(ASSET_STATUS_WARRANTY, assetNumberWarrantyExpired)
                .put(ASSET_STATUS_PM, assetNumberInPm)
                .put(ASSET_STATUS_METERING, assetNumberInMetering)
                .put(ASSET_STATUS_QA, assetNumberInQa)
                .build();
        model.addSeries(initChartSeries(placeholders));
        model.addSeries(initChartSeries(pairs));
        model.addSeries(initChartSeries(placeholders));
        return model;
    }

    private void createBarModel() {
        barModel = initBarModel();
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(Longs.max(assetNumberInMt, assetNumberStopped, assetNumberWarrantyExpired, assetNumberInPm, assetNumberInMetering, assetNumberInQa));
        barModel.setExtender("skinBar");
    }


    public static class AssetViewInfo {
        private String asset;
        private String stepId;
        private String stepName;
        private String owner;
        private String downTime;
        private String downReason;
        private String warrantyDate;
        private String pmDate;
        private String meteringDate;
        private String qaDate;

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).omitNullValues()
                    .add("asset", asset).add("stepId", stepId).add("stepName", stepName).add("owner", owner)
                    .add("downTime", downTime).add("downReason", downReason).add("warrantyDate", warrantyDate)
                    .add("pmDate", pmDate).add("meteringDate", meteringDate).add("qaDate", qaDate).toString();
        }

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public String getStepId() {
            return stepId;
        }

        public void setStepId(String stepId) {
            this.stepId = stepId;
        }

        public String getStepName() {
            return stepName;
        }

        public void setStepName(String stepName) {
            this.stepName = stepName;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getDownTime() {
            return downTime;
        }

        public void setDownTime(String downTime) {
            this.downTime = downTime;
        }

        public String getDownReason() {
            return downReason;
        }

        public void setDownReason(String downReason) {
            this.downReason = downReason;
        }

        public String getWarrantyDate() {
            return warrantyDate;
        }

        public void setWarrantyDate(String warrantyDate) {
            this.warrantyDate = warrantyDate;
        }

        public String getPmDate() {
            return pmDate;
        }

        public void setPmDate(String pmDate) {
            this.pmDate = pmDate;
        }

        public String getMeteringDate() {
            return meteringDate;
        }

        public void setMeteringDate(String meteringDate) {
            this.meteringDate = meteringDate;
        }

        public String getQaDate() {
            return qaDate;
        }

        public void setQaDate(String qaDate) {
            this.qaDate = qaDate;
        }
    }
}


