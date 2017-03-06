package com.ge.apm.web;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
        String a = request.getParameter("urix").trim();
        if(externalLoginHandler.doLoginWithPlainPassword(userName, password, request, response)){
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

    @RequestMapping(value = "/test/{id}", method = RequestMethod.GET )
    public @ResponseBody Object getData(@PathVariable String id) {
        int a = Integer.parseInt(id);
        return a;
    }
    
}
