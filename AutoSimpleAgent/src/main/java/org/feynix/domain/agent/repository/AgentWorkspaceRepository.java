package org.feynix.domain.agent.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.feynix.domain.agent.model.AgentWorkspaceEntity;

/**
 * Agent工作区仓库接口
 */
@Mapper
public interface AgentWorkspaceRepository extends BaseMapper<AgentWorkspaceEntity> {
    boolean checkAgentWorkspaceExist(String agentId, String userId);
}
