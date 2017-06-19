/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class AssetFileImportService {

    private static final String SHEET_ASSET = "Asset List";
    private static final String SHEET_ORG = "Org List";
    private static final String SHEET_USER = "User List";

    public static enum ImportStatus {
        New, Exist, Created, Failure
    }

    private Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
    private Integer hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
    private List<I18nMessage> assetFunctionTypes = FieldValueMessageController.getFieldValueList("assetFunctionType", siteId);
    private List<I18nMessage> assetGroup = FieldValueMessageController.getFieldValueList("assetGroup", siteId);
    private List<I18nMessage> depreciationMethod = FieldValueMessageController.getFieldValueList("depreciationMethodList", siteId);

    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private OrgInfoRepository orgDao;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    private SupplierRepository supplierDao;
    @Autowired
    private QrCodeLibRepository qrcodeDao;
    @Autowired
    private AssetDepreciationService assetDepreciationService;

    public Map<String, Map<String, Object>> getOrgInfoMapFromTemplate(ExcelDocument doc) {
        Map<String, Map<String, Object>> result = new HashMap();
        int keyRow = 4;
        int dataRow = 5;
        OrgInfo parentOrg = orgDao.findById(hospitalId);

        for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_ORG, keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(SHEET_ORG, keyRow, ++dataRow)) {
            OrgInfo org = new OrgInfo();
            String orgName = (String) rowMap.get("部门名称");
            if (orgName == null || orgName.isEmpty()) {
                break;
            }
            org.setName(orgName);
            org.setHospitalId(hospitalId);
            org.setParentOrg(parentOrg);
            org.setSiteId(siteId);

            Map<String, Object> map = new HashMap();
            map.put("org", org);
            map.put("status", ImportStatus.New);
            map.put("rowData", rowMap);
            result.put(orgName, map);
        }
        return result;
    }

    /*
    public Map<String, Map<String, Object>> getUserMapFromTemplate(ExcelDocument doc, Map<String, Map<String, Object>> orgMap) {
        Map<String, Map<String, Object>> result = new HashMap();
        int keyRow = 4;
        int dataRow = 5;

        for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_USER, keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(SHEET_USER, keyRow, ++dataRow)) {
            UserAccount user = new UserAccount();
            String userName = (String) rowMap.get("人员名字");
            String loginName = (String) rowMap.get("用户名");
            if (userName == null || userName.isEmpty() || loginName == null || loginName.isEmpty()) {
                break;
            }
            String orgName = (String) rowMap.get("所属科室");
            user.setHospitalId(hospitalId);
            user.setSiteId(siteId);
            user.setTelephone(String.valueOf(rowMap.get("电话")));
            user.setName(userName);
            user.setLoginName(loginName);
            OrgInfo org = (OrgInfo) orgMap.get(orgName).get("org");

            Map<String, Object> map = new HashMap();
            map.put("user", user);
            map.put("org", org);
            map.put("status", ImportStatus.New);
            result.put(userName, map);
        }
        return result;
    }
     */
    public Map<String, Map<String, Object>> getAssetMapFromTemplate(ExcelDocument doc) {

        Map<String, Map<String, Object>> result = new HashMap();
        int keyRow = 6;
        int dataRow = 7;

        for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_ASSET, keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(SHEET_ASSET, keyRow, ++dataRow)) {
            AssetInfo asset = new AssetInfo();
            String assetName = (String) rowMap.get("设备名称");
            if (assetName == null || assetName.isEmpty()) {
                break;
            }
            String modalityId = (String) rowMap.get("设备编号");
            asset.setDepartNum(modalityId);
            asset.setName(assetName);
            asset.setAliasName((String) rowMap.get("设备别名"));
            asset.setSiteId(siteId);
            asset.setHospitalId(hospitalId);
            asset.setIsValid(true);
            asset.setStatus(1);
            asset.setAssetOwnerName((String) rowMap.get("负责人"));

            asset.setClinicalDeptName((String) rowMap.get("使用部门"));
            asset.setManufacture((String) rowMap.get("制造商"));
            asset.setVendor((String) rowMap.get("供应商"));
            String functionGroup = (String) rowMap.get("功能分组");
            asset.setFunctionGroup(getI18nMsgKey(assetFunctionTypes, functionGroup));
            String assetGroupName = (String) rowMap.get("设备类型");
            asset.setAssetGroup(getI18nMsgKey(assetGroup, assetGroupName));
            asset.setFunctionGrade(ExcelDocument.getCellIntegerValue(rowMap.get("功能等级")));
            asset.setSerialNum((String) rowMap.get("制造商序列号"));
            asset.setSystemId((String) rowMap.get("SystemID/厂商报修ID"));
            asset.setBarcode((String) rowMap.get("条码"));
            asset.setFinancingNum((String) rowMap.get("资产编号"));
            asset.setModalityId((String) rowMap.get("IT系统编号"));
            asset.setPurchasePrice(ExcelDocument.getCellDoubleValue(rowMap.get("采购价格")));
            asset.setSalvageValue(ExcelDocument.getCellDoubleValue(rowMap.get("最终残值")));
            asset.setLifecycleInYear(ExcelDocument.getCellDoubleValue(rowMap.get("使用年限")));
            String depreciationMethodNmae = (String) rowMap.get("折旧算法");
            asset.setDepreciationMethod(getI18nMsgKey(depreciationMethod, depreciationMethodNmae));
            asset.setManufactDate((Date) rowMap.get("生产日期"));
            asset.setPurchaseDate((Date) rowMap.get("采购日期"));
            asset.setArriveDate((Date) rowMap.get("到货日期"));
            asset.setInstallDate((Date) rowMap.get("安装日期"));
            asset.setWarrantyDate((Date) rowMap.get("维保截止日期"));
            asset.setTerminateDate((Date) rowMap.get("报废日期"));
            asset.setLastPmDate((Date) rowMap.get("上次维护日期"));
            asset.setLastMeteringDate((Date) rowMap.get("上次计量日期"));
            asset.setLastQaDate((Date) rowMap.get("上次质检日期"));
            asset.setMaitanance((String) rowMap.get("维护商"));
            Double maitananceTel = ExcelDocument.getCellDoubleValue(rowMap.get("维护商电话"));
            asset.setMaitananceTel(maitananceTel==null? "" : String.valueOf(maitananceTel.longValue()));
            asset.setQrCode((String) rowMap.get("二维码"));

            Map<String, Object> map = new HashMap();
            map.put("asset", asset);
            map.put("status", ImportStatus.New);
            map.put("rowData", rowMap);

            result.put(modalityId.concat(assetName), map);
        }

        return result;
    }

    private Integer getI18nMsgKey(List<I18nMessage> msgList, String value) {
        I18nMessage msg = msgList.stream().filter(t -> t.getValueZh().equals(value)).findAny().orElse(null);
        return msg == null ? null : Integer.parseInt(msg.getMsgKey());
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

    public void importData(Map<String, Map<String, Object>> importOrgMap, Map<String, Map<String, Object>> importAssetMap) {
        importOrgMap.values().forEach(item -> {
            List<OrgInfo> orgs = getOrgInfoByName((OrgInfo) item.get("org"));
            if (null == orgs || orgs.isEmpty()) {
                orgDao.save((OrgInfo) item.get("org"));
                item.put("status", ImportStatus.Created);
            } else if (orgs.size() > 1) {
                item.put("status", ImportStatus.Failure);
                addErrMessage(item, WebUtil.getMessage("InvalidParameter") + ((OrgInfo) item.get("org")).getName());
            } else {
                item.put("org", orgs.get(0));
                item.put("status", ImportStatus.Exist);
            }
        });

        importAssetMap.values().forEach(item -> {
            AssetInfo newAsset = (AssetInfo) item.get("asset");

            String orgName = newAsset.getClinicalDeptName();
            OrgInfo clinicalDept = (OrgInfo) importOrgMap.get(orgName).get("org");
            if (clinicalDept.getId() == null || clinicalDept.getId() <= 0) {
                item.put("status", ImportStatus.Failure);
                addErrMessage(item, WebUtil.getMessage("InvalidParameter") + orgName);
                newAsset.setClinicalDeptName(null);
            }

            List<UserAccount> userList = getUserInfosByName(newAsset.getAssetOwnerName());
            if (userList != null && userList.size() == 1) {
                UserAccount assetOwner = userList.get(0);
                newAsset.setAssetOwnerId(assetOwner.getId());
                newAsset.setAssetOwnerTel(assetOwner.getTelephone());
            } else {
                addErrMessage(item, WebUtil.getMessage("InvalidParameter") + newAsset.getAssetOwnerName());
                newAsset.setAssetOwnerName(null);
            }

            List<Supplier> supplierList = getSupplierListByName(newAsset.getVendor());
            if (null == supplierList || supplierList.isEmpty()) {
                Supplier newSupplier = new Supplier();
                newSupplier.setSiteId(siteId);
                newSupplier.setName(newAsset.getVendor());
                supplierDao.save(newSupplier);
                newAsset.setSupplierId(newSupplier.getId());
            } else if (supplierList.size() == 1) {
                Supplier supplier = supplierList.get(0);
                newAsset.setSupplierId(supplier.getId());
            } else {
                addErrMessage(item, WebUtil.getMessage("InvalidParameter") + (String) item.get("superlierName"));
                newAsset.setVendor(null);
            }

            List<AssetInfo> assetList = getAssetInfos(newAsset);
            if (null == assetList || assetList.isEmpty()) {
                String qrCode = newAsset.getQrCode();
                if (isAvailableQrcode(qrCode, item)) {
                    createAsset(newAsset);
                    item.put("status", ImportStatus.Created);
                } else {
                    item.put("status", ImportStatus.Failure);
                    addErrMessage(item, WebUtil.getMessage("InvalidParameter") + "QRCode:" + qrCode);
                }

            } else if (assetList.size() > 1) {
                item.put("status", ImportStatus.Failure);
                addErrMessage(item, WebUtil.getMessage("InvalidParameter") + newAsset.getName() + "/" + newAsset.getDepartNum());
            } else {
                item.put("asset", assetList.get(0));
                item.put("status", ImportStatus.Exist);
            }

        });

    }

    private Boolean isAvailableQrcode(String qrcode, Map<String, Object> item) {
        if (null == qrcode || qrcode.isEmpty()) {
            return true;
        }

        QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(qrcode);
        if (qrCodeLib == null) {
            addErrMessage(item, WebUtil.getMessage("InvalidQRCode") + qrcode);
            return false;
        }
        if (qrCodeLib.getSiteId() != siteId || qrCodeLib.getHospitalId() != hospitalId) {
            addErrMessage(item, WebUtil.getMessage("WrongHospitalQRCode"));
            return false;
        }
        if (qrCodeLib.getStatus() != 1) {
            addErrMessage(item, WebUtil.getMessage("AlreadyUsingQrCode"));
            return false;
        }

        return true;
    }

    public Boolean exportFailData(ExcelDocument doc, Map<String, Map<String, Object>> importOrgMap, Map<String, Map<String, Object>> importAssetMap) {
        Integer keyRowOrg = 4;
        Integer dataRowOrg = 5;
        Boolean hasFailData = false;
        doc.clearRowsData(SHEET_ORG, 5, 4+importOrgMap.values().size());
        for (Map<String, Object> item : importOrgMap.values()) {
            if (item.get("status").equals(ImportStatus.Failure)) {
                doc.writeRowData(SHEET_ORG, keyRowOrg, dataRowOrg++, (Map<String, Object>) item.get("rowData"));
                hasFailData = true;
            }
        }

        Integer keyRowAsset = 6;
        Integer dataRowAsset = 7;
        doc.clearRowsData(SHEET_ASSET, 7, 6+importAssetMap.values().size());
        for (Map<String, Object> item : importAssetMap.values()) {
            if (item.get("status").equals(ImportStatus.Failure)) {
                doc.writeRowData(SHEET_ASSET, keyRowAsset, dataRowAsset++, (Map<String, Object>) item.get("rowData"));
                hasFailData = true;
            }
        }
//        doc.clearRowsData(SHEET_ASSET, dataRowAsset, 6+importAssetMap.values().size());

//        doc.clearRowsData(SHEET_USER, 5, 6);

        return hasFailData;
    }

    @Transactional
    private void createAsset(AssetInfo asset) {
        assetDao.save(asset);
        if (null != asset.getQrCode() || !asset.getQrCode().isEmpty()) {
            QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(asset.getQrCode());
            qrCodeLib.setStatus(3);
            qrcodeDao.save(qrCodeLib);
        }
        assetDepreciationService.saveAssetDerpeciation(asset);
    }

    private List<OrgInfo> getOrgInfoByName(OrgInfo org) {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, hospitalId));
        OrgInfoFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, siteId));
        OrgInfoFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, org.getName()));
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    private List<AssetInfo> getAssetInfos(AssetInfo asset) {
        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, hospitalId));
        assetFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, siteId));
        assetFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, asset.getName()));
        assetFilters.add(new SearchFilter("departNum", SearchFilter.Operator.EQ, asset.getDepartNum()));
        return assetDao.findBySearchFilter(assetFilters);
    }

    private List<UserAccount> getUserInfosByName(String userName) {
        List<SearchFilter> userFilters = new ArrayList<>();
        userFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, hospitalId));
        userFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, siteId));
        userFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, userName));
        return userDao.findBySearchFilter(userFilters);
    }

    private List<Supplier> getSupplierListByName(String supplierName) {
        List<SearchFilter> supplierFilters = new ArrayList<>();
        supplierFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, siteId));
        supplierFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, supplierName));
        return supplierDao.findBySearchFilter(supplierFilters);
    }

}
