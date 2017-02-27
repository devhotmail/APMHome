/*
 */
package webapp.framework.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.broker.SiBroker;

/**
 *
 * @author 212547631
 */
public class NativeSqlUtil {

    public static void execute(String sqlStatement, Map<String, Object> sqlParams) {
        if (sqlParams == null) {
            sqlParams = new HashMap<String, Object>();
        }
        sqlParams.put("_sql", sqlStatement);

        SiBroker.sendMessageWithHeaders("direct:executeNativeSQL", null, sqlParams);
    }

    public static List<Map<String, Object>> queryForList(String sqlStatement, Map<String, Object> sqlParams) {
        if (sqlParams == null) {
            sqlParams = new HashMap<String, Object>();
        }
        sqlParams.put("_sql", sqlStatement);

        return (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, sqlParams);
    }

    public static Page<Map<String, Object>> queryForPage(PageRequest pageRequest, String countSQL, String sql, Map<String, Object> params) {
        params.put("_sql", countSQL);

        //get count of rows: execute the countSQL
        List<Map<String, Object>> result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);
        int rowCount = Integer.parseInt(result.get(0).values().toArray()[0].toString());

        params.put("_sql", sql);
        params.put("_pageStart", pageRequest.getPageNumber() * pageRequest.getPageSize());
        params.put("_pageSize", pageRequest.getPageSize());
        result = (List<Map<String, Object>>) SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, params);

        return new PageImpl<Map<String, Object>>(result, pageRequest, rowCount);
    }
  
}
