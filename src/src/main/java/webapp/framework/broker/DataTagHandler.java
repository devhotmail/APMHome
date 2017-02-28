package webapp.framework.broker;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class DataTagHandler {

    private static final Logger logger = Logger.getLogger(DataTagHandler.class);

    protected Map<String, String> tagNames = new HashMap<String, String>();
    
    public DataTagHandler(String defaultTagFromTables, Map<String, String> tagNames) throws IllegalArgumentException, Exception {
        this.tagNames = tagNames;
    }

    public void getTagFromTables(){
        
    }
}
