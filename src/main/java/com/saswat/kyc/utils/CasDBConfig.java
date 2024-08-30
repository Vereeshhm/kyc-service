package com.saswat.kyc.utils;

import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "casEntityManagerFactory", 
    transactionManagerRef = "casTransactionManager", 
    basePackages = {"com.saswat.kyc.repository"}
)
public class CasDBConfig {

    @Autowired
    private PropertyConfig propertyConfig;

    @Autowired
    private Environment env;

    @Primary
    @Bean(name = "casDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() { 
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setJdbcUrl(propertyConfig.getCasDatasourceUrl());
        dataSourceConfig.setUsername(propertyConfig.getCasDatasourceUsername());
        dataSourceConfig.setPassword(propertyConfig.getCasDatasourcePassword());
        dataSourceConfig.setAutoCommit(false);
        dataSourceConfig.setMaximumPoolSize(Runtime.getRuntime().availableProcessors() + 4);
        dataSourceConfig.setMinimumIdle(1);
        dataSourceConfig.setIdleTimeout(Integer.parseInt(propertyConfig.getHikariIdleTimeout()));

        return new HikariDataSource(dataSourceConfig);
    }

    @Primary
    @Bean(name = "casEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("casDataSource") DataSource dataSource) {
        Map<String, Object> properties = new HashMap<>(); 
        properties.put("hibernate.hbm2ddl.auto", propertyConfig.getSpringJpaddlAuto());

        return builder
                .dataSource(dataSource)
                .packages("com.saswat.kyc.model") // Ensure this package contains your entities
                .persistenceUnit("casTxnPU")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = "casTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("casEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
