package org.feynix.application.agent.service;


import org.feynix.domain.agent.model.AgentDTO;
import org.feynix.domain.agent.service.AgentDomainService;
import org.feynix.domain.agent.service.AgentWorkspaceDomainService;
import org.feynix.domain.conversation.service.ConversationDomainService;
import org.feynix.domain.conversation.service.SessionDomainService;
import org.feynix.domain.conversation.dto.SessionDTO;
import org.feynix.infrastructure.exception.BusinessException;
import org.feynix.interfaces.dto.agent.SearchAgentsRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent应用服务，用于适配领域层的Agent服务
 * 职责：
 * 1. 接收和验证来自接口层的请求
 * 2. 将请求转换为领域对象或参数
 * 3. 调用领域服务执行业务逻辑
 * 4. 转换和返回结果给接口层
 */
@Service
public class AgentWorkspaceAppService {
    private final AgentWorkspaceDomainService agentWorkspaceDomainService;

    private final AgentDomainService agentServiceDomainService;

    private final SessionDomainService sessionDomainService;

    private final ConversationDomainService conversationDomainService;

    public AgentWorkspaceAppService(AgentWorkspaceDomainService agentWorkspaceDomainService,
                                    AgentDomainService agentServiceDomainService, SessionDomainService sessionDomainService, ConversationDomainService conversationDomainService) {
        this.agentWorkspaceDomainService = agentWorkspaceDomainService;
        this.agentServiceDomainService = agentServiceDomainService;
        this.sessionDomainService = sessionDomainService;
        this.conversationDomainService = conversationDomainService;
    }


    /**
     * 获取工作区下的助理
     *
     * @param  userId 用户id
     * @return
     */
    public List<AgentDTO> getAgents(String userId) {
        // 1.获取当前用户的所有助理
        List<AgentDTO> userAents = agentServiceDomainService.getUserAgents(userId, new SearchAgentsRequest());

        // 2.获取已添加到工作区的助理
        List<AgentDTO> workspaceAgents = agentWorkspaceDomainService.getWorkspaceAgents(userId);

        // 合并两个列表
        userAents.addAll(workspaceAgents);
        return userAents;
    }

    /**
     * 删除工作区中的助理
     * @param agentId 助理id
     * @param userId 用户id
     */
    public void deleteAgent(String agentId, String userId) {
        boolean deleteAgent = agentWorkspaceDomainService.deleteAgent(agentId, userId);
        if (!deleteAgent){
            throw new BusinessException("删除助理失败");
        }
        // 查出会话列表,收集 sessionIds

        List<String> sessionIds = sessionDomainService.getSessionsByAgentId(agentId).stream().map(SessionDTO::getId).collect(Collectors.toList());
        if (sessionIds.isEmpty()){
            return;
        }
        sessionDomainService.deleteSessions(sessionIds);
        // 删除agent下的会话
        // 删除会话下的所有消息
        conversationDomainService.deleteConversationMessages(sessionIds);
    }
}
