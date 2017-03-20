package webapp.framework.broker;

import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import webapp.framework.web.WebUtil;

import java.util.Map;

/**
 *
 * @author 212547631
 */
public class SiBroker {
    public static String defaultBrokerContextId = "risSiBrokerContext";

    public static CamelContext getBrokerContext(String brokerContextId){
        return WebUtil.getServiceBean(brokerContextId, CamelContext.class);
    }

    public static void sendMessage(String endpointName, Object message){
        sendMessage(defaultBrokerContextId, endpointName, message);
    }
    
    public static void sendMessage(String brokerContextId, String endpointName, Object message){
        CamelContext context = WebUtil.getServiceBean(brokerContextId, CamelContext.class);
        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody(endpointName, message);
    }

    public static void sendMessageWithHeaders(String endpointName, Object message, Map<String, Object> headers){
        sendMessageWithHeaders(defaultBrokerContextId, endpointName, message, headers);
    }
    public static void sendMessageWithHeaders(String brokerContextId, String endpointName, Object message, Map<String, Object> headers){
        CamelContext context = WebUtil.getServiceBean(brokerContextId, CamelContext.class);
        ProducerTemplate template = context.createProducerTemplate();
        template.sendBodyAndHeaders(endpointName, message, headers);
    }
    
    public static Object sendRequest(String endpointName, Object message){
        return sendRequest(defaultBrokerContextId, endpointName, message);
    }
    public static Object sendRequest(String brokerContextId, String endpointName, Object message){
        CamelContext context = WebUtil.getServiceBean(brokerContextId, CamelContext.class);
        ProducerTemplate template = context.createProducerTemplate();
        return template.sendBody(endpointName, ExchangePattern.InOut, message);
    }

    public static Object sendRequestWithHeaders(String endpointName, Object message, Map<String, Object> headers){
        return sendRequestWithHeaders(defaultBrokerContextId, endpointName, message, headers);
    }
    public static Object sendRequestWithHeaders(String brokerContextId, String endpointName, Object message, Map<String, Object> headers){
        CamelContext context = WebUtil.getServiceBean(brokerContextId, CamelContext.class);
        ProducerTemplate template = context.createProducerTemplate();
        return template.sendBodyAndHeaders(endpointName, ExchangePattern.InOut, message, headers);
    }
}
