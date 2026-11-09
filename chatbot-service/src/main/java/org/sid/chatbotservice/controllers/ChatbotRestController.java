package org.sid.chatbotservice.controllers;

import org.sid.chatbotservice.agents.ChatbotAIAgent;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatbotRestController {
    private final ChatbotAIAgent chatbotAIAgent;

    public ChatbotRestController(ChatbotAIAgent chatbotAIAgent) {
        this.chatbotAIAgent = chatbotAIAgent;
    }

    @GetMapping(value = "/chat",produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@RequestParam String query) {

        return chatbotAIAgent.chat(new Prompt(query));
    }
}