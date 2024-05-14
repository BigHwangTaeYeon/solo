package videoChat.solo.domain.webrtc.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import videoChat.solo.domain.users.dto.UserDto;
import videoChat.solo.domain.users.jwt.JwtServiceImpl;
import videoChat.solo.domain.webrtc.domain.Room;
import videoChat.solo.domain.webrtc.util.Parser;
import videoChat.solo.domain.webrtc.domain.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String REDIRECT = "redirect:/";
    
    private final RoomService roomService;
    private final Parser parser;

    private final JwtServiceImpl jwtService;

    public VideoServiceImpl(RoomService roomService, Parser parser, JwtServiceImpl jwtService) {
        this.roomService = roomService;
        this.parser = parser;
        this.jwtService = jwtService;
    }

    public Map<String, Object> displayMainPage(final Long id, final String uuid) {
        Map<String, Object> result = new HashMap<>();
        result.put("page", "video");
        result.put("id", id);
        result.put("rooms", roomService.getRooms());
        result.put("uuid", uuid);
        return result;
    }

    public Map<String, Object> createRandomRoom(final String uuid) {
        Long id = randomValue();
        roomService.addRoom(new Room(id));
        Room room = roomService.findRoomByStringId(String.valueOf(id)).orElse(null);
        return validationRoom(room, String.valueOf(id), uuid);
    }

    public synchronized Map<String, Object> connectRandomRoom(final String uuid) {
        Optional<Room> findRoom = roomService.findSizeOneRoom()
                .stream()
                .filter(c -> !StringUtils.hasText(c.getUuid()))
                .findAny();
        // size 가 1인 방을 찾아서 있으면 들어가고 없으면 생성하는 로직
        if(findRoom.isPresent()) {
            Long id = findRoom.get().getId();
            Room room = roomService.findRoomByStringId(String.valueOf(id)).orElse(null);
            return validationRoom(room, String.valueOf(id), uuid);
        } else {
            return createRandomRoom(uuid);
        }
    }

    public Map<String, Object> findUserRoom() {
        Set<Room> roomList = roomService.findSizeOneRoom();
        Map<String, Object> result = new HashMap<>();
        if(!roomList.isEmpty()) {
            result.put("count", roomList.stream().filter(c -> StringUtils.hasText(c.getUuid())).count());
            result.put("list", roomList.stream().filter(c -> StringUtils.hasText(c.getUuid())).collect(Collectors.toSet()));
        }
        return result;
    }

    public Map<String, Object> createUserChat(String title, String token) {
        Long id = randomValue();
        String email = jwtService
                        .extractEmail(token)
                        .orElseThrow(IllegalAccessError::new);
        Room room = new Room(id, new UserDto(email), title);
        roomService.addRoom(room);
        return validationRoom(room, String.valueOf(id), email);
    }

    public Map<String, Object> createUserChat(String title, HttpServletRequest request) {
        Long id = randomValue();
        String email = jwtService
                        .extractEmail(jwtService.extractAccessToken(request).get())
                        .orElseThrow(IllegalAccessError::new);
        Room room = new Room(id, new UserDto(email), title);
        roomService.addRoom(room);
        return validationRoom(room, String.valueOf(id), email);
    }

    public Map<String, Object> findUserChat(Long id, String uuid, HttpServletRequest request) {
        return validationRoom(
                roomService.findRoomByStringId(String.valueOf(id)).orElse(null), String.valueOf(id), uuid
        );
    }





    public Map<String, Object> processRoomSelection(final String sid, final String uuid, final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Map.of("page", REDIRECT);
        }
        Optional<Long> optionalId = parser.parseId(sid);
        optionalId.ifPresent(
                id -> Optional.ofNullable(uuid)
                        .ifPresent(name -> roomService.addRoom(new Room(id)))
        );

        return this.displayMainPage(optionalId.orElse(null), uuid);
    }

    public Map<String, Object> displaySelectedRoom(final String sid, final String uuid) {
        if (parser.parseId(sid).isPresent()) {
            Room room = roomService.findRoomByStringId(sid).orElse(null);
            return validationRoom(room, sid, uuid);
        } else {
            return (Map<String, Object>) new HashMap<>().put("page", REDIRECT);
        }
    }

    public Map<String, Object> processRoomExit(final String sid, final String uuid) {
        if(sid != null && uuid != null) {
            logger.debug("User {} has left Room #{}", uuid, sid);
            // implement any logic you need
        }
        return Map.of("page", REDIRECT);
    }

    public Map<String, Object> requestRandomRoomNumber(final String uuid) {
        return this.displayMainPage(randomValue(), uuid);
    }

    private Long randomValue() {
        return ThreadLocalRandom.current().nextLong(0, 100);
    }

    private Map<String, Object> validationRoom(Room room, String sid, String uuid) {
        Map<String, Object> result = new HashMap<>();
        if(room != null && uuid != null && !uuid.isEmpty() && room.getClients().size() < 2 ) {
            logger.debug("User {} is going to join Room #{}", uuid, sid);
            // open the chat room
            result.put("page", "chat_room");
            result.put("id", sid);
            result.put("uuid", uuid);
        }
        return result;
    }
}
