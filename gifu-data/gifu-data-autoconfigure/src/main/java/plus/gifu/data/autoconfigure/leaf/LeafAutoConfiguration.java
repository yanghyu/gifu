package plus.gifu.data.autoconfigure.leaf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import plus.gifu.data.leaf.segment.generater.IdGenerator;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Leaf自动配置
 *
 * @author yanghongyu
 * @since 2020-07-29
 */
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(IdGenerator.class)
public class LeafAutoConfiguration {

    @Resource
    private DataSource dataSource;

}
