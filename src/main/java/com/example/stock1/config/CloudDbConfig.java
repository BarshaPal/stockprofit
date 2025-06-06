package com.example.stock1.config;




import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.Driver;

@Configuration
@EnableTransactionManagement
@Profile("cloud")
@EnableJpaRepositories(basePackages = "com.example.stock1.data")
@SuppressWarnings({"java:S3305"})
public class CloudDbConfig {

    @Autowired
    private VCAPConstants vcapConstants;

    @Bean
    @Primary
    public DataSource dataSource() {

        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName(Driver.class.getName())
                .url(vcapConstants.getPostgresURL())
                .username(vcapConstants.getPostgresUser())
                .password(vcapConstants.getPostgresPassword())
                .build();

        dataSource.setLeakDetectionThreshold(20000);
        dataSource.setMaxLifetime(300000);
        dataSource.setRegisterMbeans(Boolean.TRUE);

        dataSource.setMaximumPoolSize(20);

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
                new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPackagesToScan("com.example.stock1.entity");
        entityManagerFactoryBean.setDataSource(dataSource);

        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.afterPropertiesSet();

        return entityManagerFactoryBean;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory,
                                                         DataSource dataSource) {

        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        jpaTransactionManager.setDataSource(dataSource);
        return jpaTransactionManager;
    }
}
