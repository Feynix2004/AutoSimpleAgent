package org.feynix.domain.agent.service;

import org.feynix.domain.agent.model.AgentDTO;
import org.feynix.domain.agent.model.AgentEntity;
import org.feynix.domain.agent.model.AgentVersionDTO;
import org.feynix.domain.agent.model.AgentVersionEntity;
import org.feynix.interfaces.api.dto.SearchAgentsRequest;

import java.util.List;

/**
 * Agent服务接口
 */

public interface AgentService {
    /**
     * 创建新Agent
     *
     * @param entity Agent实体对象
     * @return 创建的Agent信息
     */
    AgentDTO createAgent(AgentEntity entity);

    /**
     * 获取单个Agent信息
     *
     * @param agentId Agent ID，不能为空
     * @return Agent信息
     */
    AgentDTO getAgent(String agentId, String userId);

    /**
     * 获取用户的Agent列表，支持状态和名称过滤
     *
     * @param userId              用户ID，不能为空
     * @param searchAgentsRequest 查询条件
     * @return 符合条件的Agent列表
     */
    List<AgentDTO> getUserAgents(String userId, SearchAgentsRequest searchAgentsRequest);

    /**
     * 获取已上架的Agent列表，支持名称搜索
     * 当name为空时返回所有已上架Agent
     */
    List<AgentVersionDTO> getPublishedAgentsByName(SearchAgentsRequest searchAgentsRequest);

    /**
     * 更新Agent信息
     *
     * @param agentId Agent ID，不能为空
     * @param entity  更新的Agent实体对象
     * @return 更新后的Agent信息
     */
    AgentDTO updateAgent(String agentId, AgentEntity entity);

    /**
     * 切换Agent的启用/禁用状态
     *
     * @param agentId Agent ID，不能为空
     * @return 更新后的Agent信息
     */
    AgentDTO toggleAgentStatus(String agentId);

    /**
     * 删除Agent
     *
     * @param agentId Agent ID，不能为空
     * @param userId  用户ID，不能为空
     */
    void deleteAgent(String agentId, String userId);

    /**
     * 获取Agent的最新版本
     *
     * @param agentId Agent ID，不能为空
     * @return 最新版本信息
     */
    AgentVersionDTO getLatestAgentVersion(String agentId);

    /**
     * 发布Agent版本
     *
     * @param agentId       Agent ID，不能为空
     * @param versionEntity 版本实体对象
     * @return 发布的版本信息
     */
    AgentVersionDTO publishAgentVersion(String agentId, AgentVersionEntity versionEntity);

    /**
     * 获取Agent的所有版本
     *
     * @param agentId Agent ID，不能为空
     * @param userId  用户ID，不能为空
     * @return 版本列表
     */
    List<AgentVersionDTO> getAgentVersions(String agentId, String userId);

    /**
     * 获取Agent的特定版本
     *
     * @param agentId       Agent ID，不能为空
     * @param versionNumber 版本号，不能为空
     * @return 版本信息
     */
    AgentVersionDTO getAgentVersion(String agentId, String versionNumber);
}
