package org.feynix.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.feynix.infrastructure.integration.LLM.siliconflow.SiliconFlowLLMService;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class LLMConfig {

    @Value("${llm.provider.default:siliconflow}")
    private String defaultProvider;

    /**
     * 默认LLM服务
     */
    @Bean
    @Primary
    public LLMService defaultLLMService(SiliconFlowLLMService siliconFlowLLMService) {
        // 直接返回SiliconFlow服务作为默认服务
        return siliconFlowLLMService;
    }

    /**
     * LLM服务映射
     */
    @Bean
    public Map<String, LLMService> LLMServiceMap(SiliconFlowLLMService siliconFlowLLMService) {
        Map<String, LLMService> serviceMap = new HashMap<>();
        // 确保键名与defaultProvider + "LlmService"匹配
        serviceMap.put("siliconflowLLMService", siliconFlowLLMService);
        return serviceMap;
    }
}
