package org.feynix.domain.conversation.handler;

import org.feynix.domain.conversation.service.ContextDomainService;
import org.feynix.domain.conversation.service.ConversationDomainService;
import org.feynix.infrastructure.llm.service.LLMServiceFactory;
import org.feynix.infrastructure.transport.MessageTransport;
import org.springframework.stereotype.Component;

/**
 * React消息处理器
 * 用于支持工具调用的对话模式
 * 目前为预留实现，后续将完善MCP工具调用
 */
@Component(value = "reactMessageHandler")
public class ReactMessageHandler extends StandardMessageHandler {

    public ReactMessageHandler(
            ConversationDomainService conversationDomainService,
            ContextDomainService contextDomainService,
            LLMServiceFactory llmServiceFactory) {
        super(conversationDomainService, contextDomainService, llmServiceFactory);
    }

    @Override
    public <T> T handleChat(ChatEnvironment environment, MessageTransport<T> transport) {
        // 目前直接复用标准处理器的逻辑
        // 后续将在此基础上添加工具调用支持
        return super.handleChat(environment, transport);
    }

    /**
     * 预留的工具调用方法
     * @param toolName 工具名称
     * @param parameters 工具参数
     * @return 工具调用结果
     */
    protected Object invokeExternalTool(String toolName, Object parameters) {
        // 预留接口，未来将实现MCP工具调用
        return null;
    }
}