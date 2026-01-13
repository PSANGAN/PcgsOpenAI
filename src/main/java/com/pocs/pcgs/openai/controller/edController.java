package com.pocs.pcgs.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

@RestController
@RequestMapping("/api/ed")
public class edController {

    private final ChatClient chatClient;
    private final WebClient webClient;

    public edController(@Qualifier("chatMemoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
        this.webClient = WebClient.create();
    }

    @GetMapping("/website")
    public String website(@RequestParam String url) {
        String websiteContent = fetchWebsiteContents(url);
        return chatClient.prompt()
                .messages(messagesFor(websiteContent))
                .call()
                .content();
    }

    private String fetchWebsiteContents(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private List<Message> messagesFor(String websiteContent) {
        return List.of(
                new SystemMessage("You are a helpful assistant that summarizes websites."),
                new UserMessage("Summarize the following website content:\n\n" + websiteContent)
        );
    }

}
