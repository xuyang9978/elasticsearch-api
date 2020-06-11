package run.xuyang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author XuYang
 * @date 2020/6/10 12:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String username;

    private int age;
}
