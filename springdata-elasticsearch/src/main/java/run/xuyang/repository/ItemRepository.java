package run.xuyang.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import run.xuyang.pojo.Item;

import java.util.List;

/**
 * 继承了ElasticsearchRepository接口不需要写@Component这些注解了
 * 该接口已经实现了部分基本操作索引的方法，可以直接用
 * 自己也可以定义方法，不需要实现，只要命名符合规范即可，spring-data-es会
 * 自动实现这些方法
 *
 * @author XuYang
 * @date 2020/6/11 20:57
 */
public interface ItemRepository extends ElasticsearchRepository<Item, Long> {

    /**
     * 根据title查询，不需要自己实现
     *
     * @param title
     * @return
     */
    List<Item> findByTitle(String title);

    /**
     * 按照价格区间查询，一样不需要自己实现
     *
     * @param low   最低价格
     * @param right 最高价格
     * @return 价格满足该区间的所有Item
     */
    List<Item> findByPriceBetween(Double low, Double right);

    /**
     * 也可以自定义查询，在@Query中写出你的查询json,占位符?0与?1是方法的参数
     * 调用方式与上面一样,但是手写json还是非常麻烦的
     * match查询并设置operator，作用和findByTitle一样
     *
     * @param title 标题
     * @param operator
     * @return
     */
    @Query("{\"match\": {\"title\":{ \"query\": \"?0\",\"operator\":\"?1\"}}}")
    List<Item> findByTitleOperator(String title, String operator);


}
