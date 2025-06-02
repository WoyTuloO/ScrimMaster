package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.*;
import com.woytuloo.ScrimMaster.Repositories.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.Limit;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    @Mock PublicMessageRepository publicRepo;
    @Mock ChatRoomRepository roomRepo;
    @Mock PrivateMessageRepository privateRepo;
    @Mock UserService userService;

    @InjectMocks ChatService chatService;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test void getChatRoomById_found() {
        ChatRoom r = new ChatRoom("1","a","b");
        when(roomRepo.findById("1")).thenReturn(Optional.of(r));
        assertThat(chatService.getChatRoomById("1")).isPresent();
    }

    @Test void getChatRoomById_notFound() {
        when(roomRepo.findById("x")).thenReturn(Optional.empty());
        assertThat(chatService.getChatRoomById("x")).isEmpty();
    }

    @Test void getLatestPublic() {
        when(publicRepo.findLatest(2)).thenReturn(List.of(new PublicMessage("u", "x", Instant.now())));
        assertThat(chatService.getLatestPublic(2)).hasSize(1);
    }

    @Test void savePublic() {
        PublicMessage msg = new PublicMessage("a","b",Instant.now());
        when(publicRepo.save(any())).thenReturn(msg);
        assertThat(chatService.savePublic("a","b")).isEqualTo(msg);
    }

    @Test void createRoom() {
        ChatRoom r = new ChatRoom("rid","a","b");
        when(roomRepo.save(any())).thenReturn(r);
        assertThat(chatService.createRoom("rid","a","b")).isEqualTo(r);
    }

    @Test void isParticipant_true() {
        ChatRoom r = new ChatRoom("id","u1","u2");
        when(roomRepo.findById("id")).thenReturn(Optional.of(r));
        assertThat(chatService.isParticipant("id","u1")).isTrue();
    }

    @Test void isParticipant_false() {
        ChatRoom r = new ChatRoom("id","u1","u2");
        when(roomRepo.findById("id")).thenReturn(Optional.of(r));
        assertThat(chatService.isParticipant("id","nope")).isFalse();
    }

    @Test void getLatestPrivate() {
        when(privateRepo.findLatestByRoom(eq("room"), any(Limit.class))).thenReturn(List.of());
        assertThat(chatService.getLatestPrivate("room", 100)).isEmpty();
    }

    @Test void savePrivate_found() {
        ChatRoom room = new ChatRoom("room", "a", "b");
        when(roomRepo.findById("room")).thenReturn(Optional.of(room));
        when(privateRepo.save(any())).thenReturn(new PrivateMessage(room, "sender", "c", Instant.now()));
        assertThat(chatService.savePrivate("room", "sender", "c")).isNotNull();
    }

    @Test void savePrivate_notFound() {
        when(roomRepo.findById("room")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> chatService.savePrivate("room", "sender", "c"));
    }

    @Test void getUsersChatRooms_found() {
        User user = new User("xx", "a", "a@a.pl");
        user.setId(7L);
        when(userService.getUserById(7L)).thenReturn(Optional.of(user));
        when(roomRepo.findChatRoomsByUserAOrUserB("xx","xx")).thenReturn(List.of(new ChatRoom("id","xx","b")));
        assertThat(chatService.getUsersChatRooms("7")).hasSize(1);
    }

    @Test void getUsersChatRooms_notFound() {
        when(userService.getUserById(8L)).thenReturn(Optional.empty());
        assertThat(chatService.getUsersChatRooms("8")).isEmpty();
    }
}
