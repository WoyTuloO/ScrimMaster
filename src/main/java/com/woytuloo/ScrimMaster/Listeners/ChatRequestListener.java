package com.woytuloo.ScrimMaster.Listeners;

import com.woytuloo.ScrimMaster.Records.ChatAccept;
import com.woytuloo.ScrimMaster.Records.ChatRequest;
import com.woytuloo.ScrimMaster.Repositories.ChatRequestStore;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatRequestListener {

    private final SimpMessagingTemplate ws;
    private final ChatRequestStore store;

    @KafkaListener(topics = "private-chat-requests", groupId = "chat-request")
    void onRequest(ChatRequest rq) {
        store.save(rq);
        ws.convertAndSendToUser(rq.to(), "/queue/chat-requests", rq);
    }

    @KafkaListener(topics = "private-chat-accepted", groupId = "chat-accept")
    void onAccept(ChatAccept ac) {
        store.get(ac.correlationId()).ifPresent(rq -> {
            ws.convertAndSendToUser(rq.from(), "/queue/chat-accepted", ac);
            ws.convertAndSendToUser(rq.to(), "/queue/chat-accepted", ac);
            store.remove(ac.correlationId());
        });
    }
}


