package videoChat.solo.domain.webrtc.service;

//import io.github.benkoff.webrtcss.domain.Room;
//import io.github.benkoff.webrtcss.domain.RoomService;
//import io.github.benkoff.webrtcss.util.Parser;
import videoChat.solo.domain.webrtc.domain.Room;
import videoChat.solo.domain.webrtc.util.Parser;
import videoChat.solo.domain.webrtc.domain.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MainServiceImpl {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String REDIRECT = "redirect:/";
    
    private final RoomService roomService;
    private final Parser parser;

    @Autowired
    public MainServiceImpl(final RoomService roomService, final Parser parser) {
        this.roomService = roomService;
        this.parser = parser;
    }

    public Map<String, Object> displayMainPage(final Long id, final String uuid) {
        Map<String, Object> result = new HashMap<>();
        result.put("page", "video");
        result.put("id", id);
        result.put("rooms", roomService.getRooms());
        result.put("uuid", uuid);
        return result;
    }

    public Map<String, Object> createRoom(final String uuid) {
        Long id = randomValue();
        roomService.addRoom(new Room(id));
        Room room = roomService.findRoomByStringId(String.valueOf(id)).orElse(null);
        return validationRoom(room, String.valueOf(id), uuid);
    }

    public Map<String, Object> connectRoom(final String uuid) {
        // size 가 1인 방을 찾아서 있으면 들어가고 없으면 생성하는 로직
        if(roomService.findSizeOneRoom().isPresent()) {
            Long id = roomService.findSizeOneRoom().get().getId();
            Room room = roomService.findRoomByStringId(String.valueOf(id)).orElse(null);

            return validationRoom(room, String.valueOf(id), uuid);
        } else {
            return createRoom(uuid);
        }
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

    private synchronized Map<String, Object> validationRoom(Room room, String sid, String uuid) {
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

//    @Override
//    public ModelAndView displayMainPage(Model model, final Long id, final String uuid) {
//        final ModelAndView modelAndView = new ModelAndView("main");
//        modelAndView.addObject("id", id);
//        modelAndView.addObject("rooms", roomService.getRooms());
//        modelAndView.addObject("uuid", uuid);
//
//        return modelAndView;
//    }
//
//    @Override
//    public ModelAndView processRoomSelection(final String sid, final String uuid, final BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            // simplified version, no errors processing
//            return new ModelAndView(REDIRECT);
//        }
//        Optional<Long> optionalId = parser.parseId(sid);
//        optionalId.ifPresent(id -> Optional.ofNullable(uuid).ifPresent(name -> roomService.addRoom(new Room(id))));
//
//        return this.displayMainPage(model, optionalId.orElse(null), uuid);
//    }
//
//    @Override
//    public ModelAndView displaySelectedRoom(final String sid, final String uuid) {
//        // redirect to main page if provided data is invalid
//        ModelAndView modelAndView = new ModelAndView(REDIRECT);
//
//        if (parser.parseId(sid).isPresent()) {
//            Room room = roomService.findRoomByStringId(sid).orElse(null);
//            // client size < 2 추가, 3명 이상 참여 불가
//            // 동시성 문제 추가 해야함
////            if(room != null && uuid != null && !uuid.isEmpty() && room.getClients().size() < 2 ) {
////                logger.debug("User {} is going to join Room #{}", uuid, sid);
////                // open the chat room
////                modelAndView = new ModelAndView("chat_room", "id", sid);
////                modelAndView.addObject("uuid", uuid);
////            }
//            // 위에는 기존 코드, 아래는 동시성 해결
//            modelAndView = validationRoom(modelAndView, room, sid, uuid);
//        }
//
//        return modelAndView;
//    }
//
//    @Override
//    public ModelAndView processRoomExit(final String sid, final String uuid) {
//        if(sid != null && uuid != null) {
//            logger.debug("User {} has left Room #{}", uuid, sid);
//            // implement any logic you need
//        }
//        return new ModelAndView(REDIRECT);
//    }
//
//    @Override
//    public ModelAndView requestRandomRoomNumber(final String uuid) {
//        return this.displayMainPage(model, randomValue(), uuid);
//    }
//
//    private Long randomValue() {
//        return ThreadLocalRandom.current().nextLong(0, 100);
//    }
//
//    private synchronized ModelAndView validationRoom(ModelAndView modelAndView, Room room, String sid, String uuid) {
//        if(room != null && uuid != null && !uuid.isEmpty() && room.getClients().size() < 2 ) {
//            logger.debug("User {} is going to join Room #{}", uuid, sid);
//            // open the chat room
//            modelAndView = new ModelAndView("chat_room", "id", sid);
//            modelAndView.addObject("uuid", uuid);
//        }
//        return modelAndView;
//    }
}
