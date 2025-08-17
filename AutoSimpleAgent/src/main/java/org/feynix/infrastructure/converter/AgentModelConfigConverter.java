package org.feynix.infrastructure.converter;

import org.apache.ibatis.type.MappedTypes;
import org.feynix.domain.agent.model.LLMModelConfig;

/**
 * 模型配置转换器
 */
@MappedTypes(LLMModelConfig.class)
public class AgentModelConfigConverter extends JsonToStringConverter<LLMModelConfig> {

    public AgentModelConfigConverter() {
        super(LLMModelConfig.class);
    }
}
