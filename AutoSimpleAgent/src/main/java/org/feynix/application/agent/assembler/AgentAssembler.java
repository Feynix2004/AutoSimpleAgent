package org.feynix.application.agent.assembler;

import org.feynix.domain.agent.model.*;
import org.feynix.interfaces.dto.agent.CreateAgentRequest;
import org.feynix.interfaces.dto.agent.PublishAgentVersionRequest;
import org.feynix.interfaces.dto.agent.UpdateAgentRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Agent领域对象组装器
 * 负责DTO、Entity和Request之间的转换
 */
public class AgentAssembler {
    /**
     * 将CreateAgentRequest转换为AgentEntity
     */
    public static AgentEntity toEntity(CreateAgentRequest request, String userId) {
        AgentEntity entity = new AgentEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setAvatar(request.getAvatar());
        entity.setSystemPrompt(request.getSystemPrompt());
        entity.setWelcomeMessage(request.getWelcomeMessage());

        // 设置Agent类型，默认为聊天助手类型
        AgentType agentType = request.getAgentType();
        entity.setAgentType(agentType.getCode());
        entity.setUserId(userId);

        // 设置初始状态为启用
        entity.setEnabled(true);

        // 处理模型配置
        if (request.getModelConfig() != null) {
            entity.setModelConfig(request.getModelConfig());
        } else {
            entity.setModelConfig(ModelConfig.createDefault());
        }

        // 设置工具和知识库ID
        entity.setTools(request.getTools() != null ? request.getTools() : new ArrayList<>());
        entity.setKnowledgeBaseIds(request.getKnowledgeBaseIds() != null ? request.getKnowledgeBaseIds() : new ArrayList<>());

        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return entity;
    }



    /**
     * 将UpdateAgentRequest转换为AgentEntity
     */
    public static AgentEntity toEntity(UpdateAgentRequest request, String userId) {
        AgentEntity entity = new AgentEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setAvatar(request.getAvatar());
        entity.setSystemPrompt(request.getSystemPrompt());
        entity.setWelcomeMessage(request.getWelcomeMessage());
        entity.setModelConfig(request.getModelConfig());
        entity.setTools(request.getTools());
        entity.setKnowledgeBaseIds(request.getKnowledgeBaseIds());
        entity.setUserId(userId);

        return entity;
    }


    /**
     * 创建AgentVersionEntity
     */
    public static AgentVersionEntity createVersionEntity(AgentEntity agent, PublishAgentVersionRequest request) {
        return AgentVersionEntity.createFromAgent(agent, request.getVersionNumber(), request.getChangeLog());
    }

    /**
     * 将AgentEntity转换为AgentDTO
     */
    public static AgentDTO toDTO(AgentEntity entity) {
        if (entity == null) {
            return null;
        }

        AgentDTO dto = new AgentDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAvatar(entity.getAvatar());
        dto.setDescription(entity.getDescription());
        dto.setSystemPrompt(entity.getSystemPrompt());
        dto.setWelcomeMessage(entity.getWelcomeMessage());
        dto.setModelConfig(entity.getModelConfig());
        dto.setTools(entity.getTools());
        dto.setKnowledgeBaseIds(entity.getKnowledgeBaseIds());
        dto.setPublishedVersion(entity.getPublishedVersion());
        dto.setEnabled(entity.getEnabled());
        dto.setAgentType(entity.getAgentType());
        dto.setUserId(entity.getUserId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    /**
     * 将AgentVersionEntity转换为AgentVersionDTO
     */
    public static AgentVersionDTO toDTO(AgentVersionEntity entity) {
        if (entity == null) {
            return null;
        }

        AgentVersionDTO dto = new AgentVersionDTO();
        dto.setId(entity.getId());
        dto.setAgentId(entity.getAgentId());
        dto.setVersionNumber(entity.getVersionNumber());
        dto.setSystemPrompt(entity.getSystemPrompt());
        dto.setWelcomeMessage(entity.getWelcomeMessage());
        dto.setModelConfig(entity.getModelConfig());
        dto.setTools(entity.getTools());
        dto.setKnowledgeBaseIds(entity.getKnowledgeBaseIds());
        dto.setChangeLog(entity.getChangeLog());
        dto.setAgentType(entity.getAgentType());
        dto.setPublishedAt(entity.getPublishedAt());

        return dto;
    }

    /**
     * 将AgentEntity列表转换为AgentDTO列表
     */
    public static List<AgentDTO> toDTOList(List<AgentEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        List<AgentDTO> dtoList = new ArrayList<>(entities.size());
        for (AgentEntity entity : entities) {
            dtoList.add(toDTO(entity));
        }

        return dtoList;
    }

    /**
     * 将AgentVersionEntity列表转换为AgentVersionDTO列表
     */
    public static List<AgentVersionDTO> toVersionDTOList(List<AgentVersionEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        List<AgentVersionDTO> dtoList = new ArrayList<>(entities.size());
        for (AgentVersionEntity entity : entities) {
            dtoList.add(toDTO(entity));
        }

        return dtoList;
    }
}
