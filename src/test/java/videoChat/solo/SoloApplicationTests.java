package videoChat.solo;

import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;
import videoChat.solo.domain.users.dto.UserDto;
import videoChat.solo.domain.webrtc.config.WebSocketConfig;

import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SoloApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void contextLoads() throws Exception {
		Set<Room> roomList = new HashSet<>();
		roomList.add(new Room(1L, "test001", "test001"));
		roomList.add(new Room(2L));
		roomList.add(new Room(3L, "test003", "test003"));
		roomList.add(new Room(4L));

		Set<Room> collect = roomList.stream().filter(a -> StringUtils.hasText(a.getUser())).collect(Collectors.toSet());

		for (Room s : collect) {
			System.out.print("getId. = " + s.getId() + "           ");
			System.out.print("getUser. = " + s.getUser() + "           ");
			System.out.println("getTitle. = " + s.getTitle());
		}
	}
}

@Getter
class Room {
	private final Long id;
	private String user;
	private String title;
	// sockets by user names
	private final Map<String, WebSocketSession> clients = new HashMap<>();

	public Room(Long id) {
		this.id = id;
	}

	public Room(Long id, String dto, String title) {
		this.id = id;
		this.user = dto;
		this.title = title;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final videoChat.solo.domain.webrtc.domain.Room room = (videoChat.solo.domain.webrtc.domain.Room) o;
		return Objects.equals(getId(), room.getId()) &&
				Objects.equals(getClients(), room.getClients());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getClients());
	}
}
