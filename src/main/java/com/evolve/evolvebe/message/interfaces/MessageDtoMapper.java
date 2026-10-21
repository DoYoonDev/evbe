package com.evolve.evolvebe.message.interfaces;

import com.evolve.evolvebe.message.domain.MessageCommand;
import com.evolve.evolvebe.message.domain.MessageInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface MessageDtoMapper {

    MessageCommand.GetList of(MessageRequest.GetList request);

    MessageCommand.Create of(MessageRequest.Create request);

    MessageCommand.Update of(MessageRequest.Update request);

    MessageResponse.Main of(MessageInfo.Main messageInfo);

    default MessageResponse.MessagePage of(Page<MessageInfo.Main> messageInfos) {
        return MessageResponse.MessagePage.builder()
            .messageInfos(messageInfos)
            .build();
    }
}
