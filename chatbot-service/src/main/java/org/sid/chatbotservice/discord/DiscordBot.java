package org.sid.chatbotservice.discord;

import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.sid.chatbotservice.agents.ChatbotAIAgent;
import org.springframework.ai.chat.prompt.Prompt;

@DiscordController
public class DiscordBot {
    private ChatbotAIAgent chatbotAIAgent;

    public DiscordBot(ChatbotAIAgent chatbotAIAgent) {
        this.chatbotAIAgent = chatbotAIAgent;
    }

    @DiscordMapping
    private void perform(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String query = event.getMessage().getContentRaw();
        String response = chatbotAIAgent.chat(new Prompt(query));
        event.getChannel().sendMessage(response).queue();
    }
}
