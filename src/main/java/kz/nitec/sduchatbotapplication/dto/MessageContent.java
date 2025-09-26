package kz.nitec.sduchatbotapplication.dto;

public record MessageContent(
        String type,
        MessageText text
) {
    public record MessageText(String value) {}
}
