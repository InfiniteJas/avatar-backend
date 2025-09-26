package kz.nitec.sduchatbotapplication.dto;

import java.util.List;

public record CreateThreadRequest(
        List<MessageItem> messages
) {}
