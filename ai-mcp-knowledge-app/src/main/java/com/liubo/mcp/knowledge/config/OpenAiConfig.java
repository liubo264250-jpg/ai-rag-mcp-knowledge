package com.liubo.mcp.knowledge.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author 68
 * 2026/2/1 20:15
 */
@Configuration
public class OpenAiConfig {

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String apikey;

    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }

    @Bean
    public OpenAiApi openAiApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apikey)
                .build();
    }

    @Bean("openAiPgVectorStore")
    public PgVectorStore pgVectorStore(OpenAiApi openAiApi, JdbcTemplate jdbcTemplate) {
        OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel(openAiApi);
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName("vector_store_openai")
                .build();
    }

    @Bean("chatClientBuilder")
    public ChatClient.Builder chatClientBuilder(OpenAiChatModel openAiChatModel) {
        return new DefaultChatClientBuilder(openAiChatModel, ObservationRegistry.NOOP, (ChatClientObservationConvention) null);
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, ToolCallbackProvider tools) {
        ChatClient chatClient = new DefaultChatClientBuilder(openAiChatModel, ObservationRegistry.NOOP, (ChatClientObservationConvention) null)
                .defaultTools(tools)
                .defaultOptions(OpenAiChatOptions
                        .builder()
                        .model("gpt-4o")
                        .build()).
                build();
        return chatClient;
    }
}
