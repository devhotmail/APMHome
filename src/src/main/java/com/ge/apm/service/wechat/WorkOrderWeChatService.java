/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wechat;

import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.service.UserContext;

/**
 *
 * @author 212595360
 */
@Service
public class WorkOrderWeChatService {
    
    @Autowired
    protected WorkOrderRepository woDao;
    
    public List<WorkOrder> woList(HttpServletRequest request) {
        List<SearchFilter> searchFilters = new ArrayList<>();
        UserAccount ua = UserContext.getCurrentLoginUser(request);
        searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, ua.getId()));
        searchFilters.add(new SearchFilter("isClosed", SearchFilter.Operator.EQ, false));
        return woDao.findBySearchFilter(searchFilters);
    }
    
}
