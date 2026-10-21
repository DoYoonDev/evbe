package com.evolve.evolvebe.message.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface MessageRepository {

    Message getMessage(String id);

    Page<Message> getMessages(Pageable pageable, MessageCommand.GetList command);

    List<Message> getMessages(Boolean isActive);

    List<Message> getMessages(Boolean isActive, Sort sort);

    void saveMessage(Message message);

    void deleteMessage(String id);

}
