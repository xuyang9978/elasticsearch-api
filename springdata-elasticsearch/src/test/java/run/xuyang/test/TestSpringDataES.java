package run.xuyang.test;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.query.QuerySearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.mapping.Alias;
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
     * 测试自定义方法
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

    }

}
