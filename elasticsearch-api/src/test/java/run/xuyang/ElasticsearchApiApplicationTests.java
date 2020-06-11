package run.xuyang;

import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import run.xuyang.pojo.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * elasticsearch 7.6.2 高级客户端API测试
 */
@SpringBootTest
class ElasticsearchApiApplicationTests {

    // 这种是最基本的注入,注入的对象实例名字必须和自己定义的bean id相同
    // @Autowired
    // private RestHighLevelClient restHighLevelClient;

    // 如果想用自定义的实例变量名的话需要加上Qualifier或者Resource注解指明bean id
    @Autowired
    @Qualifier("restHighLevelClient")
    // @Resource(name = "restHighLevelClient")
    private RestHighLevelClient client;

    /**
     * 测试创建索引 PUT
     */
    @Test
    public void testCreateIndex() throws IOException {
        // 1. 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("xy_index");
        // 2. 执行创建索引的请求
        CreateIndexResponse response = client.indices()
                .create(request, RequestOptions.DEFAULT);

        System.out.println(response);
    }

    /**
     * 测试获取索引,判断索引是否存在 GET
     */
    @Test
    public void testExistsIndex() throws Exception{
        GetIndexRequest request = new GetIndexRequest("xy_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(exists);
    }

    /**
     * 测试删除索引
     */
    @Test
    public void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("xy_index");

        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);

        System.out.println(response.isAcknowledged());
    }


    /**
     * 测试添加文档
     */
    @Test
    public void testAddDocument() throws IOException {
        // 创建对象
        User user = new User("徐杨说", 3);
        // 创建请求
        IndexRequest indexRequest = new IndexRequest("xy_index");
        // 规则 PUT /xy_index/_doc/1
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.timeout("1s");
        // 将我们的数据放入请求 json格式
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        // 客户端发送请求,获取相应的结果
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());
    }

    /**
     * 测试获取文档 GET /index/doc/1
     */
    @Test
    public void testIsExists() throws IOException {
        GetRequest getRequest = new GetRequest("xy_index", "1");
        // 不获取返回的_source的上下文,这里只是为了演示,实际可以不用写
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);

        System.out.println(exists);
    }

    /**
     * 获取文档信息
     */
    @Test
    public void testGetDocument() throws IOException {
        GetRequest getRequest = new GetRequest("xy_index", "1");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        // 打印文档的内容
        System.out.println(getResponse.getSourceAsString());
        System.out.println(getResponse);
    }

    /**
     * 更新文档信息
     */
    @Test
    public void testUpdateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("xy_index", "1");
        updateRequest.timeout("1s");

        User user = new User("徐杨说java", 18);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

        System.out.println(updateResponse.status());
    }

    /**
     * 删除文档记录
     */
    @Test
    public void testDeleteRequest() throws Exception{
        DeleteRequest deleteRequest = new DeleteRequest("xy_index", "1");
        deleteRequest.timeout("1s");

        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(deleteResponse.status());
    }

    /**
     * 真实的项目一般都会批量插入数据,删除、更新等操作都是一样的，只是换一下方法
     */
    @Test
    public void testBulkRequest() throws Exception {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        List<User> userList = new ArrayList<>();
        userList.add(new User("xuyang1", 3));
        userList.add(new User("xuyang2", 3));
        userList.add(new User("xuyang3", 3));
        userList.add(new User("qinjiang4", 3));
        userList.add(new User("qinjiang45", 3));
        userList.add(new User("qinjiang46", 3));

        for (int i = 0; i < userList.size(); i++) {
            // 批量更新、删除，就在这里修改对应的请求就可以了
            bulkRequest.add(
                    new IndexRequest("xy_index")
                    .id("" + (i+1))   // 不设置id会随机分配id
                    .source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        System.out.println(bulkResponse.hasFailures());

    }

    /**
     * 查询
     * SearchRequest 搜索请求
     * SearchSourceBuilder 构造搜索的条件
     * HighLightBuilder 构造高亮
     * TermQueryBuilder 精确查询
     * MatchQueryBuilder 全部查询
     * ...
     */
    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xy_index");
        // 构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 查询条件，可以使用QueryBuilders工具来实现
        // QueryBuilders.termQuery("name", "qinjiang4")精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("username", "qinjiang4");
        // QueryBuilders.matchAllQuery()匹配所有
        // MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        System.out.println("==========================");
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }

    }

}
