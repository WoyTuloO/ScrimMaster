package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Records.ChatRequest;
import org.springframework.stereotype.Component;


import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatRequestStore {
    private final ConcurrentHashMap<UUID, ChatRequest> map = new ConcurrentHashMap<>();

    public void save(ChatRequest rq) {
        map.put(rq.correlationId(), rq);
    }

    public Optional<ChatRequest> get(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    public void remove(UUID id) {
        map.remove(id);
    }
}
