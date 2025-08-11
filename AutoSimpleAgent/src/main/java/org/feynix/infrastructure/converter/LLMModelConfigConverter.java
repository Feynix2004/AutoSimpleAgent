package org.feynix.infrastructure.converter;

import org.apache.ibatis.type.MappedTypes;
import org.feynix.domain.llm.model.config.LLMModelConfig;

/**
 * LLMModelConfig JSON转换器
 */
@MappedTypes(LLMModelConfig.class)
public class LLMModelConfigConverter extends JsonToStringConverter<LLMModelConfig> {

    public LLMModelConfigConverter() {
        super(LLMModelConfig.class);
    }
}
