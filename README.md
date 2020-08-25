# 这是什么

java客户端操作**elasticsearch**的两种api

- elasticsearch-api模块：使用RestHighLevel的原生API进行操作，比较繁杂
- springdata-elasticsearch模块：使用SpringData ElasticSearch封装的API进行操作，比较简单，省去了大量的繁杂的代码

# 使用版本

| ElasticSearch | 7.6.2         |
| ------------- | ------------- |
| SpringBoot    | 2.3.0.RELEASE |
| jdk           | 1.8           |

# 参考文档&博客

- 官方文档（es7.x版本弃用了很多方法，改动比较大，强烈建议看看官方文档说明）：[Spring Data Elasticsearch - Reference Documentation](https://docs.spring.io/spring-data/elasticsearch/docs/4.0.1.RELEASE/reference/html/#new-features)
- 对新版本讲解比较详细的博客：[自己挖坑自己填 spring-data-elasticsearch  4.0.0.M4   简单实践](https://blog.csdn.net/lixiang19971019/article/details/105009148)
