package videoChat.solo.domain.videoChat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import videoChat.solo.domain.videoChat.dto.WebSocketMessage;
import videoChat.solo.domain.videoChat.service.RtcChatService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RtcController {

    private final RtcChatService rtcChatService;

    @PostMapping("/webrtc/usercount")
    public String webRTC(@ModelAttribute WebSocketMessage webSocketMessage) {
        log.info("MESSAGE : {}", webSocketMessage.toString());
        return Boolean.toString(rtcChatService.findUserCount(webSocketMessage));
    }


}
