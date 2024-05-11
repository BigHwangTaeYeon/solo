package videoChat.solo.domain.webrtc.apiController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import videoChat.solo.domain.webrtc.service.VideoServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import java.util.Objects;

@Controller
@ControllerAdvice
public class VideoController {
    private final VideoServiceImpl videoService;
    
    public VideoController(final VideoServiceImpl videoService) {
        this.videoService = videoService;
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
        return "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @GetMapping(value = "/userChat")
    public String userChat(Model model){
        model.addAllAttributes(videoService.findUserRoom());
        return "video/user/userChat";
    }

    @GetMapping(value = "/createUserChat")
    public void createUserChat(@RequestParam String title, Model model, HttpServletRequest request){
        model.addAllAttributes(videoService.createUserChat(title, request));
        model.addAttribute("redirectUrl", "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString());
    }

    @GetMapping(value = "/connectUserChat")
    public String connectUserChat(@RequestParam Long id, @RequestParam String uuid, Model model, HttpServletRequest request){
        model.addAllAttributes(videoService.findUserChat(id, uuid, request));
        return "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @GetMapping("/video")
    public String displayMainPage(final Long id, final String uuid) {
        Model model = new ExtendedModelMap();
        model.addAllAttributes(videoService.displayMainPage(id, uuid));
        return "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @PostMapping(value = "/room", params = "action=create")
    public String processRoomSelection(
            Model model, @ModelAttribute("id") final String sid, @ModelAttribute("uuid") final String uuid, final BindingResult binding) {
        model.addAllAttributes(videoService.processRoomSelection(sid, uuid, binding));
        return "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @GetMapping("/room/{sid}/user/{uuid}")
    public String displaySelectedRoom(Model model, @PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
        model.addAllAttributes(videoService.displaySelectedRoom(sid, uuid));
        return "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @GetMapping("/room/{sid}/user/{uuid}/exit")
    public String processRoomExit(Model model, @PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
        model.addAllAttributes(videoService.processRoomExit(sid, uuid));
        return Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @GetMapping("/room/random")
    public String requestRandomRoomNumber(Model model, @ModelAttribute("uuid") final String uuid) {
        model.addAllAttributes(videoService.requestRandomRoomNumber(uuid));
        return "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @GetMapping("/offer")
    public String displaySampleSdpOffer() {
        return "video/rtc/sdp_offer";
    }

    @GetMapping("/stream")
    public String displaySampleStreaming() {
        return "video/rtc/streaming";
    }
}
