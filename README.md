# ShardingJdbc
之前看到网上有篇关于shardingjdbc的demo，就简单用springboot重写了一遍。经过测试 没有问题。</br>
参考文章：</br>
https://blog.csdn.net/l1028386804/article/details/79368021

注意：插入的时候不能用mybatis反向编译的工具的insert 语句插入，会报错,无法获取selectkey。</br>
建议：分库分表的id最好自己写一个全局的分布式唯一主键生成策略。</br>

### 1、测试新增 ：测试插入100条数据，根据分库分表的原则 插入到两个库不同的表中。</br>
  ```java
  @Test
    public void contextLoads() {
        for (int i = 1; i <100; i ++ ){
            User u = new User();
            u.setUserId(i);
            u.setAge(25);
            u.setName("github");
            userMapper.insert(u);
        }
    }
  ```
                        
### 2、测试查询功能：经过查询遍历插入的100条数据，可以按照user_id  倒叙排序。说明shardingjdbc会将各个不同库不同表中的数据聚合起来查询。</br>
```java
@Test
    public void test (){
        UserExample userExample = new UserExample();
        userExample.setOrderByClause("user_id desc");
        List<User> users = userMapper.selectByExample(userExample);
        if (users == null){
            System.out.println("没有查到数据");
            return;
        }
        users.forEach(item->{
            System.out.println(item.toString());
        });
    }
```
  
