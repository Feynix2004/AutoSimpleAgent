package org.feynix.application.conversation.service.handler;

import dev.langchain4j.data.message.AiMessage;


public interface Agent {
    AiMessage chat(String prompt);

}
