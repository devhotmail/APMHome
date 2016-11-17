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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ViewScoped
@ManagedBean
public class HomeAssetHeadController extends SqlConfigurableChartController {
    private static final Logger log = LoggerFactory.getLogger(HomeHeadController.class);
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
    private final Map<String, Object> parameters;
    private BarChartModel barModel;
    private int hospitalId = 0;
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
        parameters = new HashMap<>();
        queries.put("assetsInMt", "select w.asset_name, ws.step_id, ws.step_name, ws.owner_name from work_order w, work_order_step as ws where w.id = ws.work_order_id and ws.id in (select w.current_step from asset_info a, work_order w, work_order_step s where a.is_valid = true and w.is_closed = false and a.id = w.asset_id and a.hospital_id = :#hospitalId)");
        queries.put("assetsStopped", "select a.name as asset_name, w.create_time as down_time, i.msg_key as down_reason from asset_info a right join work_order w on a.id = w.asset_id right join i18n_message i on w.case_type = i.id where a.is_valid = true and a.status =2 and a.hospital_id = :#hospitalId order by w.create_time");
        queries.put("assetsWarrantyExpired", "select a.name as asset_name, a.warranty_date as warranty_date from asset_info a where a.is_valid = true and a.warranty_date <= (now() + interval '2 months')  and a.hospital_id = :#hospitalId order by a.warranty_date");
        queries.put("assetsInPm", "select a.name as asset_name, a.last_pm_date as pm_date from asset_info a where a.hospital_id = :#hospitalId and a.last_pm_date < (now() + interval '1 weeks') order by last_pm_date");
        queries.put("assetsInMetering", "select a.name as asset_name, a.last_metering_date as metering_date from asset_info a where a.hospital_id = 1 and a.last_pm_date < (now() + interval '2 months') order by a.last_metering_date");
        queries.put("assetsInQa", "select a.name as asset_name, a.last_qa_date as qa_date from asset_info a where a.hospital_id = 1 and a.last_qa_date < (now() + interval '2 months') order by a.last_qa_date");
    }

    @PostConstruct
    public void init() {
        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        parameters.put("hospitalId", hospitalId);
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

    public int getHospitalId() {
        return hospitalId;
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
        log.info("entering initAssetsInfoInMt");

        log.info("fetching assetsInMt, sql= {}", queries.get("assetsInMt"));
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsInMt"), parameters));
        log.info("assets = {}", assets);

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
        log.info("assetsInMt = {}", assetsInMt);

        stepCounts = new ImmutableMap.Builder<String, Integer>()
                .put(STEP_APPLY, countStep(assets, STEP_APPLY))
                .put(STEP_APPROVE, countStep(assets, STEP_APPROVE))
                .put(STEP_DISPATCH, countStep(assets, STEP_DISPATCH))
                .put(STEP_ENGAGE, countStep(assets, STEP_ENGAGE))
                .put(STEP_REPAIR, countStep(assets, STEP_REPAIR))
                .put(STEP_CLOSE, countStep(assets, STEP_CLOSE)).build();
        log.info("stepCounts = {}", stepCounts);
    }

    private void initAssetsStopped() {
        log.info("entering initAssetsStopped");

        log.info("fetching assetsStopped, sql= {}", queries.get("assetsStopped"));
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsStopped"), parameters));
        log.info("assets = {}", assets);

        assetNumberStopped = (long) assets.size();
        assetsStopped = assets.limit(2).transform(new Function<Map<String, Object>, AssetViewInfo>() {
            @Override
            public AssetViewInfo apply(Map<String, Object> input) {
                AssetViewInfo assetViewInfo = new AssetViewInfo();
                assetViewInfo.setAsset(Optional.fromNullable(input.get("asset_name")).or("").toString());
                assetViewInfo.setDownTime(FluentIterable.from(Splitter.on(" ").split(Optional.fromNullable(input.get("down_time")).or("").toString())).first().or(""));
                assetViewInfo.setDownReason(WebUtil.getMessage(Optional.fromNullable(input.get("down_reason")).or("").toString()));
                return assetViewInfo;
            }
        }).toList();
        log.info("assetsStopped = {}", assetsStopped);
    }

    private void initAssetsWarrantyExpired() {
        log.info("entering initAssetsWarrantyExpired");

        log.info("fetching assetsWarrantyExpired, sql= {}", queries.get("assetsWarrantyExpired"));
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsWarrantyExpired"), parameters));
        log.info("assets = {}", assets);

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
        log.info("assetsWarrantyExpired = {}", assetsWarrantyExpired);
    }

    private void initAssetsInPm() {
        log.info("entering initAssetsInPm");

        log.info("fetching assetsInPm, sql= {}", queries.get("assetsInPm"));
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsInPm"), parameters));
        log.info("assets = {}", assets);

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
        log.info("assetsInPm = {}", assetsInPm);
    }

    private void initAssetsInMetering() {
        log.info("entering initAssetsInMetering");

        log.info("fetching assetsInMetering, sql= {}", queries.get("assetsInMetering"));
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsInMetering"), parameters));
        log.info("assets = {}", assets);

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
        log.info("assetsInMetering = {}", assetsInMetering);
    }

    private void initAssetsInQa() {
        log.info("entering initAssetsInQa");

        log.info("fetching assetsInQa, sql= {}", queries.get("assetsInQa"));
        FluentIterable<Map<String, Object>> assets = FluentIterable.from(NativeSqlUtil.queryForList(queries.get("assetsInQa"), parameters));
        log.info("assets = {}", assets);

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
        log.info("assetsInQa = {}", assetsInQa);
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


