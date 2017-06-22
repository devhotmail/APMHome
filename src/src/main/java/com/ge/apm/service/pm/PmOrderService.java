package com.ge.apm.service.pm;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PmOrderRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PmOrder;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import webapp.framework.broker.SiBroker;
import webapp.framework.dao.SearchFilter;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 212605082 on 2017/5/18.
 */
@Component
public class PmOrderService {

    @Autowired
    PmOrderRepository pmOrderDao;
    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private OrgInfoRepository orgInfoDao;
    @Autowired
    private UserAccountRepository userAccountDao;

    public void savePmOrder(AssetInfo assetInfo, Date startDate, Date endDate, Integer pmCount, UserAccount owner){

        UserAccount userAccount = UserContextService.getCurrentUserAccount();

        Instant startDateInstant = startDate.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(startDateInstant, zone);
        LocalDate startLocalDate = startLocalDateTime.toLocalDate();

        Instant endDateInstant = endDate.toInstant();
        LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(endDateInstant, zone);
        LocalDate endLocalDate = endLocalDateTime.toLocalDate();

        //Period period = Period.between(startLocalDate, endLocalDate);
        //Integer days = period.getDays()/(pmCount - 1);

        long daysDiff = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
        long days = daysDiff/(pmCount - 1);

        for(int i = 1 ; i <= pmCount ; i++){
            PmOrder pmOrder = new PmOrder();
            pmOrder.setName("pm_" + assetInfo.getName() + "_" + i);
            pmOrder.setAssetId(assetInfo.getId());
            pmOrder.setAssetName(assetInfo.getName());
            pmOrder.setCreatorId(userAccount.getId());
            pmOrder.setCreatorName(userAccount.getName());
            pmOrder.setCreateTime(new Date());
            pmOrder.setSiteId(userAccount.getSiteId());
            pmOrder.setHospitalId(userAccount.getHospitalId());

            if(owner != null){
                pmOrder.setOwnerId(owner.getId());
                pmOrder.setOwnerName(owner.getName());
                OrgInfo orgInfo = orgInfoDao.findById(owner.getOrgInfoId());
                pmOrder.setOwnerOrgId(orgInfo.getId());
                pmOrder.setOwnerOrgName(orgInfo.getName());
            }

            Instant plantimeInstant = null;
            if(pmCount == 1){
                plantimeInstant = startLocalDate.plus(days/2, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant();
            }else{
                if(i == 1){
                    plantimeInstant = startLocalDate.atStartOfDay().atZone(zone).toInstant();
                }else if(i == pmCount){
                    plantimeInstant = endLocalDate.atStartOfDay().atZone(zone).toInstant();
                }else{
                    plantimeInstant = startLocalDate.plus(days * (i - 1), ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant();
                }
            }

            pmOrder.setPlanTime(Date.from(plantimeInstant));

            /*Calendar calender = Calendar.getInstance();
            calender.setTime(Date.from(plantimeInstant));
            calender.add(Calendar.HOUR_OF_DAY, 23);
            calender.add(Calendar.MINUTE, 59);
            calender.add(Calendar.SECOND, 59);
            pmOrder.setPlanTime(calender.getTime());*/

            /*Instant startInstant = startLocalDate.atStartOfDay().atZone(zone).toInstant();
            pmOrder.setStartTime(Date.from(startInstant));
            startLocalDate = startLocalDate.plus(days, ChronoUnit.DAYS);
            Instant endInstant = startLocalDate.atStartOfDay().atZone(zone).toInstant();
            pmOrder.setEndTime(Date.from(endInstant));*/
            pmOrderDao.save(pmOrder);
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("assetId", assetInfo.getId());
            SiBroker.sendMessageWithHeaders("direct:updatePmOrder", null, param);
        }

    }

    public void saveBatchPmOrder(List<AssetInfo> assetInfoList, Date startDate, Date endDate, Integer pmCount, UserAccount owner){
        for (AssetInfo tempAssetInfo : assetInfoList) {
            if(tempAssetInfo.getAssetOwnerId() != null){
                this.savePmOrder(tempAssetInfo, startDate, endDate, pmCount, userAccountDao.findById(tempAssetInfo.getAssetOwnerId()));
            }else{
                this.savePmOrder(tempAssetInfo, startDate, endDate, pmCount, owner);
            }

        }
    }

    public List<AssetInfo> getAssetList(AssetInfo queryAsset) {

        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, queryAsset.getSiteId()));
        //assetFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, queryAsset.getHospitalId()));
        if (null != queryAsset.getHospitalId() && queryAsset.getHospitalId() != 0) {
            assetFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, queryAsset.getHospitalId()));
        }else{
            queryAsset.setClinicalDeptId(null);
        }
        if (null != queryAsset.getClinicalDeptId()) {
            assetFilters.add(new SearchFilter("clinicalDeptId", SearchFilter.Operator.EQ, queryAsset.getClinicalDeptId()));
        }
        if (null != queryAsset.getAssetGroup()) {
            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }

        return assetDao.findBySearchFilter(assetFilters);
    }


}
