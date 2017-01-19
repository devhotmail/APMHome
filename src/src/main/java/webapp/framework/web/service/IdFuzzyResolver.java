package webapp.framework.web.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.primefaces.expression.SearchExpressionResolver;
import org.primefaces.expression.SearchExpressionResolverFactory;

/**
 *
 * @author 212547631
 */
public class IdFuzzyResolver implements SearchExpressionResolver {

    private static final String PREFIX = "@*";
    private static final Logger logger = Logger.getLogger(IdFuzzyResolver.class);
    
    private static final Map<String, UIComponent> ID_MAP = new HashMap<String, UIComponent>();

    @Override
    public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression) {
        UIComponent result = ID_MAP.get(expression);
        if(result!=null ) return result;
            
        String compId = expression.substring(3, expression.length() - 1);
        
        UIComponent parent = last.getParent();
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        
        result = parent.findComponent(compId);
        if(result==null){
            result = findIdRecursively(parent, compId);
            if(result!=null){
                String uid = result.getId();
                parent = result.getParent();
                while (parent.getParent() != null) {
                    uid = parent.getId() + "("+parent.getClass().getSimpleName()+"), " + uid;
                    parent = parent.getParent();
                }
                logger.info("------INFO: id="+expression+", clientID="+result.getClientId()+", full Path="+uid);
            }
        }
        
        if(result!=null){
            ID_MAP.put(expression, result);
            return result;
        }
        else
            throw new FacesException("cannot find ID="+expression);
    }

    public UIComponent findIdRecursively(UIComponent parent, String id) {
        if (null == parent) {
            return null;
        }
        if (id.equals(parent.getId())) {
            return parent;
        }
        Iterator<UIComponent> facetsAndChildren = parent.getFacetsAndChildren();
        while (facetsAndChildren.hasNext()) {
            UIComponent child = (UIComponent) facetsAndChildren.next();
            UIComponent result = findIdRecursively(child, id);
            if (null != result) {
                return result;
            }
        }
        return null;
    }

    public static void registerResolver() {
        SearchExpressionResolverFactory.registerResolver(PREFIX, new IdFuzzyResolver());
    }
}
