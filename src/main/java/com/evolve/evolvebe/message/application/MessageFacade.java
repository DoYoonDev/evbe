package com.evolve.evolvebe.message.application;

import com.evolve.evolvebe.auth.application.AuthFacade;
import com.evolve.evolvebe.auth.domain.AuthInfo;
import com.evolve.evolvebe.message.domain.Message;
import com.evolve.evolvebe.message.domain.MessageCommand;
import com.evolve.evolvebe.message.domain.MessageInfo;
import com.evolve.evolvebe.message.domain.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageFacade {

    private final MessageService messageService;

    private final AuthFacade authFacade;

    @Transactional(readOnly = true)
    public MessageInfo.Main getMessage(String id) {
        return messageService.getMessage(id);
    }

    @Transactional(readOnly = true)
    public Page<MessageInfo.Main> getMessages(
        Pageable pageable, MessageCommand.GetList command
    ) {
        return messageService.getMessages(pageable, command);
    }

    @Transactional
    public void createMessage(MessageCommand.Create command) {
        messageService.createMessage(command);
    }

    @Transactional
    public void updateMessage(String id, MessageCommand.Update command) {
        messageService.updateMessage(id, command);
    }

    @Transactional
    public void deleteMessage(String id) {
        messageService.deleteMessage(id);
    }

    @Transactional(readOnly = true)
    public void deployTranslatedMessagesAll() {
        messageService.deployTranslatedMessagesAll();
    }

}
