package plus.gifu.data.leaf.segment.generater;

import plus.gifu.data.leaf.segment.dao.KeySequenceDao;
import plus.gifu.data.leaf.segment.model.IdResult;
import plus.gifu.data.leaf.segment.model.KeySequence;

import javax.sql.DataSource;

/**
 * ID 生成器
 *
 * @author yanghongyu
 * @since 2020-08-06
 */
public class IdGeneratorImpl implements IdGenerator {

    private final KeySequenceDao keySequenceDao;

    public IdGeneratorImpl(DataSource dataSource) {
        this.keySequenceDao = new KeySequenceDao(dataSource);
    }

    @Override
    public IdResult generateId(String key) {
        KeySequence keySequence = keySequenceDao.get(key);
        IdResult idResult = new IdResult();
        idResult.setId(keySequence.getSequenceId());
        return idResult;
    }

}
