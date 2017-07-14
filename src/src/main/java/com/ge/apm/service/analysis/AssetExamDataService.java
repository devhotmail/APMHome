package com.ge.apm.service.analysis;

import com.ge.apm.dao.AssetSummitRepository;
import com.ge.apm.dao.mapper.AssetSummitMapper;
import com.ge.apm.domain.AssetSummit;
import com.ge.apm.domain.AssetSummitMaxMinPojo;
import com.ge.apm.domain.BatchAssetExam;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lsg on 7s/3/2017.
 */

@Service
public class AssetExamDataService {
    @Autowired
    AssetSummitMapper assetSummitMapper;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final int DAY = 1;
    @Autowired
    AssetExamDataAggregator assetExamDataAggregator;
    @Value("#{ratingProperties.netprofit}")
    private String netprofit;
    @Value("#{ratingProperties.injectCount}")
    private String injectCount;
    @Value("#{ratingProperties.exposeCount}")
    private String exposeCount;
    @Value("#{ratingProperties.filmCount}")
    private String filmCount;


    /***
     * 	按当天的聚合
     */
    @Transactional
    public String assetAssetSumitAggregatorByday(BatchAssetExam batchAssetExam){
        if(batchAssetExam == null){
            return "illegal param";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(batchAssetExam.getCalDay());
            assetExamDataAggregator.aggregateForAssetSumit(date);
        } catch (Exception e) {
            logger.error("assetExamAggregatorByday failed ",e.getMessage());
            return "failue";
        }
        return "success";
    }
    @Autowired
    AssetSummitRepository assetSummitRepository;
    /***
     * 	按选定的日期聚合
     */
    public String aggregateAssetSummitRangeDay(BatchAssetExam bac){
        {
            if(bac == null){
                return "illegal param";
            }
            if(StringUtils.isEmpty(bac.getFrom()) || StringUtils.isEmpty(bac.getTo())){
                return "from or to is empty !";
            }
            try{
                DateTime from = new DateTime(bac.getFrom());
                DateTime to = new DateTime(bac.getTo());
                //该方法会将某天创建新设备加入到asset_summit为了之后做统计
              //  assetExamDataAggregator.initAssetAggregationDataByDateRange(from.toDate(), to.toDate());
                while(from.isBefore(to)||from.isEqual(to)){
                    logger.info("calByFromTo begin ,current day is {}",from.toString());
                    assetExamDataAggregator.aggregateForAssetSumit(from.toDate());

                    from = from.plusDays(DAY);
                }
                return "success";
            }catch(Exception e){
                logger.error("calByFromTo error,param is {}",bac);
                logger.error("calByFromTo error,message is {}",e.getMessage());
                return "failue";
            }
        }
    }
    @Transactional
    public String aggregateExamSumitRangeDay(BatchAssetExam bac){
        {
            if(bac == null){
                return "illegal param";
            }
            if(StringUtils.isEmpty(bac.getFrom()) || StringUtils.isEmpty(bac.getTo())){
                return "from or to is empty !";
            }
            try{
                DateTime from = new DateTime(bac.getFrom());
                DateTime to = new DateTime(bac.getTo());
               // assetExamDataAggregator.initAssetAggregationDataByDateRange(from.toDate(), to.toDate());
                while(from.isBefore(to)||from.isEqual(to)){
                    logger.info("aggrateExamSummitbyRange begin ,current day is {}",from.toString());
                    assetExamDataAggregator.aggregateForExamSumit(from.toDate());
                    from = from.plusDays(DAY);
                }
                return "success";
            }catch(Exception e){
                logger.error("aggrateExamSummitbyRange error,param is {}",bac);
                logger.error("aggrateExamSummitbyRange error,message is {}",e.getMessage());
                return "failue";
            }
        }
    }

    /***
     * 	计算rating的值
     */
    @Transactional
    public String calRating() {

        double nprofit=0.0f,ic=0.0f,ec=0.0f,fc=0.0f,weightedSum=0.0f,np=0.0f;
        //min max based on 1 year
        //gl: nest sql are not support in HQL ,take mybatis here to look for max and min
        try{
            /*按assetid和created聚合，获取assetSummit中最大最小的inject film expose——count的值*/
            AssetSummitMaxMinPojo asmm = assetSummitMapper.fetchAssetSummit();

            //update records with rating is null
            List<AssetSummit> assetSummit = assetSummitRepository.findAssetSummitByRating();
            logger.info( "gl define: netprofit"+netprofit+" injectCount: "+injectCount+" exposeCount: "+exposeCount+" filmCount: "+filmCount);
            for (AssetSummit asm : assetSummit) {
                if(asm.getRevenue()!=null && asm.getMaintenanceCost() !=null && asm.getDeprecationCost() !=null){
                    nprofit=asm.getRevenue()-asm.getMaintenanceCost()-asm.getDeprecationCost();
                    np=(nprofit-asmm.getNpMin())/(asmm.getNpMax()-asmm.getNpMin());
                    ic=(asm.getInjectCount()-asmm.getIcMin())/(asmm.getIcMax()-asmm.getIcMin());
                    ec=(asm.getExposeCount()-asmm.getEcMin())/(asmm.getEcMax()-asmm.getEcMin());
                    fc=(asm.getFilmCount()-asmm.getFmMin())/(asmm.getFmMax()-asmm.getFmMin());
                    weightedSum=np*Double.valueOf(netprofit)+ic*Double.valueOf(injectCount)+ec*Double.valueOf(exposeCount)+fc*Double.valueOf(filmCount);                    logger.info( "gl: nprofit"+nprofit+"np: "+np+" ic: "+ic+" ec: "+ec+" fc: "+fc);
                    asm.setRating(weightedSum);
                    assetSummitRepository.save(asm);
                } else {
                    logger.info(asm.getAssetId()+" with null on revenue or maintenanceCost or deprecationCost");
                    continue;
                }

            }
            return "success";

        }catch(Exception ex){
            logger.error(ex.toString());
            return "failure";
        }

    }


}