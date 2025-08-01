package org.feynix.domain.conversation.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.feynix.application.conversation.assembler.SessionAssembler;
import org.feynix.domain.conversation.dto.SessionDTO;
import org.feynix.domain.conversation.model.SessionEntity;
import org.feynix.domain.conversation.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionDomainService {

    private final SessionRepository sessionRepository;

    public SessionDomainService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * 根据 agentId 获取会话列表
     *
     * @param agentId 助理id
     * @return
     */
    public List<SessionDTO> getSessionsByAgentId(String agentId) {
        List<SessionEntity> sessions = sessionRepository.selectList(Wrappers.<SessionEntity>lambdaQuery()
                .eq(SessionEntity::getAgentId, agentId).orderByDesc(SessionEntity::getCreatedAt));
        return sessions.stream().map(SessionAssembler::toDTO).collect(Collectors.toList());
    }

    public void deleteSessions(List<String> sessionIds) {
        sessionRepository.delete(Wrappers.<SessionEntity>lambdaQuery()
                .in(SessionEntity::getId, sessionIds));
    }
}
