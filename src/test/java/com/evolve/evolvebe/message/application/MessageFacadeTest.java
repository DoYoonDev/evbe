package com.evolve.evolvebe.message.application;

import com.evolve.evolvebe.message.domain.MessageCommand;
import com.evolve.evolvebe.message.domain.MessageInfo;
import com.evolve.evolvebe.message.domain.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageFacadeTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageFacade messageFacade;

    @Test
    @DisplayName("메시지 ID로 조회")
    void getMessage() {
        // given
        String id = "COM.MSG.HELLO";
        MessageInfo.Main messageInfo = mock(MessageInfo.Main.class);
        when(messageService.getMessage(id)).thenReturn(messageInfo);

        // when
        MessageInfo.Main result = messageFacade.getMessage(id);

        // then
        assertEquals(messageInfo, result);
        verify(messageService).getMessage(id);
    }

    @Test
    @DisplayName("메시지 목록 조회")
    void getMessages() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        MessageCommand.GetList command = MessageCommand.GetList.builder()
            .id("menu")
            .koText("관리")
            .enText("")
            .isActive(true)
            .build();

        Page<MessageInfo.Main> messageInfos = new PageImpl<>(
            List.of(
                mock(MessageInfo.Main.class),
                mock(MessageInfo.Main.class)
            ), pageable, 2
        );

        when(messageService.getMessages(pageable,command)).thenReturn(messageInfos);

        // when
        Page<MessageInfo.Main> result = messageFacade.getMessages(pageable, command);

        // then
        assertThat(result).isNotNull();
        assertEquals(2, result.getContent().size());
        verify(messageService).getMessages(pageable, command);
    }

    @Test
    @DisplayName("메시지 생성")
    void createMessage() {
        // given
        MessageCommand.Create command = MessageCommand.Create.builder()
                .id("COM.MSG.HELLO")
                .koText("안녕하세요")
                .enText("Hello")
                .isActive(true)
                .build();

        // when
        messageFacade.createMessage(command);

        // then
        verify(messageService).createMessage(command);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    @DisplayName("메시지 수정")
    void updateMessage() {
        // given
        String id = "COM.MSG.HELLO";
        MessageCommand.Update command = mock(MessageCommand.Update.class);

        // when
        messageFacade.updateMessage(id, command);

        // then
        verify(messageService).updateMessage(id, command);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    @DisplayName("메시지 삭제")
    void deleteMessage() {
        // given
        String id = "COM.MSG.HELLO";

        // when
        messageFacade.deleteMessage(id);

        // then
        verify(messageService).deleteMessage(id);
    }

    @Test
    @DisplayName("메시지 다국어 반영")
    void deployTranslatedMessagesAll() {
        // when
        messageFacade.deployTranslatedMessagesAll();

        // then
        verify(messageService).deployTranslatedMessagesAll();
    }
  
}