package com.ge.apm.web;

import webapp.framework.context.WrappedRequest;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import webapp.framework.context.ExternalLoginHandler;

/**
 *
 * @author wujianb
 */
@Controller
public class ProxyController {

    @Autowired
    ExternalLoginHandler externalLoginHandler;
    
    //URL is like:
    // http://localhost:8080/rissi/web/go?un=itpschina&pw=iischina&uri=/home.xhtml&inpid=452&outpid=12
    @RequestMapping(value = "/go", method = RequestMethod.GET )
    public String proxyHandler(@RequestParam("un") String userName, @RequestParam("pw") String password, HttpServletRequest request, HttpServletResponse response) {
        //let spring security to authenticate sso user
        Map<String, String[]> extraParams = new TreeMap<String, String[]>();
        
        extraParams.put("j_username", new String[]{userName});
        extraParams.put("j_password", new String[]{password});
        HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);

        if(externalLoginHandler.doExternalLogin(wrappedRequest, response)){
            String uri = request.getParameter("uri");
            if(uri==null)
                return "redirect:/home.xhtml";
            else{
                uri = uri+"?";
                Map<String, String[]> params = request.getParameterMap();
                for(Map.Entry<String, String[]> param: params.entrySet()){
                    if("un".equals(param.getKey())) continue;
                    if("pw".equals(param.getKey())) continue;
                    if("uri".equals(param.getKey())) continue;
                    
                    uri = uri + param.getKey()+"="+param.getValue()[0]+"&";
                }
                return "redirect:"+uri;
            }
        }
        else
            return "redirect:/login.xhtml?msg=wrongLogin";
    }

}
