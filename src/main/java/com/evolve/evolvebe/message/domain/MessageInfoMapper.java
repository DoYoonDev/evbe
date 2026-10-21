package com.evolve.evolvebe.message.domain;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

@Mapper(
    componentModel = "spring",
    injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface MessageInfoMapper {

    MessageInfo.Main of(Message message);

    default Page<MessageInfo.Main> of(Page<Message> messageList) {
        if (messageList == null) {
            return null;
        }

        List<MessageInfo.Main> list = new ArrayList<>(messageList.getSize());
        for (Message message : messageList) {
            list.add(of(message));
        }

        return new PageImpl<>(list, messageList.getPageable(), messageList.getTotalElements());
    }

}
