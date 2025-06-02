package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.*;
import com.woytuloo.ScrimMaster.Repositories.*;
import org.springframework.data.domain.Limit;

import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private final PublicMessageRepository publicRepo;
    private final ChatRoomRepository roomRepo;
    private final PrivateMessageRepository privateRepo;
    private final UserService userService;

    public ChatService(PublicMessageRepository publicRepo,
                       ChatRoomRepository roomRepo,
                       PrivateMessageRepository privateRepo, UserService userService) {
        this.publicRepo  = publicRepo;
        this.roomRepo    = roomRepo;
        this.privateRepo = privateRepo;
        this.userService = userService;
    }

    public Optional<ChatRoom> getChatRoomById(String id) {
        return roomRepo.findById(id);
    }

    public List<PublicMessage> getLatestPublic(int limit) {
        return publicRepo.findLatest(limit);
    }
    public PublicMessage savePublic(String sender, String content) {
        return publicRepo.save(new PublicMessage(sender, content, Instant.now()));
    }

    public ChatRoom createRoom(String roomId, String userA, String userB) {
        ChatRoom room = new ChatRoom(roomId, userA, userB);
        return roomRepo.save(room);
    }
    public boolean isParticipant(String roomId, String user) {
        return roomRepo.findById(roomId)
                .map(r -> r.getUserA().equals(user) || r.getUserB().equals(user))
                .orElse(false);
    }
    public List<PrivateMessage> getLatestPrivate(String roomId, int limit) {
        return privateRepo.findLatestByRoom(roomId, Limit.of(100));
    }
    public PrivateMessage savePrivate(String roomId, String sender, String content) {
        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown room " + roomId));
        return privateRepo.save(new PrivateMessage(room, sender, content, Instant.now()));
    }

    public List<ChatRoom> getUsersChatRooms(String userId) {
        Optional<User> userById = userService.getUserById(Long.parseLong(userId));
        if (userById.isPresent()) {
            String username = userById.get().getUsername();
            return roomRepo.findChatRoomsByUserAOrUserB(username, username);
        }
        return new ArrayList<>();
    }
}