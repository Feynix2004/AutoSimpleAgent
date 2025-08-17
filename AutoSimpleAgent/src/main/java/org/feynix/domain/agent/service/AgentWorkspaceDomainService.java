package org.feynix.domain.agent.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

    public List<AgentEntity> getWorkspaceAgents(String userId) {

        LambdaQueryWrapper<AgentWorkspaceEntity> wrapper = Wrappers.<AgentWorkspaceEntity>lambdaQuery()
                .eq(AgentWorkspaceEntity::getUserId, userId).select(AgentWorkspaceEntity::getAgentId);

        List<String> agentIds = agentWorkspaceRepository.selectList(wrapper).stream()
                .map(AgentWorkspaceEntity::getAgentId).collect(Collectors.toList());

        if (agentIds.isEmpty()) {
            return Collections.emptyList();
        }
        return agentRepository.selectByIds(agentIds);

    }

    public boolean deleteAgent(String agentId, String userId) {
        return agentWorkspaceRepository.delete(Wrappers.<AgentWorkspaceEntity>lambdaQuery()
                .eq(AgentWorkspaceEntity::getAgentId, agentId).eq(AgentWorkspaceEntity::getUserId, userId)) > 0;
    }

    public boolean exist(String agentId, String userId) {
        Wrapper<AgentWorkspaceEntity> wrapper = Wrappers.<AgentWorkspaceEntity>lambdaQuery()
                .eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId);

        Long l = agentWorkspaceRepository.selectCount(wrapper);
        return  l > 0;
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

    public void save(AgentWorkspaceEntity workspace) {
        boolean b = agentWorkspaceRepository.insertOrUpdate(workspace);
        if (!b){
            throw new BusinessException("保存失败");
        }
    }

    public AgentWorkspaceEntity findWorkspace(String agentId, String userId) {
        Wrapper<AgentWorkspaceEntity> wrapper = Wrappers.<AgentWorkspaceEntity>lambdaQuery()
                .eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId);
        return agentWorkspaceRepository.selectOne(wrapper);
    }

    public void update(AgentWorkspaceEntity workspace) {
        LambdaUpdateWrapper<AgentWorkspaceEntity> wrapper = Wrappers.<AgentWorkspaceEntity>lambdaUpdate()
                .eq(AgentWorkspaceEntity::getAgentId, workspace.getAgentId())
                .eq(AgentWorkspaceEntity::getAgentId, workspace.getAgentId());
        agentWorkspaceRepository.checkedUpdate(workspace,wrapper);
    }
}
