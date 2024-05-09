package videoChat.solo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import videoChat.solo.domain.webrtc.config.WebSocketConfig;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SoloApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void contextLoads() throws Exception {
		if (applicationContext != null) {
			String[] beans = applicationContext.getBeanDefinitionNames();

			for (String bean : beans) {
				System.out.println("bean : " + bean);
			}
		}

		System.out.println("websocket bean : " + applicationContext.getBean(WebSocketConfig.class).signalHandler());
	}

	@Test
	public void randomTest() throws Exception {
	    // given
		for(int i=0; i<100; i ++) {
			int a = (int) (Math.random() * 10 + 1);
			System.out.println("a = " + a);
		}
	    // when
	    // then
	}
}
