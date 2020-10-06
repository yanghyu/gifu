package com.github.yanghyu.gifu.data.leaf.segment.mapper;

import org.apache.ibatis.annotations.*;
import com.github.yanghyu.gifu.data.leaf.segment.model.Sequence;

public interface SequenceMapper {

    @Select("SELECT c_key, c_max_id, c_version, c_create_timestamp, c_update_timestamp, c_delete_flag " +
            "FROM t_key_sequence WHERE c_key = #{key} AND c_delete_flag = 0")
    @Results(value = {
            @Result(column = "c_key", property = "key"),
            @Result(column = "c_max_id", property = "maxId"),
            @Result(column = "c_version", property = "version"),
            @Result(column = "c_create_timestamp", property = "createTimestamp"),
            @Result(column = "c_update_timestamp", property = "updateTimestamp"),
            @Result(column = "c_delete_flag", property = "deleteFlag")
    })
    Sequence get(@Param("key") String key);

    @Insert("INSERT INTO t_key_sequence " +
            "(c_key, c_max_id, c_version, c_create_timestamp, c_update_timestamp, c_delete_flag) " +
            "VALUES (#{key}, #{maxId}, #{version}, #{createTimestamp}, #{updateTimestamp}, #{deleteFlag})")
    int insert(@Param("keySequence") Sequence sequence);

    @Update("UPDATE t_key_sequence " +
            "SET c_max_id = #{maxId}, c_version = #{version}, c_update_timestamp = #{updateTimestamp} " +
            "WHERE c_key = #{key} AND c_version = #{version} - 1 AND c_delete_flag = 0")
    int updateMaxId(@Param("keySequence") Sequence sequence);

}
