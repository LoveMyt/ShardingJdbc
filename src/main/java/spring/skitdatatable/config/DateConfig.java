package spring.skitdatatable.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Author: yangfeng
 * @Description: todo
 * @Date: created in 17:27 2019/5/20
 */
@Configuration
@SuppressWarnings("all")
public class DateConfig {
    /**
     * 配置数据源0，数据源的名称最好要有一定的规则，方便配置分库的计算规则
     * @return
     */

    @Bean(name="dataSource0")
    @ConfigurationProperties(prefix = "spring.datasource.test1")
    public DataSource dataSource0(){
        return DataSourceBuilder.create().build();
    }
    /**
     * 配置数据源1，数据源的名称最好要有一定的规则，方便配置分库的计算规则
     * @return
     */
    @Bean(name="dataSource1")
    @ConfigurationProperties(prefix = "spring.datasource.test2")
    public DataSource dataSource1(){
        return DataSourceBuilder.create().build();
    }

}
