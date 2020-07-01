package plus.data.leaf.segment.mapper;

import org.apache.ibatis.annotations.Param;
import plus.data.leaf.segment.model.KeySequence;

public interface KeySequenceMapper {

    KeySequence get(@Param("key") String key);

}
