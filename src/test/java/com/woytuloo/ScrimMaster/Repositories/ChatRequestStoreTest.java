package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Records.ChatRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ChatRequestStoreTest {

    private ChatRequestStore store;

    @BeforeEach
    void setUp() {
        store = new ChatRequestStore();
    }

    @Test
    void saveAndGetRequest() {
        UUID corrId = UUID.randomUUID();
        ChatRequest rq = new ChatRequest("ziutek", "alina", corrId);

        store.save(rq);

        assertThat(store.get(corrId)).isPresent();
        assertThat(store.get(corrId).get().from()).isEqualTo("ziutek");
    }

    @Test
    void removeRequest() {
        UUID corrId = UUID.randomUUID();
        ChatRequest rq = new ChatRequest("janek", "krzychu", corrId);

        store.save(rq);
        store.remove(corrId);

        assertThat(store.get(corrId)).isNotPresent();
    }
}
