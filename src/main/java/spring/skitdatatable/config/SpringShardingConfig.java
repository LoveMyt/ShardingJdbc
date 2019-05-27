package spring.skitdatatable.config;

import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSource;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangfeng
 * @Description: todo
 * @Date: created in 17:38 2019/5/20
 */
@Configuration
@SuppressWarnings("all")
public class SpringShardingConfig {
    /**
     * @Author: yangfeng
     * @Description:配置sqlSessionFactory
     * @jdk: 1.8
     * @Date: 16:53 2019/5/27
     */
    @Bean
    SqlSessionFactoryBean sqlSessionFactory (ShardingDataSource shardingDataSource){
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(shardingDataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            sessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mapper/autoMapper/*Mapper.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessionFactoryBean;
    }
    /**
     * 配置数据源规则，即将多个数据源交给sharding-jdbc管理，并且可以设置默认的数据源，
     * 当表没有配置分库规则时会使用默认的数据源
     * @param dataSource0
     * @param dataSource1
     * @return
     */
    @Bean
    public DataSourceRule dataSourceRule(@Qualifier("dataSource0") DataSource dataSource0,
                                         @Qualifier("dataSource1") DataSource dataSource1){
        Map<String, DataSource> dataSourceMap = new HashMap<>(); //设置分库映射
        dataSourceMap.put("dataSource0", dataSource0);
        dataSourceMap.put("dataSource1", dataSource1);
        return new DataSourceRule(dataSourceMap); //设置默认库，两个库以上时必须设置默认库。默认库的数据源名称必须是dataSourceMap的key之一
    }

    /**
     * @Author: yangfeng
     * @Description:对t_user表的配置，进行分库配置，逻辑表名为t_user，每个库有实际的三张表
     * @jdk: 1.8
     * @Date: 15:36 2019/5/22
     */
    @Bean("userTableRule")
    public TableRule userTableRule(DataSourceRule dataSourceRule,@Qualifier("userDatabaseShardingStrategy")DatabaseShardingStrategy userDatabaseShardingStrategy,
                                   @Qualifier("userTableShardingStrategy")TableShardingStrategy userTableShardingStrategy){
        List<String> list =new ArrayList<>();
        list.add("t_user_00");
        list.add("t_user_01");
        list.add("t_user_02");
        TableRule tableRule = new TableRule("t_user",list,dataSourceRule,userDatabaseShardingStrategy,userTableShardingStrategy);
        return tableRule;
    }

    /**
     * @Author: yangfeng
     * @Description: t_user分库策略
     * @jdk: 1.8
     * @Date: 15:30 2019/5/22
     */
    @Bean(name = "userDatabaseShardingStrategy")
    public DatabaseShardingStrategy userDatabaseShardingStrategy(){
       return new DatabaseShardingStrategy("user_id",new UserSingleKeyDatabaseShardingAlgorithm());
    }
    /**
     * @Author: yangfeng
     * @Description: t_user分表策略
     * @jdk: 1.8
     * @Date: 15:30 2019/5/22
     */
    @Bean("userTableShardingStrategy")
    public TableShardingStrategy userTableShardingStrategy(){
        return new TableShardingStrategy("user_id",new UserSingleKeyTableShardingAlgorithm());
    }


    /**
     * @Author: yangfeng
     * @Description: 对t_student表的配置，进行分库配置，逻辑表名为t_student，每个库有实际的三张表
     * @jdk: 1.8
     * @Date: 15:30 2019/5/22
     */
    @Bean("studentTableRule")
    public TableRule studentTableRule(DataSourceRule dataSourceRule,@Qualifier("studentDatabaseShardingStrategy")DatabaseShardingStrategy studentDatabaseShardingStrategy,
                                      @Qualifier("studentTableShardingStrategy")TableShardingStrategy studentTableShardingStrategy){
        List<String> list =new ArrayList<>();
        list.add("t_student_00");
        list.add("t_student_01");
        TableRule tableRule = new TableRule("t_student",list,dataSourceRule,studentDatabaseShardingStrategy,studentTableShardingStrategy);
        return tableRule;
    }
    /**
     * @Author: yangfeng
     * @Description: t_student分库策略
     * @jdk: 1.8
     * @Date: 15:30 2019/5/22
     */
    @Bean("studentDatabaseShardingStrategy")
    public DatabaseShardingStrategy studentDatabaseShardingStrategy(){
        return new DatabaseShardingStrategy("student_id",new StudentSingleKeyDatabaseShardingAlgorithm());
    }
    /**
     * @Author: yangfeng
     * @Description: t_student分表策略
     * @jdk: 1.8
     * @Date: 15:30 2019/5/22
     */
    @Bean("studentTableShardingStrategy")
    public TableShardingStrategy studentTableShardingStrategy(){
        return new TableShardingStrategy("student_id",new StudentSingleKeyTableShardingAlgorithm());
    }

    /**
     * @Author: yangfeng
     * @Description: 构成分库分表的规则 传入数据源集合和每个表的分库分表的具体规则
     * @jdk: 1.8
     * @Date: 15:40 2019/5/22
     */
    @Bean
    public ShardingRule shardingRule(DataSourceRule dataSourceRule,@Qualifier("studentTableRule") TableRule studentTableRule,@Qualifier("userTableRule")TableRule userTableRule){
        List<TableRule>list =new ArrayList();
        list.add(studentTableRule);
        list.add(userTableRule);
        ShardingRule shardingRule = new ShardingRule(dataSourceRule,list);
        return shardingRule;
    }


    /**
     * @Author: yangfeng
     * @Description: 对datasource进行封装
     * @jdk: 1.8
     * @Date: 15:41 2019/5/22
     */
    @Bean(name="dataSource")
    public DataSource shardingDataSource (ShardingRule shardingRule){
        return new ShardingDataSource(shardingRule);
    }

}
