package run.xuyang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author XuYang
 * @date 2020/6/11 20:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "sdes_article", shards = 5, replicas = 1)
public class Item implements Serializable {

    @Id
    Long id;

    /**
     * 标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    String title;

    /**
     * 分类
     */
    @Field(type = FieldType.Keyword)
    String category;

    /**
     * 品牌
     */
    @Field(type = FieldType.Keyword)
    String brand;

    /**
     * 价格
     */
    @Field(type = FieldType.Double)
    Double price;

    /**
     * 图片地址
     * 不会对图片地址查询,指定为false
     */
    @Field(type = FieldType.Keyword, index = false)
    String images;
}
