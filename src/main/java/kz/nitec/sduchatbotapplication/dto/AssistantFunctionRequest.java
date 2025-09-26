package kz.nitec.sduchatbotapplication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AssistantFunctionRequest(
        String function_name,
        JsonNode arguments
) {}
