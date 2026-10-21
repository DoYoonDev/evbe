package com.evolve.evolvebe.message.interfaces;

import com.evolve.evolvebe.message.domain.MessageInfo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

public class MessageResponse {

    @Getter
    @Builder
    public static class Main {
        private MessageInfo.Main messageInfo;
    }

    @Getter
    @Builder
    public static class MessagePage {
        private Page<MessageInfo.Main> messageInfos;
    }

}
