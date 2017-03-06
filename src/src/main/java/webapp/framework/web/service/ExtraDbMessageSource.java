package webapp.framework.web.service;

import java.util.HashMap;
import java.util.Map;
import webapp.framework.broker.JsonMapper;
import org.apache.log4j.Logger;

/**
 *
 * @author 212547631
 */
public class ExtraDbMessageSource {
    private static final Logger logger = Logger.getLogger(ExtraDbMessageSource.class);
    
    private static Map<String, String> extraMessages;
    public static void setExtraMessages(String extraMessageConfig) {
        try{
            extraMessages = JsonMapper.nonEmptyMapper().fromJson(extraMessageConfig.replace("'", "\""), Map.class);
        }
        catch(Exception ex){
            logger.error("error reading extraMessages.", ex);
            extraMessages = new HashMap<String, String>();
        }
    }

    public static Map<String, String> getExtraMessages() {
        return extraMessages;
    }
    
    
}
