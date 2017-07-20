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
	
    //构建PageRequest
    private PageRequest buildPageRequest(int pageNumber, int pagzSize,String createTime) {
        //Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
    	Sort sort = new Sort(Sort.Direction.DESC, createTime);
        return new PageRequest(pageNumber, pagzSize, sort);
    }
}
