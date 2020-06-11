package run.xuyang.config;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * java使用elasticsearch的客户端配置类
 * Configuration注解就相当于spring项目中的xml配置文件
 * 其中的Bean注解就是xml中配置的bean对象注入
 *
 * @author XuYang
 * @date 2020/6/10 10:33
 */
@Configuration
public class ElasticSearchClientConfig {

    /**
     * 这个bean注解就相当于
     * spring项目中的<bean id="restHighLevelClient"
     * class="org.elasticsearch.client.RestHighLevelClient"></>
     *
     * @return elasticsearch客户端对象
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
        return client;
    }
}
