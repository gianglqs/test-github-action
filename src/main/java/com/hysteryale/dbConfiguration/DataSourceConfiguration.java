package com.hysteryale.dbConfiguration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DataSourceConfiguration {
    @Bean(name = "postgresDataSource")
    @ConfigurationProperties("spring.datasource")
    @Primary
    public DataSource postgresDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "h2DataSource")
    @ConfigurationProperties("spring.datasource2")
    public DataSource h2dataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name="postgresTransactionManager")
    @Autowired
    @Primary
    DataSourceTransactionManager postgresTransactionManager(@Qualifier("postgresDataSource") DataSource datasource) {
        return new DataSourceTransactionManager(datasource);
    }

    /**
     * Configuration for using H2 Database
     */
    @Bean(name="transactionManager")
    @Autowired
    DataSourceTransactionManager transactionManager(@Qualifier ("h2DataSource") DataSource datasource) {
        return new DataSourceTransactionManager(datasource);
    }
}
