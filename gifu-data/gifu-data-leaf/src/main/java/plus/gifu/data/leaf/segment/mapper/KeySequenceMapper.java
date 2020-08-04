package plus.gifu.data.leaf.segment.mapper;

import org.apache.ibatis.annotations.*;
import plus.gifu.data.leaf.segment.model.KeySequence;

public interface KeySequenceMapper {

    @Select("SELECT c_key, c_sequence_id, c_step, c_version, c_create_timestamp, c_update_timestamp, c_delete_flag " +
            "FROM t_key_sequence WHERE c_key = #{key}")
    @Results(value = {
            @Result(column = "c_key", property = "key"),
            @Result(column = "c_sequence_id", property = "sequenceId"),
            @Result(column = "c_step", property = "step"),
            @Result(column = "c_version", property = "version"),
            @Result(column = "c_create_timestamp", property = "createTimestamp"),
            @Result(column = "c_update_timestamp", property = "updateTimestamp"),
            @Result(column = "c_delete_flag", property = "deleteFlag")
    })
    KeySequence get(@Param("key") String key);

    @Insert("INSERT INTO t_key_sequence (c_key, c_sequence_id, c_step, c_version, c_create_timestamp, c_update_timestamp, c_delete_flag) " +
            "VALUES (#{key}, #{sequenceId}, #{step}, #{version}, #{createTimestamp}, #{updateTimestamp}, #{deleteFlag})")
    void insert(@Param("keySequence") KeySequence keySequence);

}