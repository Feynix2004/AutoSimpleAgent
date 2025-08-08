package org.feynix.domain.agent.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.feynix.application.agent.assembler.AgentAssembler;
import org.feynix.application.agent.dto.AgentDTO;
import org.feynix.domain.agent.model.AgentEntity;
import org.feynix.domain.agent.model.AgentWorkspaceEntity;
import org.feynix.domain.agent.repository.AgentRepository;
import org.feynix.domain.agent.repository.AgentWorkspaceRepository;
import org.feynix.infrastructure.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentWorkspaceDomainService {

    private final AgentWorkspaceRepository agentWorkspaceRepository;

    private final AgentRepository agentRepository;

    public AgentWorkspaceDomainService(AgentWorkspaceRepository agentWorkspaceRepository,
                                       AgentDomainService agentServiceDomainService, AgentRepository agentRepository) {
        this.agentWorkspaceRepository = agentWorkspaceRepository;
        this.agentRepository = agentRepository;
    }

    public List<AgentDTO> getWorkspaceAgents(String userId) {

        LambdaQueryWrapper<AgentWorkspaceEntity> wrapper = Wrappers.<AgentWorkspaceEntity>lambdaQuery()
                .eq(AgentWorkspaceEntity::getUserId, userId).select(AgentWorkspaceEntity::getAgentId);

        List<String> agentIds = agentWorkspaceRepository.selectList(wrapper).stream()
                .map(AgentWorkspaceEntity::getAgentId).collect(Collectors.toList());

        if (agentIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<AgentEntity> agents = agentRepository.selectBatchIds(agentIds);
        return agents.stream().map(AgentAssembler::toDTO).collect(Collectors.toList());

    }

    public boolean deleteAgent(String agentId, String userId) {
        return agentWorkspaceRepository.delete(Wrappers.<AgentWorkspaceEntity>lambdaQuery()
                .eq(AgentWorkspaceEntity::getAgentId, agentId).eq(AgentWorkspaceEntity::getUserId, userId)) > 0;
    }

    public boolean checkAgentWorkspaceExist(String agentId, String userId) {
        return agentWorkspaceRepository.checkAgentWorkspaceExist(agentId,userId);
    }

    public AgentWorkspaceEntity getWorkspace(String agentId, String userId) {
        Wrapper<AgentWorkspaceEntity> wrapper = Wrappers.<AgentWorkspaceEntity>lambdaQuery()
                .eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId);
        AgentWorkspaceEntity agentWorkspaceEntity = agentWorkspaceRepository.selectOne(wrapper);
        if (agentWorkspaceEntity ==null){
            throw new BusinessException("助理不存在");
        }
        return agentWorkspaceEntity;
    }
}
