package com.saswat.kyc.utils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Configuration
public class MultipleDBConfiguration {

	private static Logger logger = LoggerFactory.getLogger(MultipleDBConfiguration.class);

	@Autowired
	private Environment env;
	
	@Autowired
	private PropertyConfig propertyConfig;

	private static volatile Map<String, EntityManagerFactory> datasourceFactory = new ConcurrentHashMap<>();
	
	private EntityManagerFactory createEntityManager(String instanceIdentifier, String packageName) {

		EntityManagerFactory entityManagerFactory = null;
		
		LocalContainerEntityManagerFactoryBean em = null;

		String instanceDetails = env.getProperty(instanceIdentifier);
		if (instanceDetails == null) {
			logger.error("database instance is not configured {} " , instanceIdentifier);
			throw new RuntimeException("Contact Saswat support.");
		}
		String[] dataSourceDetails = instanceDetails.split(",");

		String instanceAndDB = instanceIdentifier + packageName;

		if ((entityManagerFactory = datasourceFactory.get(instanceAndDB)) == null) {
			synchronized (MultipleDBConfiguration.class) {
				if ((entityManagerFactory = datasourceFactory.get(instanceAndDB)) == null) {
					logger.info("Creating entityManager for instanceIdentifier {}  with package name {}",instanceIdentifier,packageName);

					StringBuilder dbUrlBuilder = new StringBuilder();
					dbUrlBuilder.append("jdbc:postgresql://").append(dataSourceDetails[0]).append(":")
							.append(dataSourceDetails[1]).append("/").append(dataSourceDetails[2]);

					final Properties properties = new Properties();

					properties.put("hibernate.dialect", propertyConfig.getSpringJpaHibernateDialect());
					properties.put("hibernate.hbm2ddl.auto", propertyConfig.getSpringJpaddlAuto());
					properties.put("hibernate.show_sql", propertyConfig.getSpringJpaShowSql());

					em = new LocalContainerEntityManagerFactoryBean();
					em.setDataSource(configureHikariDataSource(dataSourceDetails, dbUrlBuilder));
					em.setPackagesToScan(packageName);
					em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
					em.setJpaProperties(properties);
					em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
					em.afterPropertiesSet();
					datasourceFactory.put(instanceAndDB, entityManagerFactory = em.getObject());
				}
			}
		}

		return entityManagerFactory;
	}

	private HikariDataSource configureHikariDataSource(String[] dataSourceDetails, StringBuilder dbUrlBuilder) {
		HikariConfig dataSourceConfig = new HikariConfig();
		dataSourceConfig.setJdbcUrl(dbUrlBuilder.toString());
		dataSourceConfig.setUsername(dataSourceDetails[3]);
		dataSourceConfig.setPassword(dataSourceDetails[4]);
		dataSourceConfig.setAutoCommit(false);
		dataSourceConfig.setMaximumPoolSize(Runtime.getRuntime().availableProcessors() + 4);
		dataSourceConfig.setMinimumIdle(1);
		dataSourceConfig.setIdleTimeout(Integer.parseInt(propertyConfig.getHikariIdleTimeout()));
		

		HikariDataSource dataSource = new HikariDataSource(dataSourceConfig);
		return dataSource;
	}
	
	public <T> EntityManager getDynamicEntityManager(T t, String instanceIdentifier) {
		return t == null || instanceIdentifier == null ? null
				: this.createEntityManager("" + instanceIdentifier, t.getClass().getPackage().getName()).createEntityManager();
	}
}
