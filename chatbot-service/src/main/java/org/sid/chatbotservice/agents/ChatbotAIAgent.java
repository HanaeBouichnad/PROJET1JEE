package org.sid.chatbotservice.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ChatbotAIAgent {
    private final ChatClient chatClient;

    public ChatbotAIAgent(ChatClient.Builder chatClientBuilder,
                          ChatMemory chatMemory,
                          ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are a bank assistant.
                        Use the available tools to answer questions about customers and bank accounts.
                        Always display identifiers (IDs) in full: never shorten or truncate them with "...".
                        When asked for a table, use a Markdown table with all the requested columns.
                        Answer in the same language as the user's question.
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(toolCallbackProvider)
                .build();
    }

    // texte seul : contrôleur REST et Discord
    public String chat(String query) {
        return chatClient.prompt()
                .user(query)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "default"))
                .call()
                .content();
    }

    // texte + images : Telegram
    public String chat(Prompt prompt) {
        return chatClient.prompt(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "default"))
                .call()
                .content();
    }

    public Flux<String> chatStream(Prompt prompt) {
        return chatClient.prompt(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "default"))
                .stream()
                .content();
    }
}