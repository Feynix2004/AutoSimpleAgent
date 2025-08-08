package org.feynix.application.agent.service;

import org.feynix.application.conversation.assembler.SessionAssembler;
import org.feynix.domain.agent.model.AgentEntity;
import org.feynix.domain.agent.service.AgentDomainService;
import org.feynix.domain.agent.service.AgentWorkspaceDomainService;
import org.feynix.domain.conversation.constant.Role;
import org.feynix.application.conversation.dto.SessionDTO;
import org.feynix.domain.conversation.model.MessageEntity;
import org.feynix.domain.conversation.model.SessionEntity;
import org.feynix.domain.conversation.service.ConversationDomainService;
import org.feynix.domain.conversation.service.SessionDomainService;
import org.feynix.infrastructure.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgentSessionAppService {

    private final AgentWorkspaceDomainService agentWorkspaceDomainService;

    private final AgentDomainService agentDomainService;

    private final SessionDomainService sessionDomainService;

    private final ConversationDomainService conversationDomainService;

    public AgentSessionAppService(AgentWorkspaceDomainService agentWorkspaceDomainService,
                                  AgentDomainService agentDomainService,
                                  SessionDomainService sessionDomainService,
                                  ConversationDomainService conversationDomainService) {
        this.agentWorkspaceDomainService = agentWorkspaceDomainService;
        this.agentDomainService = agentDomainService;
        this.sessionDomainService = sessionDomainService;
        this.conversationDomainService = conversationDomainService;
    }

    /**
     * 获取助理下的会话列表
     *
     * @param userId  用户id
     * @param agentId 助理id
     * @return 会话列表
     */
    public List<SessionDTO> getAgentSessionList(String userId, String agentId) {

        // 校验该 agent 是否被添加了工作区，判断条件：是否是自己的助理 or 在工作区中
        boolean b = agentDomainService.checkAgentExist(agentId, userId);
        boolean b1 = agentWorkspaceDomainService.checkAgentWorkspaceExist(agentId, userId);

        if (!b && !b1){
            throw new BusinessException("助理不存在");
        }

        // 获取对应的会话列表
        List<SessionEntity> sessions = sessionDomainService.getSessionsByAgentId(agentId);
        if (sessions.isEmpty()) {
            // 如果会话列表为空，则新创建一个并且返回
            SessionEntity session = sessionDomainService.createSession(agentId, userId);
            sessions.add(session);
        }

        return SessionAssembler.toDTOs(sessions);
    }

    /**
     * 创建会话
     *
     * @param userId  用户id
     * @param agentId 助理id
     * @return 会话
     */
    public SessionDTO createSession(String userId, String agentId) {
        SessionEntity session = sessionDomainService.createSession(agentId, userId);
        AgentEntity agent = agentDomainService.getAgentWithPermissionCheck(agentId, userId);
        String welcomeMessage = agent.getWelcomeMessage();
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setRole(Role.SYSTEM);
        messageEntity.setContent(welcomeMessage);
        messageEntity.setSessionId(session.getId());
        conversationDomainService.saveMessage(messageEntity);
        return SessionAssembler.toDTO(session);
    }

    /**
     * 更新会话
     *
     * @param id     会话id
     * @param userId 用户id
     * @param title  标题
     */
    public void updateSession(String id, String userId, String title) {
        sessionDomainService.updateSession(id, userId, title);
    }

    /**
     * 删除会话
     *
     * @param id 会话id
     */
    @Transactional
    public void deleteSession(String id, String userId) {
        boolean deleteSession = sessionDomainService.deleteSession(id, userId);
        if (!deleteSession){
            throw new BusinessException("删除会话失败");
        }
        // 删除会话下的消息
        conversationDomainService.deleteConversationMessages(id);
    }
}
