package videoChat.solo.domain.videoChat.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Map;

@Setter
@Getter
public class ChatRoomDto {
    @NotNull
    private String roomId; // 채팅방 아이디
    private String roomName; // 채팅방 이름
    private int userCount; // 채팅방 인원수
    private int maxUserCnt; // 채팅방 최대 인원 제한

    private String roomPwd; // 채팅방 삭제시 필요한 pwd
    private boolean secretChk; // 채팅방 잠금 여부
    public enum ChatType{  // 화상 채팅, 문자 채팅
        MSG, RTC
    }
    private ChatType chatType; //  채팅 타입 여부

    // ChatRoomDto 클래스는 하나로 가되 서비스를 나누었음
    private Map<String, ?> userList;

    @Builder
    public ChatRoomDto(String roomId, String roomName, int userCount, int maxUserCnt, String roomPwd, boolean secretChk, ChatType chatType, Map<String, ?> userList) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.userCount = userCount;
        this.maxUserCnt = maxUserCnt;
        this.roomPwd = roomPwd;
        this.secretChk = secretChk;
        this.chatType = chatType;
        this.userList = userList;
    }
}
