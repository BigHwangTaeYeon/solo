package videoChat.solo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
public class SoloApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoloApplication.class, args);
	}

}
