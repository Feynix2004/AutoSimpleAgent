package org.feynix.application.conversation.service.message;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import org.feynix.application.conversation.service.handler.content.ChatContext;
import org.feynix.application.conversation.service.message.agent.workflow.AgentWorkflowContext;
import org.feynix.domain.conversation.constant.MessageType;
import org.feynix.domain.conversation.constant.Role;
import org.feynix.domain.conversation.model.MessageEntity;
import org.feynix.domain.conversation.service.ContextDomainService;
import org.feynix.domain.conversation.service.ConversationDomainService;
import org.feynix.infrastructure.llm.LLMServiceFactory;
import org.feynix.infrastructure.transport.MessageTransport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractMessageHandler {

    /**
     * 连接超时时间（毫秒）
     */
    protected static final long CONNECTION_TIMEOUT = 3000000L;

    /**
     * 摘要前缀信息
     */
    protected static final String SUMMARY_PREFIX = "以下是用户历史消息的摘要，请仅作为参考，用户没有提起则不要回答摘要中的内容：\\n";


    protected final ConversationDomainService conversationDomainService;
    protected final ContextDomainService contextDomainService;
    protected final LLMServiceFactory llmServiceFactory;


    public AbstractMessageHandler(
            ConversationDomainService conversationDomainService,
            ContextDomainService contextDomainService,
            LLMServiceFactory llmServiceFactory) {
        this.conversationDomainService = conversationDomainService;
        this.contextDomainService = contextDomainService;
        this.llmServiceFactory = llmServiceFactory;
    }

    /**
     * 处理对话
     *
     * @param environment 对话环境
     * @param transport 消息传输实现
     * @return 连接对象
     * @param <T> 连接类型
     */
    public abstract <T> T chat(ChatContext environment, MessageTransport<T> transport);


    /**
     * 创建用户消息实体
     */
    protected MessageEntity createUserMessage(ChatContext environment) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setRole(Role.USER);
        messageEntity.setContent(environment.getUserMessage());
        messageEntity.setSessionId(environment.getSessionId());
        return messageEntity;
    }

    /**
     * 创建LLM消息实体
     */
    protected MessageEntity createLlmMessage(ChatContext environment) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setRole(Role.ASSISTANT);
        messageEntity.setSessionId(environment.getSessionId());
        messageEntity.setModel(environment.getModel().getModelId());
        messageEntity.setProvider(environment.getProvider().getId());
        return messageEntity;
    }



    protected void saveMessages(ChatContext chatContext,MessageEntity userMessageEntity,MessageEntity llmMessageEntity){
        // 保存消息
        conversationDomainService.insertBathMessage(Arrays.asList(userMessageEntity, llmMessageEntity));

        // 更新上下文
        List<String> activeMessages = chatContext.getContextEntity().getActiveMessages();
        activeMessages.add(userMessageEntity.getId());
        activeMessages.add(llmMessageEntity.getId());
        contextDomainService.insertOrUpdate(chatContext.getContextEntity());
    }
}
