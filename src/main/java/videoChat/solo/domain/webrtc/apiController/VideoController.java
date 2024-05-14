package videoChat.solo.domain.webrtc.apiController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import videoChat.solo.domain.webrtc.service.VideoServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import java.net.URI;
import java.util.Objects;

@Controller
@ControllerAdvice
public class VideoController {
    private final VideoServiceImpl videoService;
    
    private final String BASE_PATH = "video/rtc/";

    private String getPage(Model model) {
        return Objects.requireNonNull(model.getAttribute("page")).toString();
    }
    
    public VideoController(final VideoServiceImpl videoService) {
        this.videoService = videoService;
    }


    @GetMapping(value = "/video/rtc/chat_room")
    public String userChat(){
        return "video/rtc/chat_room";
    }

    @GetMapping("/randomChat/{uuid}")
    public String randomChat(Model model, @PathVariable("uuid") final String uuid) {
        if(Math.random() * 10 + 1 > 5) {
            // create room
            model.addAllAttributes(videoService.createRandomRoom(uuid));
        } else {
            // connect room | 방이 없으면 생성으로 변경
            model.addAllAttributes(videoService.connectRandomRoom(uuid));
        }
        return BASE_PATH + getPage(model);
    }

    @GetMapping(value = "/userChat")
    public String userChat(Model model){
        model.addAllAttributes(videoService.findUserRoom());
        return "video/user/userChat";
    }

    @GetMapping(value = "/createUserChat")
    public ResponseEntity<?> createUserChat(@RequestParam String title, Model model, HttpServletRequest request){
        model.addAllAttributes(videoService.createUserChat(title, request));
//        return "forward:/" + BASE_PATH + getPage(model);
        return ResponseEntity.created(URI.create(BASE_PATH + getPage(model))).build();
    }

    @GetMapping(value = "/connectUserChat/{id}/{uuid}")
    public String connectUserChat(@PathVariable Long id, @PathVariable String uuid, Model model, HttpServletRequest request){
        model.addAllAttributes(videoService.findUserChat(id, uuid, request));
        return BASE_PATH + getPage(model);
    }

    @GetMapping("/video")
    public String displayMainPage(final Long id, final String uuid) {
        Model model = new ExtendedModelMap();
        model.addAllAttributes(videoService.displayMainPage(id, uuid));
        return BASE_PATH + getPage(model);
    }

    @PostMapping(value = "/room", params = "action=create")
    public String processRoomSelection(
            Model model, @ModelAttribute("id") final String sid, @ModelAttribute("uuid") final String uuid, final BindingResult binding) {
        model.addAllAttributes(videoService.processRoomSelection(sid, uuid, binding));
        return BASE_PATH + getPage(model);
    }

    @GetMapping("/room/{sid}/user/{uuid}")
    public String displaySelectedRoom(Model model, @PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
        model.addAllAttributes(videoService.displaySelectedRoom(sid, uuid));
        return BASE_PATH + getPage(model);
    }

    @GetMapping("/room/{sid}/user/{uuid}/exit")
    public String processRoomExit(Model model, @PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
        model.addAllAttributes(videoService.processRoomExit(sid, uuid));
        return getPage(model);
    }

    @GetMapping("/room/random")
    public String requestRandomRoomNumber(Model model, @ModelAttribute("uuid") final String uuid) {
        model.addAllAttributes(videoService.requestRandomRoomNumber(uuid));
        return BASE_PATH + getPage(model);
    }

    @GetMapping("/offer")
    public String displaySampleSdpOffer() {
        return BASE_PATH + "/sdp_offer";
    }

    @GetMapping("/stream")
    public String displaySampleStreaming() {
        return BASE_PATH + "/streaming";
    }
}
