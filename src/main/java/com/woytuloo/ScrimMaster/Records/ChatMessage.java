package com.woytuloo.ScrimMaster.Records;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record ChatMessage(
        String type,
        String content,
        String sender
) { }