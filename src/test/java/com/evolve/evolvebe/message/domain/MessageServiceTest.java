package com.evolve.evolvebe.message.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private MessageInfoMapper messageInfoMapper;

	@InjectMocks
	private MessageService messageService;

	@Nested
	@DisplayName("메시지 ID로 조회")
	class GetMessage {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("getMessage: 성공")
			void getMessage() {
				// given
				String id = "COM.MSG.HELLO";
				Message message = Message.builder().id(id).build();
				when(messageRepository.getMessage(id)).thenReturn(message);
				when(messageInfoMapper.of(message)).thenReturn(mock(MessageInfo.Main.class));

				// when
				MessageInfo.Main result = messageService.getMessage(id);

				// then
				assertNotNull(result);
				verify(messageRepository).getMessage(id);
				verify(messageInfoMapper).of(message);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("getMessage: 존재하지 않는 메시지 ID")
			void getMessage_존재하지_않는_메시지_ID() {
				// given
				String id = "COM.MSG.HELLO";
				when(messageRepository.getMessage(id)).thenReturn(null);

				// when
				MessageInfo.Main result = messageService.getMessage(id);

				// then
				assertNull(result);
				verify(messageRepository).getMessage(id);
				verifyNoInteractions(messageInfoMapper);
			}
		}
	}

	@Test
	@DisplayName("메시지 목록 조회")
	void getMessages() {
		// given
		List<Message> messageList = List.of(
			mock(Message.class),
			mock(Message.class)
		);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Message> messages = new PageImpl<>(messageList, pageable, messageList.size());

		MessageCommand.GetList command = MessageCommand.GetList.builder()
			.id("menu")
			.koText("관리")
			.enText("")
			.isActive(true)
			.build();

		when(messageRepository.getMessages(pageable, command)).thenReturn(messages);
		when(messageInfoMapper.of(messages)).thenReturn(new PageImpl<>(
			List.of(
				mock(MessageInfo.Main.class),
				mock(MessageInfo.Main.class)
			), pageable, 2
		));

		// when
		Page<MessageInfo.Main> result = messageService.getMessages(pageable, command);

		// then
		assertEquals(2, result.getContent().size());
		verify(messageRepository).getMessages(pageable, command);
		verify(messageInfoMapper).of(messages);
	}

	@Nested
	@DisplayName("메시지 생성")
	class createMessage {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("createMessage: 성공")
			void createMessage() {
                // given
				String id = "COM.MSG.HELLO";
				MessageCommand.Create command = MessageCommand.Create.builder()
                        .id(id)
                        .koText("안녕하세요")
                        .enText("Hello")
                        .isActive(true)
                        .build();

				when(messageRepository.getMessage(id)).thenReturn(null);

				ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);

				// when
                messageService.createMessage(command);

				// then
				verify(messageRepository).getMessage(id);
				verify(messageRepository).saveMessage(captor.capture());
				verifyNoMoreInteractions(messageRepository);

				Message saved = captor.getValue();
				assertAll(
						() -> assertEquals(command.getId(), saved.getId()),
						() -> assertEquals(command.getKoText(), saved.getKoText()),
						() -> assertEquals(command.getEnText(), saved.getEnText()),
						() -> assertEquals(command.getIsActive(), saved.getIsActive())
				);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("createMessage: 이미 존재하는 메시지 ID")
			void createMessage_이미_존재하는_메시지_ID() {
				// given
                String id = "COM.MSG.HELLO";

                MessageCommand.Create command = MessageCommand.Create.builder()
                        .id(id)
                        .koText("안녕하세요")
                        .enText("Hello")
                        .isActive(true)
                        .build();

				Message existing = Message.builder().id(id).build();
				when(messageRepository.getMessage(id)).thenReturn(existing);

				// when
				CustomException exception = assertThrows(CustomException.class,
						() -> messageService.createMessage(command));

                // then
                assertEquals(ErrorCode.EXISTS_ALREADY_MESSAGE.name(), exception.getCode());

                verify(messageRepository).getMessage(id);
                verifyNoMoreInteractions(messageRepository);
			}
		}
	}

	@Nested
	@DisplayName("메시지 수정")
	class UpdateMessage {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("updateMessage: 성공")
			void updateMessage() {
				// given
				String id = "COM.MSG.HELLO";

				MessageCommand.Update command = MessageCommand.Update.builder()
						.koText("안녕하세요")
						.enText("Hello")
						.isActive(true)
						.build();

				Message message = Message.builder()
						.id(id)
						.koText("기존한글")
						.enText("Old")
						.isActive(false)
						.build();

				when(messageRepository.getMessage(id)).thenReturn(message);

				// when
				messageService.updateMessage(id, command);

				// then
				verify(messageRepository).getMessage(id);
				verifyNoMoreInteractions(messageRepository);

				assertAll(
						() -> assertEquals("안녕하세요", message.getKoText()),
						() -> assertEquals("Hello", message.getEnText()),
						() -> assertTrue(message.getIsActive())
				);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("updateMessage: 존재하지 않는 메시지 ID")
			void updateMessage_존재하지_않는_메시지_ID() {
                // given
                String id = "COM.MSG.NONE";
                MessageCommand.Update command = mock(MessageCommand.Update.class);

                when(messageRepository.getMessage(id)).thenReturn(null);

                // when & then
                CustomException exception = assertThrows(CustomException.class,
                        () -> messageService.updateMessage(id, command));

                // 예외 코드 직접 비교(ENUM)
                assertEquals(ErrorCode.NOT_FOUND_MESSAGE.name(), exception.getCode());

                verify(messageRepository).getMessage(id);
				verifyNoMoreInteractions(messageRepository);

				verifyNoInteractions(command);
			}
		}
	}

	@Nested
	@DisplayName("메시지 삭제")
	class DeleteMessage {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("deleteMessage: 성공")
			void deleteMessage() {
				// given
				String id = "COM.MSG.HELLO1";
				Message message = Message.builder().id(id).build();
				when(messageRepository.getMessage(id)).thenReturn(message);

				// when
				messageService.deleteMessage(id);

				// then
				verify(messageRepository).getMessage(id);
				verify(messageRepository).deleteMessage(id);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("deleteMessage: 존재하지 않는 메시지 ID")
			void deleteMessage_존재하지_않는_메시지_ID() {
				// given
				String id = "COM.MSG.HELLO1";
				when(messageRepository.getMessage(id)).thenReturn(null);

				// when
				CustomException exception = assertThrows(CustomException.class, () -> messageService.deleteMessage(id));

				// then
				assertEquals(ErrorCode.NOT_FOUND_MESSAGE.name(), exception.getCode());
				verify(messageRepository).getMessage(id);
			}
		}
	}

	@Test
	@DisplayName("deployTranslatedMessagesAll: 성공")
	void deployTranslatedMessagesAll() throws Exception {
		// given
		String filePath = "src/test/resources";
		Field filePathField = MessageService.class.getDeclaredField("filePath");
		filePathField.setAccessible(true);
		filePathField.set(messageService, filePath);

		List<Message> messages = List.of(
			mock(Message.class),
			mock(Message.class)
		);

		when(messageRepository.getMessages(Boolean.TRUE, Sort.by(Sort.Direction.ASC, "id"))).thenReturn(messages);

		// when
		messageService.deployTranslatedMessagesAll();

		// then
		String[] files = {"\\translation_ko.json", "\\translation_en.json"};
		for (String file : files) {
			Path path = Path.of(filePath + file);
			if (!Files.exists(path)) {
				log.error("File not found: {}", path);
			}
			assertTrue(Files.exists(path));
		}

		// cleanup
		for (String file : files) {
			Path path = Path.of(filePath + file);
			Files.deleteIfExists(path);
		}

	}

}