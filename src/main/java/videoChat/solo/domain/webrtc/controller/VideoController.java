package videoChat.solo.domain.webrtc.controller;

//import io.github.benkoff.webrtcss.service.MainService;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import videoChat.solo.domain.webrtc.service.MainServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

@Controller
@ControllerAdvice
public class VideoController {
    private final MainServiceImpl mainService;
    
    public VideoController(final MainServiceImpl mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/randomChat")
    public String randomChat(final Long id, final String uuid) {
        Model model = new ExtendedModelMap();
        model.addAllAttributes(mainService.displayMainPage(id, uuid));
        return "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @GetMapping("/video")
    public String displayMainPage(final Long id, final String uuid) {
        Model model = new ExtendedModelMap();
        model.addAllAttributes(mainService.displayMainPage(id, uuid));
        return "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @PostMapping(value = "/room", params = "action=create")
    public String processRoomSelection(
            Model model, @ModelAttribute("id") final String sid, @ModelAttribute("uuid") final String uuid, final BindingResult binding) {
        model.addAllAttributes(mainService.processRoomSelection(sid, uuid, binding));
        return "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @GetMapping("/room/{sid}/user/{uuid}")
    public String displaySelectedRoom(Model model, @PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
        model.addAllAttributes(mainService.displaySelectedRoom(sid, uuid));
        return "video/rtc/" + Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @GetMapping("/room/{sid}/user/{uuid}/exit")
    public String processRoomExit(Model model, @PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
        model.addAllAttributes(mainService.processRoomExit(sid, uuid));
        return Objects.requireNonNull(model.getAttribute("page")).toString();
    }

    @GetMapping("/room/random")
    public String requestRandomRoomNumber(Model model, @ModelAttribute("uuid") final String uuid) {
        model.addAllAttributes(mainService.requestRandomRoomNumber(uuid));
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

//    @GetMapping({"", "/", "/index", "/home", "/main"})
//    public ModelAndView displayMainPage(final Long id, final String uuid) {
//        return this.mainService.displayMainPage(id, uuid);
//    }
//
//    @PostMapping(value = "/room", params = "action=create")
//    public ModelAndView processRoomSelection(@ModelAttribute("id") final String sid, @ModelAttribute("uuid") final String uuid, final BindingResult binding) {
//        return this.mainService.processRoomSelection(sid, uuid, binding);
//    }
//
//    @GetMapping("/room/{sid}/user/{uuid}")
//    public ModelAndView displaySelectedRoom(@PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
//        return this.mainService.displaySelectedRoom(sid, uuid);
//    }
//
//    @GetMapping("/room/{sid}/user/{uuid}/exit")
//    public ModelAndView processRoomExit(@PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
//        return this.mainService.processRoomExit(sid, uuid);
//    }
//
//    @GetMapping("/room/random")
//    public ModelAndView requestRandomRoomNumber(@ModelAttribute("uuid") final String uuid) {
//        return mainService.requestRandomRoomNumber(uuid);
//    }
//
//    @GetMapping("/offer")
//    public ModelAndView displaySampleSdpOffer() {
//        return new ModelAndView("sdp_offer");
//    }
//
//    @GetMapping("/stream")
//    public ModelAndView displaySampleStreaming() {
//        return new ModelAndView("streaming");
//    }
}
