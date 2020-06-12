package run.xuyang.test;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import run.xuyang.pojo.Item;
import run.xuyang.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XuYang
 * @date 2020/6/11 21:03
 */
@SpringBootTest
public class TestSpringDataES {

    /**
     * 注入jpa查询
     */
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 测试批量插入数据
     */
    @Test
    public void testSaveAll() {
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item(1L, "OPPOFindX2", "手机", "OPPO", 4999d, "http://image.leyou.com/13123.jpg"));
        itemList.add(new Item(2L, "OPPOFindX", "手机", "OPPO", 3999d, "http://image.leyou.com/13123.jpg"));
        itemList.add(new Item(3L, "OPPORENO", "手机", "OPPO", 2999d, "http://image.leyou.com/13123.jpg"));
        itemList.add(new Item(4L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        itemList.add(new Item(5L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        itemList.add(new Item(6L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        itemList.add(new Item(7L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        itemList.add(new Item(8L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        itemRepository.saveAll(itemList);
        Iterable<Item> all = itemRepository.findAll();
        all.forEach(System.out::println);
    }

    /**
     * 测试自定义查询方法
     */
    @Test
    public void testSelfMethod() {
        List<Item> item = itemRepository.findByTitleOperator("小米", "OR");
        item.forEach(System.out::println);
        System.out.println("=================================================");
        itemRepository.findByPriceBetween(2000.00, 4000.00).forEach(System.out::println);
        System.out.println("=================================================");
        itemRepository.findByTitle("小米").forEach(System.out::println);
    }

    /**
     * 测试创建索引库
     */
    @Test
    public void testIndex() {
        // 设置索引信息(绑定实体类)，返回IndexOperations
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Item.class);

        // 创建索引库
        // 注意，这里不需要自己使用create方法创建了，上面第一句话执行后就会创建出索引库了，再调用就会抛出异常，提醒索引库已经存在
        // indexOperations.create();

        // 为该IndexOperations绑定到的实体创建索引映射。
        Document mapping = indexOperations.createMapping();
        // 将刚刚通过类创建的映射写入索引
        indexOperations.putMapping(mapping);
    }

    /**
     * 测试QueryBuilders构造复杂查询
     */
    @Test
    public void testQBs() {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "OPPOFindX");
        NativeSearchQuery query = nativeSearchQueryBuilder.withQuery(matchQueryBuilder).build();
        SearchHits<Item> hits = elasticsearchRestTemplate.search(query, Item.class);
        hits.forEach(System.out::println);
    }

    /**
     * 测试删除DocumentOperations文档的删除操作
     */
    @Test
    public void testDelete1() {
        // 删除sdes_article索引库中id为1的文档
        String count = elasticsearchRestTemplate.delete("1", IndexCoordinates.of("sdes_article"));
        System.out.println(count);
    }

    /**
     * 测试重载的删除的方法
     */
    @Test
    public void testDelete2() {
        elasticsearchRestTemplate.delete(
                new NativeSearchQueryBuilder().withQuery(
                        QueryBuilders.matchQuery("title", "OPPOFindX")).build(),
                Item.class,
                IndexCoordinates.of("sdes_article")
        );
        SearchHits<Item> itemSearchHits = elasticsearchRestTemplate.search(
                new NativeSearchQueryBuilder().withQuery(
                        QueryBuilders.matchAllQuery()).build(),
                Item.class
        );
        itemSearchHits.forEach(System.out::println);
    }

    /**
     * 测试新增/更改方法，可以传入集合、单个或者多个实体类对象，返回更新后的对象或者集合
     */
    @Test
    void testSaveRest() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "OPPOFindX2", "手机", "OPPO", 4999d, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(2L, "OPPOFindX", "手机", "OPPO", 3999d, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(3L, "OPPORENO", "手机", "OPPO", 2999d, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(4L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(5L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(6L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(7L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(8L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        Iterable<Item> save = elasticsearchRestTemplate.save(list);
        save.forEach(System.out::println);
    }

    /**
     * SearchOperations查询相关操作
     * 基本查询
     */
    @Test
    public void testSearch1() {
        // 利用构造器建造NativeSearchQuery  也可以添加条件，过滤，等复杂操作
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", "OPPOFindX"))
                .build();
        // elasticsearchRestTemplate.search方法参数一：本机查询的构造，参数二：index的类,可选参数三：再次声明库名(可以多个)
        SearchHits<Item> search = elasticsearchRestTemplate.search(query, Item.class);
        search.forEach(searchHit -> System.out.println(searchHit.getContent()));
    }

    /**
     * 排序分页查询
     * SortBuilders.fieldSort("排序字段").order(SortOrder.ASC/DESC) : 分页后的排序
     * PageRequest.of(当前页,每页条数): 注意起始页为0
     */
    @Test
    void testNativeSearchQueryBuilder2() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("category", "手机"))
                // 添加分页  注意页码是从0开始的
                // pageable的实现类PageRequest的静态方法of
                // 要排序就增加参数3：Sort.Direction.ASC升  Sort.Direction.DESC降
                .withPageable(PageRequest.of(1, 4))
                // 排序整体
                // 根据字段排序fieldSort("字段名").order(SortOrder.ASC/DESC)
                .withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC))
                .build();
        // elasticsearchRestTemplate.search方法参数一,本机查询的构造,参数二index的类,可选参数三再次声明库名(可以多个)
        SearchHits<Item> search = elasticsearchRestTemplate.search(query, Item.class);
        search.forEach(searchHit -> System.out.println(searchHit.getContent()));
    }

    /**
     * 测试聚合查询
     */
    @Test
    void testAgg1() {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        // 聚合可以有多个,所以add
        // terms词条聚合,传入聚合名称   field("聚合字段")   size(结果集大小)
        NativeSearchQuery query = nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"))
                // 结果集过滤  这里设置不需要结果集(不添加包含与不包含,会自动生成length为0数组)
                .withSourceFilter(new FetchSourceFilterBuilder().build())
                .build();
        SearchHits<Item> hits = elasticsearchRestTemplate.search(query, Item.class);
        System.out.println(hits);
        // 获取聚合结果集   因为结果为字符串类型 所以用ParsedStringTerms接收   还有ParsedLongTerms接收数字  ParsedDoubleTerms接收小数
        Aggregations aggregations = hits.getAggregations();
        assert aggregations != null;
        ParsedStringTerms brands = aggregations.get("brands");
        // 获取桶
        brands.getBuckets().forEach(bucket -> {
            // 获取桶中的key   与    记录数
            System.out.println(bucket.getKeyAsString() + " : " + bucket.getDocCount());
        });
    }

    /**
     * 嵌套聚合查询
     * <p>
     * 下面查出各品牌的产品数量后再查出各品牌产品均价:
     * 添加子聚合 subAggregation(添加方式和上面是一样的)
     * 获取自聚合 在父聚合的桶中用getAggregations()获取子聚合
     */
    @Test
    void testAgg2() {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        // 聚合可以有多个,所以add
        // terms词条聚合,传入聚合名称   field("聚合字段")
        NativeSearchQuery query = nativeSearchQueryBuilder
                .addAggregation(
                        AggregationBuilders
                                .terms("brands")
                                .field("brand")
                                // 添加子聚合  subAggregation(添加方式是一样的)  值为桶中品牌均价
                                .subAggregation(AggregationBuilders.avg("price_avg").field("price"))
                )
                // 结果集过滤  这里设置不需要结果集(不添加包含与不包含,会自动生成长为0数组)
                .withSourceFilter(new FetchSourceFilterBuilder().build())
                .build();
        SearchHits<Item> hits = elasticsearchRestTemplate.search(query, Item.class);
        System.out.println(hits);
        // 获取聚合结果集   因为结果为字符串类型 所以用ParsedStringTerms接收   还有ParsedLongTerms接收数字  ParsedDoubleTerms接收小数
        Aggregations aggregations = hits.getAggregations();
        assert aggregations != null;
        ParsedStringTerms brands = aggregations.get("brands");
        // 获取桶brands
        brands.getBuckets().forEach(bucket -> {
            // 获取桶中的key   与    记录数
            System.out.println(bucket.getKeyAsString() + " : " + bucket.getDocCount());
            // 获取嵌套的桶price_avg
            ParsedAvg price_avg = bucket.getAggregations().get("price_avg");
            System.out.println(price_avg.getValue());
        });
    }


}
