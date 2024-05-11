package videoChat.solo.domain.webrtc.domain;

import videoChat.solo.domain.webrtc.util.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {    
    private final Parser parser;
    // repository substitution since this is a very simple realization
    private final Set<Room> rooms = new TreeSet<>(Comparator.comparing(Room::getId));

    @Autowired
    public RoomService(final Parser parser) {
        this.parser = parser;
    }

    public Set<Room> getRooms() {
        final TreeSet<Room> defensiveCopy = new TreeSet<>(Comparator.comparing(Room::getId));
        defensiveCopy.addAll(rooms);

        return defensiveCopy;
    }

    public Boolean addRoom(final Room room) {
        return rooms.add(room);
    }

    public Set<Room> findSizeOneRoom() {
        return rooms.stream()
                .filter(r -> r.getClients().size() == 1)
                .collect(Collectors.toSet());
    }

    public Optional<Room> findRoomByStringId(final String sid) {
        // simple get() because of parser errors handling
        return rooms.stream().filter(r -> r.getId().equals(parser.parseId(sid).get())).findAny();
    }

    public Long getRoomId(Room room) {
        return room.getId();
    }

    public Map<String, WebSocketSession> getClients(final Room room) {
        return Optional.ofNullable(room)
                .map(r -> Collections.unmodifiableMap(r.getClients()))
                .orElse(Collections.emptyMap());
    }

    public WebSocketSession addClient(final Room room, final String name, final WebSocketSession session) {
        return room.getClients().put(name, session);
    }

    public WebSocketSession removeClientByName(final Room room, final String name) {
        return room.getClients().remove(name);
    }
}
