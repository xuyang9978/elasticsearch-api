package run.xuyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
public class SpringdataElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringdataElasticsearchApplication.class, args);
    }

}
