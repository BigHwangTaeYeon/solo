package videoChat.solo.domain.webrtc.apiController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import videoChat.solo.domain.webrtc.service.VideoServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import java.net.URI;
import java.util.Objects;

@Controller
public class VideoController {
    private final VideoServiceImpl videoService;
    
    private final String BASE_PATH = "video/rtc/";

    private String getPage(Model model) {
        return Objects.requireNonNull(model.getAttribute("page")).toString();
    }
    
    public VideoController(final VideoServiceImpl videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/randomChat/{uuid}")
    public ResponseEntity<?> randomChat(Model model, @PathVariable("uuid") final String uuid) {
        if(Math.random() * 10 + 1 > 5) {
            // create room
            model.addAllAttributes(videoService.createRandomRoom(uuid));
        } else {
            // connect room | 방이 없으면 생성으로 변경
            model.addAllAttributes(videoService.connectRandomRoom(uuid));
        }
        return ResponseEntity.created(URI.create(BASE_PATH + getPage(model))).build();
    }

    @GetMapping(value = "/userChat")
    public ResponseEntity<?> userChat(Model model){
        model.addAllAttributes(videoService.findUserRoom());
        return ResponseEntity.created(URI.create("video/user/userChat")).build();
    }

    @GetMapping(value = "/video/rtc/chat_room")
    public ResponseEntity<?> userChat(Model model, @RequestParam("uuid") final String uuid, @RequestParam("id") final String id){
        model.addAttribute("uuid", uuid);
        model.addAttribute("id", id);
        return ResponseEntity.created(URI.create("video/rtc/chat_room")).build();
    }

    @RequestMapping(value = "/createUserChat", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> createUserChat(@RequestParam String title, ModelMap model, HttpServletRequest request){
        request.setAttribute("Authorization-refresh", request.getHeader("refresh"));
        model.addAllAttributes(videoService.createUserChat(title, request));
        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/connectUserChat/{id}/{uuid}")
    public ResponseEntity<?> connectUserChat(@PathVariable Long id, @PathVariable String uuid, Model model, HttpServletRequest request){
        model.addAllAttributes(videoService.findUserChat(id, uuid, request));
        return ResponseEntity.created(URI.create(BASE_PATH + getPage(model))).build();
    }

    @GetMapping("/video")
    public ResponseEntity<?> displayMainPage(final Long id, final String uuid) {
        Model model = new ExtendedModelMap();
        model.addAllAttributes(videoService.displayMainPage(id, uuid));
        return ResponseEntity.created(URI.create(BASE_PATH + getPage(model))).build();
    }

    @PostMapping(value = "/room", params = "action=create")
    public ResponseEntity<?> processRoomSelection(
            Model model, @ModelAttribute("id") final String sid, @ModelAttribute("uuid") final String uuid, final BindingResult binding) {
        model.addAllAttributes(videoService.processRoomSelection(sid, uuid, binding));
        return ResponseEntity.created(URI.create(BASE_PATH + getPage(model))).build();
    }

    @GetMapping("/room/{sid}/user/{uuid}")
    public ResponseEntity<?> displaySelectedRoom(Model model, @PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
        model.addAllAttributes(videoService.displaySelectedRoom(sid, uuid));
        return ResponseEntity.created(URI.create(BASE_PATH + getPage(model))).build();
    }

    @GetMapping("/room/{sid}/user/{uuid}/exit")
    public ResponseEntity<?> processRoomExit(Model model, @PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
        model.addAllAttributes(videoService.processRoomExit(sid, uuid));
        return ResponseEntity.created(URI.create(getPage(model))).build();
    }

    @GetMapping("/room/random")
    public ResponseEntity<?> requestRandomRoomNumber(Model model, @ModelAttribute("uuid") final String uuid) {
        model.addAllAttributes(videoService.requestRandomRoomNumber(uuid));
        return ResponseEntity.created(URI.create(BASE_PATH + getPage(model))).build();
    }

    @GetMapping("/offer")
    public ResponseEntity<?> displaySampleSdpOffer() {
        return ResponseEntity.created(URI.create(BASE_PATH + "/sdp_offer")).build();
    }

    @GetMapping("/stream")
    public ResponseEntity<?> displaySampleStreaming() {
        return ResponseEntity.created(URI.create(BASE_PATH + "/streaming")).build();
    }
}
