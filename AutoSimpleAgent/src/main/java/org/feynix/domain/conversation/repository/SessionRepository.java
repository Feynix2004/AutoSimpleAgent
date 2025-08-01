package org.feynix.domain.conversation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.feynix.domain.conversation.model.SessionEntity;

@Mapper
public interface SessionRepository extends BaseMapper<SessionEntity> {
}
