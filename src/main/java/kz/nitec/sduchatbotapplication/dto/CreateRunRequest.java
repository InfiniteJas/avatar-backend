package kz.nitec.sduchatbotapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateRunRequest(@JsonProperty("assistant_id") String assistantId) {}

