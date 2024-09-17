package com.saswat.kyc.config;

// Using jakarta.persistence instead of javax
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;

import java.util.HashMap;

import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "casEntityManagerFactory", transactionManagerRef = "casTransactionManager", basePackages = "com.saswat.kyc.repository" // JPA
																																										// repositories
																																										// location
)
public class CasDBConfig {

	@Autowired
	private PropertyConfig propertyConfig; // Configuration for properties

	@Autowired
	private Environment env; // Spring environment to load properties

	@Primary
	@Bean(name = "casDataSource")
	@ConfigurationProperties(prefix = "spring.datasource") // This binds properties to the DataSource configuration
	public DataSource dataSource() {
		HikariConfig dataSourceConfig = new HikariConfig();
		dataSourceConfig.setJdbcUrl(propertyConfig.getCasDatasourceUrl()); // Custom URL from the PropertyConfig
		dataSourceConfig.setUsername(propertyConfig.getCasDatasourceUsername());
		dataSourceConfig.setPassword(propertyConfig.getCasDatasourcePassword());
		dataSourceConfig.setAutoCommit(false); // Disable auto-commit for manual transaction handling
		dataSourceConfig.setMaximumPoolSize(Runtime.getRuntime().availableProcessors() + 4); // Dynamic pool size
		dataSourceConfig.setMinimumIdle(1); // Minimum idle connections
		dataSourceConfig.setIdleTimeout(Long.parseLong(propertyConfig.getHikariIdleTimeout())); // Idle timeout

		return new HikariDataSource(dataSourceConfig); // Return HikariCP DataSource
	}

	@Primary
	@Bean(name = "casEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("casDataSource") DataSource dataSource) {

		// Hibernate/JPA properties
		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", propertyConfig.getSpringJpaddlAuto()); // ddl-auto mode from config
		properties.put("hibernate.dialect",
				env.getProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")); // Set
																														// Hibernate
																														// dialect

		return builder.dataSource(dataSource) // Attach the primary DataSource
				.packages("com.saswat.kyc.model") // Specify the package where your JPA entities are located
				.persistenceUnit("casTxnPU") // Define a persistence unit
				.properties(properties) // Additional JPA properties
				.build();
	}

	/**
	 * Define the primary Transaction Manager for managing transactions.
	 */
	@Primary
	@Bean(name = "casTransactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("casEntityManagerFactory") EntityManagerFactory entityManagerFactory) {

		// Use JpaTransactionManager with the primary EntityManagerFactory
		return new JpaTransactionManager(entityManagerFactory);
	}
}
