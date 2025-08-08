package org.feynix.domain.conversation.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.feynix.application.conversation.assembler.MessageAssembler;

import org.feynix.application.conversation.dto.MessageDTO;
import org.feynix.domain.conversation.model.MessageEntity;
import org.feynix.domain.conversation.repository.ContextRepository;
import org.feynix.domain.conversation.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

/**
 * 对话服务实现
 */
@Service
public class ConversationDomainService {
    private final Logger logger = LoggerFactory.getLogger(ConversationDomainService.class);
    private final MessageRepository messageRepository;
    private final ContextRepository contextRepository;
    private final SessionDomainService sessionDomainService;



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

    /**
     * 获取会话中的消息列表
     *
     * @param sessionId 会话id
     * @return 消息列表
     */
    public List<MessageDTO> getConversationMessages(String sessionId) {
        List<MessageEntity> messageEntities = messageRepository.selectList(
                Wrappers.<MessageEntity>lambdaQuery().eq(MessageEntity::getSessionId, sessionId));
        return messageEntities.stream().map(MessageAssembler::toDTO).collect(Collectors.toList());
    }




    public MessageEntity saveMessage(MessageEntity message){
        messageRepository.insert(message);
        return message;
    }

    public void insertBathMessage(List<MessageEntity> messages){
        messageRepository.insert(messages);
    }

}
