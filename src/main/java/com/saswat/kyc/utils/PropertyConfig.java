package com.saswat.kyc.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyConfig {	
	

	@Value("${primary.spring.datasource.url}")
	private String casDatasourceUrl;
	@Value("${primary.spring.datasource.username}")
	private String casDatasourceUsername;
	@Value("${primary.spring.datasource.password}")
	private String casDatasourcePassword;
	
	
	@Value("${datasource.hikari.connection-timeout}")
	private String hikariConnectionTimeout;
	@Value("${datasource.hikari.minimum-idle}")
	private String hikariMinimumIdle;
	@Value("${datasource.hikari.maximum-pool-size}")
	private String hikariMaxPoolSize;
	@Value("${datasource.hikari.idle-timeout}")
	private String hikariIdleTimeout;
	@Value("${datasource.hikari.max-lifetime}")
	private String hikariMaxLifeTime;
	@Value("${datasource.hikari.auto-commit}")
	private String hikariAutoCommit;
	
	@Value("${spring.jpa.show-sql}")
	private String springJpaShowSql;
	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String springJpaddlAuto;
	@Value("${spring.jpa.properties.hibernate.dialect}")
	private String springJpaHibernateDialect;
	
	@Value("${postgres.db.driverClassName}")
	private String postgresDriverClassName;
	
	public String getSpringJpaShowSql() {
		return springJpaShowSql;
	}

	public void setSpringJpaShowSql(String springJpaShowSql) {
		this.springJpaShowSql = springJpaShowSql;
	}

	

	
	
	
	public String getPostgresDriverClassName() {
		return postgresDriverClassName;
	}

	public void setPostgresDriverClassName(String postgresDriverClassName) {
		this.postgresDriverClassName = postgresDriverClassName;
	}

	public String getCasDatasourceUrl() {
		return casDatasourceUrl;
	}

	public void setCasDatasourceUrl(String casDatasourceUrl) {
		this.casDatasourceUrl = casDatasourceUrl;
	}

	public String getCasDatasourceUsername() {
		return casDatasourceUsername;
	}

	public void setCasDatasourceUsername(String casDatasourceUsername) {
		this.casDatasourceUsername = casDatasourceUsername;
	}

	public String getCasDatasourcePassword() {
		return casDatasourcePassword;
	}

	public void setCasDatasourcePassword(String casDatasourcePassword) {
		this.casDatasourcePassword = casDatasourcePassword;
	}

	public String getHikariConnectionTimeout() {
		return hikariConnectionTimeout;
	}

	public void setHikariConnectionTimeout(String hikariConnectionTimeout) {
		this.hikariConnectionTimeout = hikariConnectionTimeout;
	}

	public String getHikariMinimumIdle() {
		return hikariMinimumIdle;
	}

	public void setHikariMinimumIdle(String hikariMinimumIdle) {
		this.hikariMinimumIdle = hikariMinimumIdle;
	}

	public String getHikariMaxPoolSize() {
		return hikariMaxPoolSize;
	}

	public void setHikariMaxPoolSize(String hikariMaxPoolSize) {
		this.hikariMaxPoolSize = hikariMaxPoolSize;
	}

	public String getHikariIdleTimeout() {
		return hikariIdleTimeout;
	}

	public void setHikariIdleTimeout(String hikariIdleTimeout) {
		this.hikariIdleTimeout = hikariIdleTimeout;
	}

	public String getHikariMaxLifeTime() {
		return hikariMaxLifeTime;
	}

	public void setHikariMaxLifeTime(String hikariMaxLifeTime) {
		this.hikariMaxLifeTime = hikariMaxLifeTime;
	}

	public String getHikariAutoCommit() {
		return hikariAutoCommit;
	}

	public void setHikariAutoCommit(String hikariAutoCommit) {
		this.hikariAutoCommit = hikariAutoCommit;
	}

	public String getSpringJpaddlAuto() {
		return springJpaddlAuto;
	}

	public void setSpringJpaddlAuto(String springJpaddlAuto) {
		this.springJpaddlAuto = springJpaddlAuto;
	}

	public String getSpringJpaHibernateDialect() {
		return springJpaHibernateDialect;
	}

	public void setSpringJpaHibernateDialect(String springJpaHibernateDialect) {
		this.springJpaHibernateDialect = springJpaHibernateDialect;
	}
	
}
