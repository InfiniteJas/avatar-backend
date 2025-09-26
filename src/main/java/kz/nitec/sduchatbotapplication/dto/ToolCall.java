package kz.nitec.sduchatbotapplication.dto;

public record ToolCall(
        String id,
        String type,
        ToolFunction function
) {
    public record ToolFunction(String name, String arguments) {}
}
