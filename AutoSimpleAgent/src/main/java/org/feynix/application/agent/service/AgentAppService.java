package org.feynix.application.agent.service;


import org.feynix.application.agent.assembler.AgentAssembler;
import org.feynix.domain.agent.model.AgentDTO;
import org.feynix.domain.agent.model.AgentEntity;
import org.feynix.domain.agent.model.AgentVersionDTO;
import org.feynix.domain.agent.model.AgentVersionEntity;
import org.feynix.domain.agent.service.AgentService;
import org.feynix.domain.common.exception.ParamValidationException;
import org.feynix.interfaces.api.dto.CreateAgentRequest;
import org.feynix.interfaces.api.dto.PublishAgentVersionRequest;
import org.feynix.interfaces.api.dto.SearchAgentsRequest;
import org.feynix.interfaces.api.dto.UpdateAgentRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Agent应用服务，用于适配领域层的Agent服务
 * 职责：
 * 1. 接收和验证来自接口层的请求
 * 2. 将请求转换为领域对象或参数
 * 3. 调用领域服务执行业务逻辑
 * 4. 转换和返回结果给接口层
 */
@Service
public class AgentAppService {



    private final AgentService agentService;

    public AgentAppService(AgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * 创建新Agent
     */
    public AgentDTO createAgent(CreateAgentRequest request, String userId) {
        // 在应用层验证请求
        request.validate();

        // 使用组装器创建领域实体
        AgentEntity entity = AgentAssembler.toEntity(request,userId);

        entity.setUserId(userId);

        // 调用领域服务
        return agentService.createAgent(entity);
    }

    /**
     * 获取Agent信息
     */
    public AgentDTO getAgent(String agentId, String userId) {
        return agentService.getAgent(agentId, userId);
    }

    /**
     * 获取用户的Agent列表，支持状态和名称过滤
     */
    public List<AgentDTO> getUserAgents(String userId, SearchAgentsRequest searchAgentsRequest) {
        return agentService.getUserAgents(userId, searchAgentsRequest);
    }

    public List<AgentVersionDTO> getPublishedAgentsByName(SearchAgentsRequest searchAgentsRequest) {
        return agentService.getPublishedAgentsByName(searchAgentsRequest);
    }

    /**
     * 更新Agent信息（基本信息和配置合并更新）
     */
    public AgentDTO updateAgent(String agentId, UpdateAgentRequest request, String userId) {
        // 在应用层验证请求
        request.validate();

        // 使用组装器创建更新实体
        AgentEntity updateEntity = AgentAssembler.toEntity(request,userId);

        updateEntity.setUserId(userId);
        // 调用领域服务更新Agent
        return agentService.updateAgent(agentId, updateEntity);
    }

    /**
     * 切换Agent的启用/禁用状态
     */
    public AgentDTO toggleAgentStatus(String agentId) {
        return agentService.toggleAgentStatus(agentId);
    }

    public void deleteAgent(String agentId, String userId) {
        agentService.deleteAgent(agentId, userId);
    }

    /**
     * 发布Agent版本
     */
    public AgentVersionDTO publishAgentVersion(String agentId, PublishAgentVersionRequest request, String userId) {
        // 在应用层验证请求
        request.validate();

        // 获取当前Agent
        AgentDTO currentAgentDTO = agentService.getAgent(agentId,userId);

        // 获取最新版本，检查版本号大小
        AgentVersionDTO latestVersion = agentService.getLatestAgentVersion(agentId);
        if (latestVersion != null) {
            // 检查版本号是否大于上一个版本
            if (!request.isVersionGreaterThan(latestVersion.getVersionNumber())) {
                throw new ParamValidationException("versionNumber",
                        "新版本号(" + request.getVersionNumber() +
                                ")必须大于当前最新版本号(" + latestVersion.getVersionNumber() + ")");
            }
        }

        // 使用组装器创建版本实体
        AgentVersionEntity versionEntity = AgentAssembler.createVersionEntity(currentAgentDTO.toEntity(), request);

        versionEntity.setUserId(userId);
        // 调用领域服务发布版本
        return agentService.publishAgentVersion(agentId, versionEntity);
    }

    /**
     * 获取Agent的所有版本
     */
    public List<AgentVersionDTO> getAgentVersions(String agentId, String userId) {
        return agentService.getAgentVersions(agentId, userId);
    }
    /**
     * 获取Agent的特定版本
     */
    public AgentVersionDTO getAgentVersion(String agentId, String versionNumber) {
        return agentService.getAgentVersion(agentId, versionNumber);
    }

    /**
     * 获取Agent的最新版本
     */
    public AgentVersionDTO getLatestAgentVersion(String agentId) {
        return agentService.getLatestAgentVersion(agentId);
    }
}
