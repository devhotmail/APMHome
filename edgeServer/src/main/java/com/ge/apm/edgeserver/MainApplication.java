package com.ge.apm.edgeserver;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableAutoConfiguration
@ImportResource("classpath:spring.xml")
public class MainApplication {

    public static void main(String[] args) {
        //new SpringApplicationBuilder(MainApplication.class).web(true).run(args);        
        SpringApplication.run(MainApplication.class, args);
    }
/*
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        //factory.setPort(8090);
        return factory;
     }
*/    
    @Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        CamelHttpTransportServlet servlet = new CamelHttpTransportServlet();
        ServletRegistrationBean servletBean = new ServletRegistrationBean(servlet, "/ws/*");
        servletBean.setLoadOnStartup(1);
        servletBean.setName("myServlet");
        return servletBean;
    }

    @Bean
    public ServletRegistrationBean cxfServletRegistrationBean() {
        org.apache.cxf.transport.servlet.CXFServlet servlet = new org.apache.cxf.transport.servlet.CXFServlet();
        ServletRegistrationBean servletBean = new ServletRegistrationBean(servlet, "/soap/*");
        servletBean.setLoadOnStartup(1);
        servletBean.setName("cxfServlet");
        
        return servletBean;
    }

}
