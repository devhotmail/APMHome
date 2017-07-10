/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.data;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.MM3DataRecordRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.MM3DataRecord;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

/**
 *
 * @author 212579464
 */
@Component
public class MM3FileImportService {

    private static Pattern PATTERN_RuO = Pattern.compile("([0-9. ]+)K");
    private static Pattern PATTERN_Pressure = Pattern.compile("([0-9. ]+)PSI[.]*");
    private static Pattern PATTERN_Percentage = Pattern.compile("([0-9. ]+)%");

    public static enum ImportStatus {
        New, Exist, Created, Failure, NoAsset
    }

    private Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
    private Integer hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
    private List<I18nMessage> assetFunctionTypes = FieldValueMessageController.getFieldValueList("assetFunctionType", siteId);
    private List<I18nMessage> assetGroup = FieldValueMessageController.getFieldValueList("assetGroup", siteId);
    private List<I18nMessage> depreciationMethod = FieldValueMessageController.getFieldValueList("depreciationMethodList", siteId);

    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private MM3DataRecordRepository mm3Dao;

    public Map<String, Map<String, Object>> getMM3DataMapFromTemplate(ExcelDocument doc) {
        Map<String, Map<String, Object>> result = new HashMap();
        int keyRow = 1;
        int dataRow = 2;

        for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {

            String systemId = ExcelDocument.getCellStringValue(rowMap.get("SYSTEM_ID"));
            if (null == systemId || systemId.isEmpty()) {
                break;
            }
            MM3DataRecord record = new MM3DataRecord();
            record.setSystemId(systemId);
            record.setChineseName(ExcelDocument.getCellStringValue(rowMap.get("Chinese Name")));
            record.setProduct(ExcelDocument.getCellStringValue(rowMap.get("Product")));
            record.setColdheadRuO(getDoubleValue(ExcelDocument.getCellStringValue(rowMap.get("Coldhead RuO")), PATTERN_RuO));
            record.setHePressure(getDoubleValue(ExcelDocument.getCellStringValue(rowMap.get("HePressure")), PATTERN_Pressure));
            record.setReportTime((Date) rowMap.get("HePressure - Read Time"));
            record.setHeLevel(ExcelDocument.getCellDoubleValue(rowMap.get("HeLevel")));
            record.setShieldTemp(getDoubleValue(ExcelDocument.getCellStringValue(rowMap.get("Shield Temp")), PATTERN_RuO));
            record.setReconRuO(getDoubleValue(ExcelDocument.getCellStringValue(rowMap.get("Recon RuO")), PATTERN_RuO));
            record.setHeaterDutyCycle(ExcelDocument.getCellDoubleValue(rowMap.get("Heater Duty Cycle")));
            record.setHeaterOffPressure(getDoubleValue(ExcelDocument.getCellStringValue(rowMap.get("Heater Off Pressure")), PATTERN_Pressure));
            record.setHeaterOnPressure(getDoubleValue(ExcelDocument.getCellStringValue(rowMap.get("Heater On Pressure")), PATTERN_Pressure));
            record.setStatus(MM3DataRecord.Status.New.toString());

            Map<String, Object> map = new HashMap();
            map.put("record", record);
            result.put(systemId, map);
        }
        return result;
    }


    private void addErrMessage(Map<String, Object> item, String comments) {
        if (item.containsKey("errmsg")) {
            ((List<String>) item.get("errmsg")).add(comments);
        } else {
            List<String> c = new ArrayList();
            c.add(comments);
            item.put("errmsg", c);
        }
    }

    public void importData(Map<String, Map<String, Object>> importMM3DataMap) {
        importMM3DataMap.values().forEach(item -> {
            MM3DataRecord recordItem = (MM3DataRecord) item.get("record");
            if(item.get("status").equals(MM3FileImportService.ImportStatus.New)){
                 mm3Dao.save(recordItem);
                item.put("status", MM3FileImportService.ImportStatus.Created);
            }
        });

    }

    private List<AssetInfo> getAssetInfos(String systemId) {
        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("systemId", SearchFilter.Operator.EQ, systemId));
        return assetDao.findBySearchFilter(assetFilters);
    }

    private List<MM3DataRecord> getMM3RecordsList(MM3DataRecord record) {
        List<SearchFilter> mm3Filters = new ArrayList<>();
        mm3Filters.add(new SearchFilter("systemId", SearchFilter.Operator.EQ, record.getSystemId()));
        mm3Filters.add(new SearchFilter("reportTime", SearchFilter.Operator.EQ, record.getReportTime()));
        return mm3Dao.findBySearchFilter(mm3Filters);
    }

    public void checkData(Map<String, Map<String, Object>> importMM3DataMap) {

        importMM3DataMap.values().forEach(item -> {

            MM3DataRecord recordItem = (MM3DataRecord) item.get("record");
            List<MM3DataRecord> records = getMM3RecordsList(recordItem);
            if (null == records || records.isEmpty()) {
                item.put("status", MM3FileImportService.ImportStatus.New);
            } else {
                item.put("status", MM3FileImportService.ImportStatus.Exist);
            }
            
            List<AssetInfo> assetList = getAssetInfos(recordItem.getSystemId());
            if(assetList == null || assetList.isEmpty()){
                item.put("status", MM3FileImportService.ImportStatus.NoAsset);
            }else if(assetList.size()==1){
                AssetInfo asset = (AssetInfo)assetList.get(0);
                recordItem.setAssetId(asset.getId());
                recordItem.setAssetUid(asset.getUid());
                item.put("asset", asset);
            }else if(assetList.size()>1){
                addErrMessage(item, "Error: 匹配到多个设备");
            }

        });
    }

    private Double getDoubleValue(String value, Pattern pt) {
        Matcher matcher = pt.matcher(value);
        if (matcher.lookingAt()) {
            return Double.parseDouble(matcher.group(1));
        } else {
            return null;
        }
    }
    
}
