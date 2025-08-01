package org.feynix.domain.conversation.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.time.LocalDateTime;

/**
 * 上下文实体类，管理会话的上下文窗口
 */
@TableName("context")
public class ContextEntity extends Model<ContextEntity> {
    /**
     * 上下文唯一ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 所属会话ID
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 活跃消息ID列表，JSON数组
     */
    @TableField(value = "active_messages", typeHandler = org.feynix.infrastructure.typehandler.JsonTypeHandler.class)
    private String activeMessages;

    /**
     * 历史消息摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 最后更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 无参构造函数
     */
    public ContextEntity() {
    }

    /**
     * 全参构造函数
     */
    public ContextEntity(String id, String sessionId, String activeMessages,
                         String summary, LocalDateTime updatedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.activeMessages = activeMessages;
        this.summary = summary;
        this.updatedAt = updatedAt;
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getActiveMessages() {
        return activeMessages;
    }

    public void setActiveMessages(String activeMessages) {
        this.activeMessages = activeMessages;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
