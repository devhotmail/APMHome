package webapp.framework.broker;


import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbProcedure extends StoredProcedureBean {
    
    public DbProcedure(final DataSource dataSource,
                               final String sqlTypesClassName,
                               final String storedProcedureName,
                               final boolean isFunction,
                               final Map<String, String> parameters)  throws IllegalArgumentException {
        super(dataSource, storedProcedureName);
        
        List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
        for(Map.Entry<String, String> item: parameters.entrySet()){
            Map<String, Object> param = new HashMap<String, Object>();
            paramList.add(param);
            
            param.put("name", item.getKey());
            String[] paramTypes = item.getValue().split(",");
            param.put("type", paramTypes[0]);
            param.put("mode", paramTypes[1]);
        }
        
        initProc(sqlTypesClassName, isFunction, paramList);
    }
}
