package solo.video_service.webrtc.service;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

public interface MainService {
    Map<String, Object> displayMainPage(Long id, String uuid);
    Map<String, Object> processRoomSelection(Model model, String sid, String uuid, BindingResult bindingResult);
    ModelAndView displaySelectedRoom(String sid, String uuid);
    ModelAndView processRoomExit(String sid, String uuid);
    ModelAndView requestRandomRoomNumber(String uuid);
}
