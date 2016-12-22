/*
 */
package com.ge.apm.edgeserver;

import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author 212547631
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.ge.apm.edgeserver.dataupload.dao", 
    entityManagerFactoryRef = "entityManager", 
    transactionManagerRef = "transactionManager"
)
public class AppDbConfig {
    //@Autowired
    //private Environment env;
/*     
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(edgeDataSource());
        em.setPackagesToScan(new String[] { "com.ge.apm.edgeserver.dataupload.entity" });
        em.setPersistenceUnitName("edgePU");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
 
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.connection.useUnicode", "true");
        properties.put("hibernate.connection.charSet", "UTF-8");
        em.setJpaPropertyMap(properties);
 
        return em;
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager().getObject());
        return transactionManager;
    }
*/
    @Primary
    @Bean
    public DataSource edgeDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://127.0.0.1:5432/ge_apm");
        dataSource.setUsername("postgres");
        dataSource.setPassword("root");
 
        return dataSource;
    }
    
}
