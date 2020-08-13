package plus.gifu.data.autoconfigure.leaf;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import plus.gifu.data.leaf.segment.generater.IdGenerator;
import plus.gifu.data.leaf.segment.generater.IdGeneratorImpl;

import javax.sql.DataSource;

/**
 * Leaf自动配置
 *
 * @author yanghongyu
 * @since 2020-07-29
 */
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(IdGenerator.class)
public class LeafAutoConfiguration {

    @Bean
    public IdGenerator idGenerator(DataSource dataSource) {
        return new IdGeneratorImpl(dataSource);
    }

}
