package com.ge.apm.service.pm;

import java.time.Period;
import java.util.Map;

import org.apache.camel.Headers;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.ge.apm.dao.PmOrderRepository;
import com.ge.apm.domain.PmOrder;

@Service
public class PmOrderAggregateService {
	
	Logger logger = LoggerFactory.getLogger(PmOrderAggregateService.class);
	
	@Autowired
	PmOrderRepository pmOrderRepository;
	
	public void updatePmOrder(@Headers Map<String, Object> map){
		Integer assetId = (Integer) map.get("assetId");
		logger.info("begin to updatePmOrderData");
		PageRequest pr = buildPageRequest(0,2,"startTime");
		Page<PmOrder> orders = pmOrderRepository.findByAssetId(assetId,pr);
		if(!orders.hasContent()){
			logger.warn("can't find pmOrders by assetId :{}",assetId);
			return;
		}
		if(orders.getSize() == 1){
			PmOrder order = orders.getContent().get(0);
			order.setNearestDays(-1);
			order.setNearestPmId(-1);
			pmOrderRepository.save(order);
			return;
		}
		PmOrder order1 = orders.getContent().get(0);
		PmOrder order2 = orders.getContent().get(1);
		DateTime dt1 = new DateTime(order1.getCreateTime());
		DateTime dt2 = new DateTime(order2.getCreateTime());
		Integer days = Math.abs(Days.daysBetween(dt1, dt2).getDays());
		order1.setNearestDays(days);
		order1.setNearestPmId(order2.getId());
		pmOrderRepository.save(order1);
		
	
	}
	
    //构建PageRequest
    private PageRequest buildPageRequest(int pageNumber, int pagzSize,String createTime) {
        //Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
    	Sort sort = new Sort(Sort.Direction.DESC, createTime);
        return new PageRequest(pageNumber, pagzSize, sort);
    }
}
