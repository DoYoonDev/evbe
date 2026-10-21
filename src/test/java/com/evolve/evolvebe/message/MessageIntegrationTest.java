package com.evolve.evolvebe.message;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import com.evolve.evolvebe.message.application.MessageFacade;
import com.evolve.evolvebe.message.domain.Message;
import com.evolve.evolvebe.message.domain.MessageCommand;
import com.evolve.evolvebe.message.domain.MessageInfo;
import com.evolve.evolvebe.message.domain.MessageRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MessageIntegrationTest {

	@Autowired
	private MessageFacade messageFacade;

	@Autowired
	private MessageRepository messageRepository;

    @BeforeEach
    void setUpSecurity() {
        var context = SecurityContextHolder.createEmptyContext();
        var auth = new UsernamePasswordAuthenticationToken(
                "member001", "N/A", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

	@Value("${message.path}")
	private String filePath;

	@Nested
	@DisplayName("메시지 ID로 조회")
	class GetMessage {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("메시지 ID로 조회")
			void getMessage() {
				// given
				String id = "menu.home";

				// when
				MessageInfo.Main result = messageFacade.getMessage(id);

				// then
				assertEquals(id, result.getId());
				assertTrue(result.getIsActive());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("getMessage: 존재하지 않는 메시지 ID")
			void getMessage_존재하지_않는_메시지_ID() {
				// given
				String id = "not.exist";

				// when
				MessageInfo.Main result = messageFacade.getMessage(id);

				// then
				assertNull(result);
			}
		}
	}

	@Test
	@DisplayName("메시지 목록 조회")
	void getMessages() {
		// given
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
		MessageCommand.GetList command = MessageCommand.GetList.builder()
			.id("")
			.koText("관리")
			.enText("")
			.isActive(true)
			.build();

		// when
		Page<MessageInfo.Main> result = messageFacade.getMessages(pageable, command);

		// then
		assertEquals(10, result.getContent().size());
	}

	@Nested
	@DisplayName("메시지 생성")
	class createMessage {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("메시지 생성")
			void createMessage() {
				// given
				String id = "test.id";
				MessageCommand.Create command = MessageCommand.Create.builder()
					.id(id)
					.koText("테스트")
					.enText("test")
					.isActive(true)
					.build();

				// when
				messageFacade.createMessage(command);

				// then
				Message result = messageRepository.getMessage(id);
				assertNotNull(result);
				assertEquals(id, result.getId());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("createMessage: 이미 존재하는 ID")
			void createMessage_이미_존재하는_ID() {
				// given
				String id = "menu.home";
				MessageCommand.Create command = MessageCommand.Create.builder()
					.id(id)
					.koText("테스트")
					.enText("test")
					.isActive(true)
					.build();

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					messageFacade.createMessage(command)
				);

				// then
				assertEquals(ErrorCode.EXISTS_ALREADY_MESSAGE.name(), exception.getCode());
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
			@DisplayName("메시지 수정")
			void updateMessage() {
				// given
				String id = "menu.home";
				String updateId = "menu.home";
				MessageCommand.Update command = MessageCommand.Update.builder()
					.koText("홈 수정")
					.enText("home update")
					.isActive(false)
					.build();

				// when
				messageFacade.updateMessage(id, command);

				// then
				Message result = messageRepository.getMessage(updateId);
				assertNotNull(result);
				assertEquals(updateId, result.getId());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("updateMessage: 존재하지 않는 ID")
			void updateMessage_존재하지_않는_ID() {
				// given
				String id = "not.exist";
				MessageCommand.Update command = MessageCommand.Update.builder()
					.koText("테스트")
					.enText("test")
					.isActive(true)
					.build();

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					messageFacade.updateMessage(id, command)
				);

				// then
				assertEquals(ErrorCode.NOT_FOUND_MESSAGE.name(), exception.getCode());
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
			@DisplayName("메시지 삭제")
			void deleteMessage() {
				// given
				String id = "menu.home";

				// when
				messageFacade.deleteMessage(id);

				// then
				assertNull(messageRepository.getMessage(id));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("deleteMessage: 존재하지 않는 ID")
			void deleteMessage_존재하지_않는_ID() {
				// given
				String id = "not.exist";

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					messageFacade.deleteMessage(id)
				);

				// then
				assertEquals(ErrorCode.NOT_FOUND_MESSAGE.name(), exception.getCode());
			}
		}
	}

	@Test
	@DisplayName("메시지 다국어 반영")
	void deployTranslatedMessagesAll() throws Exception {
		// when
		messageFacade.deployTranslatedMessagesAll();

		// then
        Path ko = Path.of(filePath, "translation_ko.json");
        Path en = Path.of(filePath, "translation_en.json");

        assertTrue(Files.exists(ko), () -> "File not found: " + ko);
        assertTrue(Files.exists(en), () -> "File not found: " + en);

        Files.deleteIfExists(ko);
        Files.deleteIfExists(en);
	}
}
