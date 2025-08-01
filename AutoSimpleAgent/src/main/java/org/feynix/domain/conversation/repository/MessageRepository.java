package org.feynix.domain.conversation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.feynix.domain.conversation.model.MessageEntity;

/**
 * 消息仓库接口
 */
@Mapper
public interface MessageRepository extends BaseMapper<MessageEntity> {
}
