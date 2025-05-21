package com.woytuloo.ScrimMaster.Records;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

@JsonSerialize
public record ChatRequest(String from, String to, UUID correlationId) {
}
