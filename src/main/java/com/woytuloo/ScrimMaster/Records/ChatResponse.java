package com.woytuloo.ScrimMaster.Records;

import java.util.UUID;

public record ChatResponse(String status, String from, String to, UUID correlationId) {
}

