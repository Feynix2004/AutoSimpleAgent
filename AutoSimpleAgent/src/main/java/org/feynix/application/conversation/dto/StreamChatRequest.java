package org.feynix.application.conversation.dto;

public class StreamChatRequest extends ChatRequest{
    /**
     * 是否启用流式响应
     */
    private boolean stream = true;

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }
}
