package com.jobcrm.ai.dto;

import java.util.List;

public class ChatResponse {

    private List<Choice> choices;

    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }

    public String getFirstContent() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).getMessage().getContent();
        }
        return null;
    }

    public static class Choice {
        private Message message;

        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
    }

    public static class Message {
        private String content;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}