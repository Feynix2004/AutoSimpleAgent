package org.feynix.domain.conversation.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.feynix.domain.conversation.model.MessageEntity;
import org.feynix.domain.conversation.repository.ContextRepository;
import org.feynix.domain.conversation.repository.MessageRepository;
import org.feynix.domain.llm.service.LLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 对话服务实现
 */
@Service
public class ConversationDomainService {
    private final Logger logger = LoggerFactory.getLogger(ConversationDomainService.class);
    private final MessageRepository messageRepository;
    private final ContextRepository contextRepository;
    private final SessionDomainService sessionDomainService;

    @Resource
    private LLMService defaultLlmService;

    @Resource
    private Map<String, LLMService> llmServiceMap;


    public ConversationDomainService(MessageRepository messageRepository,
                                     ContextRepository contextRepository,
                                     SessionDomainService sessionDomainService) {
        this.messageRepository = messageRepository;
        this.contextRepository = contextRepository;
        this.sessionDomainService = sessionDomainService;
    }

    /**
     * 删除会话下的消息
     *
     * @param sessionId 会话id
     */
    public void deleteConversationMessages(String sessionId) {
        messageRepository.delete(Wrappers.<MessageEntity>lambdaQuery().eq(MessageEntity::getSessionId, sessionId));
    }

    public void deleteConversationMessages(List<String> sessionIds) {
        messageRepository.delete(Wrappers.<MessageEntity>lambdaQuery().in(MessageEntity::getSessionId, sessionIds));
    }
}
