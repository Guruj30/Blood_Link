package com.bloodlink.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Explicit DataSource configuration activated strictly when JDBC_MYSQL persistence mode is enabled.
 * Prevents connection failure exceptions when running in standalone IN_MEMORY test mode.
 * Owned by Member 4 (Integration & Database Connection).
 */
@Configuration
@ConditionalOnProperty(name = "bloodlink.persistence.mode", havingValue = "JDBC_MYSQL")
public class DataSourceConfig {

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/bloodlink?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}")
    private String url;

    @Value("${spring.datasource.username:root}")
    private String username;

    @Value("${spring.datasource.password:password}")
    private String password;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
