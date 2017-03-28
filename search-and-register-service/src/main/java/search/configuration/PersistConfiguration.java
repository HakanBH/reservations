package search.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Created by Toncho_Petrov on 7/14/2016.
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "search.repository")
public class PersistConfiguration {

    @Value("${spring.datasource.url}")
    String jdbcUrl;

    @Value("${spring.datasource.username}")
    String jdbcUser;

    @Value("${spring.datasource.password}")
    String jdbcPass;

    @Value("${spring.database.driverClassName}")
    String driver;

    @Value("${spring.datasource.className}")
    String datasourceClassName;

    @Value("${hibernate.dialect}")
    String hibernateDialect;

    @Bean
    public HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(datasourceClassName);
//        config.setDriverClassName(driver);
        config.setInitializationFailFast(false);
        config.setConnectionTestQuery("Select 1");

        Properties dsProperties = new Properties();
        dsProperties.put("url", jdbcUrl + "?useUnicode=true&characterEncoding=utf-8");
        dsProperties.put("user", jdbcUser);
        dsProperties.put("password", jdbcPass);

        config.setDataSourceProperties(dsProperties);
        return config;
    }

    @Bean
    @Primary
    public HikariDataSource dataSource() {
        HikariDataSource ds = new HikariDataSource(getHikariConfig());
        return ds;
    }

    @Bean(name = "jpaPropertyMap")
    public Map<String, ?> getJpaPropertyMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("hibernate.format_sql", true);
        map.put("hibernate.show_sql", false);
        map.put("hibernate.connection.characterEncoding", "utf8");
        map.put("hibernate.connection.useUnicode", true);
        map.put("hibernate.connection.CharSet", "utf-8");
        return map;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(this.dataSource());
        em.setPackagesToScan("search.model");
        em.setJpaPropertyMap(this.getJpaPropertyMap());
        em.setJpaVendorAdapter(this.getHibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    public HibernateJpaVendorAdapter getHibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.POSTGRESQL);
        adapter.setShowSql(true);
        adapter.setDatabasePlatform(hibernateDialect);
        return adapter;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(this
                .entityManagerFactory().getObject());
        return jpaTransactionManager;
    }
}
