package videoChat.solo.domain.webrtc.domain;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;
import videoChat.solo.domain.users.dto.UserDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class Room {
    private final Long id;
    private String userEmail;
    private String title;
    // sockets by user names
    private final Map<String, WebSocketSession> clients = new HashMap<>();

    public Room(Long id) {
        this.id = id;
    }

    public Room(Long id, UserDto dto, String title) {
        this.id = id;
        this.userEmail = dto.getEmail();
        this.title = title;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Room room = (Room) o;
        return Objects.equals(getId(), room.getId()) &&
                Objects.equals(getClients(), room.getClients());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getClients());
    }
}
