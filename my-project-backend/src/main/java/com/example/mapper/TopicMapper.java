package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
//    @Select("""
//            select * from db_topic left join db_account on uid=db_account.id order by `time` desc limit ${start},10
//            """)
//    List<Topic>topicList(int start);
//
//    @Select("""
//            select * from db_topic left join db_account on uid=db_account.id
//            where type=#{type} order by `time` desc limit ${start},10
//            """)
//    List<Topic>topicListByType(int start,int type);

    @Select("""
            select count(*) from db_topic_interact_${type} where tid=#{tid}
            """)
    int interactCount(int tid,String type);
    @Select("""
            select count(*) from db_topic_interact_${type} where tid=#{tid} and uid=#{uid}
            """)
    int userInteractCount(int tid,int uid,String type);
    @Insert("""
            <script>
            insert ignore into db_topic_interact_${type} values
            <foreach collection="interacts" item="item" separator =",">
            (#{item.tid},#{item.uid},#{item.time})
            </foreach>
            </script>
            """)
    void addInteract(List<Interact>interacts,String type);
    @Delete("""
            <script>
            delete from db_topic_interact_${type} where
            <foreach collection="interacts" item="item" separator =",">
            (tid=#{item.tid} and uid=#{item.uid})
            </foreach>
            </script>
            """)
    void deleteInteract(List<Interact>interacts,String type);
    @Select("""
            select * from db_topic_interact_collect left join db_topic on tid=db_topic.id
            where db_topic_interact_collect.uid= #{uid}
            """)
    List<Topic>collectTopics(int uid);

}
