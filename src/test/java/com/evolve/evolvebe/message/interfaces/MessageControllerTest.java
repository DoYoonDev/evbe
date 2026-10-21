package com.evolve.evolvebe.message.interfaces;


import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.evolve.evolvebe.global.config.SecurityConfig;
import com.evolve.evolvebe.global.security.JwtFilter;
import com.evolve.evolvebe.message.application.MessageFacade;
import com.evolve.evolvebe.message.domain.MessageCommand;
import com.evolve.evolvebe.message.domain.MessageInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = MessageController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {SecurityConfig.class, JwtFilter.class})
    }
)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private MessageFacade messageFacade;

    @MockitoBean
    private MessageDtoMapper messageDtoMapper;

    @Test
    @DisplayName("메시지 ID로 조회")
    void getMessage() throws Exception {
        // given
        String id = "COM.MSG.HELLO";
        MessageInfo.Main message = MessageInfo.Main.builder().id(id).build();
        MessageResponse.Main response = MessageResponse.Main.builder().messageInfo(message).build();

        when(messageFacade.getMessage(id)).thenReturn(message);
        when(messageDtoMapper.of(message)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/messages/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.messageInfo.id").value(id))
            .andDo(document("message/get-message",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("message")
                        .pathParameters(
                            parameterWithName("id").description("메시지 ID")
                        )
                        .responseFields(
                            fieldWithPath("messageInfo.id").description("메시지 ID"),
                            fieldWithPath("messageInfo.koText").description("한글 메시지"),
                            fieldWithPath("messageInfo.enText").description("영문 메시지"),
                            fieldWithPath("messageInfo.isActive").description("활성화 여부"),
                            fieldWithPath("messageInfo.createMemberId").description("생성 멤버 ID"),
                            fieldWithPath("messageInfo.createdAt").description("생성일"),
                            fieldWithPath("messageInfo.updateMemberId").description("수정 멤버 ID"),
                            fieldWithPath("messageInfo.updatedAt").description("최종수정일")
                        )
                        .build()
                )
            ));

    }

    @Test
    @DisplayName("메시지 목록 조회")
    void getMessages() throws Exception {
        // given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        String id = "";
        String koText = "관리";
        String enText = "";
        Boolean isActive = true;
        MessageRequest.GetList request = MessageRequest.GetList.builder()
            .id(id)
            .koText(koText)
            .enText(enText)
            .isActive(isActive)
            .build();
        MessageCommand.GetList command = MessageCommand.GetList.builder()
            .id(id)
            .koText(koText)
            .enText(enText)
            .isActive(isActive)
            .build();

        MessageInfo.Main message1 = MessageInfo.Main.builder().id("1").koText("관리1").enText("Manage1").isActive(true).build();
        MessageInfo.Main message2 = MessageInfo.Main.builder().id("2").koText("관리2").enText("Manage2").isActive(true).build();

        Page<MessageInfo.Main> messageInfos = new PageImpl<>(
            List.of(message1, message2), pageable, 2
        );

        MessageResponse.MessagePage response = MessageResponse.MessagePage.builder()
            .messageInfos(messageInfos)
            .build();

        when(messageDtoMapper.of(request)).thenReturn(command);
        when(messageFacade.getMessages(any(), any())).thenReturn(messageInfos);
        when(messageDtoMapper.of(messageInfos)).thenReturn(response);

        // when & then
        mockMvc
            .perform(
                get("/api/messages")
                    .param("id", id)
                    .param("koText", koText)
                    .param("enText", enText)
                    .param("isActive", String.valueOf(isActive))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.messageInfos").isNotEmpty())
            .andExpect(jsonPath("$.messageInfos").isMap())
            .andExpect(jsonPath("$.messageInfos.content").isArray())
            .andExpect(jsonPath("$.messageInfos.content.length()").value(2))
            .andDo(document("message/get-messages",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("message")
                        .responseFields(
                            fieldWithPath("messageInfos").description("메시지 정보"),
                            fieldWithPath("messageInfos.content").description("메시지 목록"),
                            fieldWithPath("messageInfos.content[].id").description("메시지 ID"),
                            fieldWithPath("messageInfos.content[].koText").description("메시지의 한글 텍스트"),
                            fieldWithPath("messageInfos.content[].enText").description("메시지의 영어 텍스트"),
                            fieldWithPath("messageInfos.content[].isActive").description("메시지가 활성화 상태인지 여부"),
                            fieldWithPath("messageInfos.content[].createMemberId").optional().description("메시지를 생성한 사용자의 ID (선택 사항)"),
                            fieldWithPath("messageInfos.content[].createdAt").optional().description("메시지가 생성된 시간 (선택 사항)"),
                            fieldWithPath("messageInfos.content[].updateMemberId").optional().description("메시지를 수정한 사용자의 ID (선택 사항)"),
                            fieldWithPath("messageInfos.content[].updatedAt").optional().description("메시지가 마지막으로 수정된 시간 (선택 사항)"),
                            fieldWithPath("messageInfos.pageable").description("페이지네이션 정보"),
                            fieldWithPath("messageInfos.pageable.pageNumber").description("현재 페이지 번호"),
                            fieldWithPath("messageInfos.pageable.pageSize").description("페이지 크기"),
                            fieldWithPath("messageInfos.pageable.sort").description("정렬 정보"),
                            fieldWithPath("messageInfos.pageable.sort.empty").description("정렬 정보가 비어 있는지 여부"),
                            fieldWithPath("messageInfos.pageable.sort.sorted").description("정렬이 적용되었는지 여부"),
                            fieldWithPath("messageInfos.pageable.sort.unsorted").description("정렬이 적용되지 않았는지 여부"),
                            fieldWithPath("messageInfos.pageable.offset").description("현재 페이지의 오프셋"),
                            fieldWithPath("messageInfos.pageable.unpaged").description("페이지네이션이 비활성화되었는지 여부"),
                            fieldWithPath("messageInfos.pageable.paged").description("페이지네이션이 활성화되었는지 여부"),
                            fieldWithPath("messageInfos.last").description("현재 페이지가 마지막 페이지인지 여부"),
                            fieldWithPath("messageInfos.totalPages").description("전체 페이지 수"),
                            fieldWithPath("messageInfos.totalElements").description("전체 요소 수"),
                            fieldWithPath("messageInfos.size").description("페이지당 요소 수"),
                            fieldWithPath("messageInfos.number").description("현재 페이지 번호"),
                            fieldWithPath("messageInfos.sort").description("페이지의 정렬 정보"),
                            fieldWithPath("messageInfos.sort.empty").description("정렬 정보가 비어 있는지 여부"),
                            fieldWithPath("messageInfos.sort.sorted").description("정렬이 적용되었는지 여부"),
                            fieldWithPath("messageInfos.sort.unsorted").description("정렬이 적용되지 않았는지 여부"),
                            fieldWithPath("messageInfos.numberOfElements").description("현재 페이지의 요소 수"),
                            fieldWithPath("messageInfos.first").description("현재 페이지가 첫 번째 페이지인지 여부"),
                            fieldWithPath("messageInfos.empty").description("현재 페이지가 비어 있는지 여부")
                        )
                        .build()
                )
            ));
    }

    @Test
    @DisplayName("메시지 생성")
    void createMessage() throws Exception {
        // given
        MessageRequest.Create request = mock(MessageRequest.Create.class);
        MessageCommand.Create command = mock(MessageCommand.Create.class);

        when(messageDtoMapper.of(request)).thenReturn(command);
        doNothing().when(messageFacade).createMessage(command);

        // when & then
        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(Boolean.TRUE))
            .andDo(document("message/create-message",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("message")
                        .requestFields(
                            fieldWithPath("id").description("메시지 ID"),
                            fieldWithPath("koText").description("한글 메시지"),
                            fieldWithPath("enText").description("영문 메시지"),
                            fieldWithPath("isActive").description("활성화 여부")
                        )
                        .build()
                )
            ));
    }

    @Test
    @DisplayName("메시지 수정")
    void updateMessage() throws Exception {
        // given
        String id = "COM.MSG.HELLO";
        MessageRequest.Update request = mock(MessageRequest.Update.class);
        MessageCommand.Update command = mock(MessageCommand.Update.class);

        when(messageDtoMapper.of(request)).thenReturn(command);
        doNothing().when(messageFacade).updateMessage(id, command);

        // when & then
        mockMvc.perform(put("/api/messages/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(Boolean.TRUE))
            .andDo(document("message/update-message",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("message")
                        .pathParameters(
                            parameterWithName("id").description("메시지 ID")
                        )
                        .requestFields(
                            fieldWithPath("id").description("메시지 ID"),
                            fieldWithPath("koText").description("한글 메시지"),
                            fieldWithPath("enText").description("영문 메시지"),
                            fieldWithPath("isActive").description("활성화 여부")
                        )
                        .build()
                )
            ));
    }

    @Test
    @DisplayName("메시지 삭제")
    void deleteMessage() throws Exception {
        // given
        String id = "COM.MSG.HELLO";

        doNothing().when(messageFacade).deleteMessage(id);

        // when & then
        mockMvc.perform(delete("/api/messages/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(Boolean.TRUE))
            .andDo(document("message/delete-message",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("message")
                        .pathParameters(
                            parameterWithName("id").description("메시지 ID")
                        )
                        .build()
                )
            ));
    }

    @Test
    @DisplayName("메시지 다국어 반영")
    void deployTranslatedMessagesAll() throws Exception {
        // given
        doNothing().when(messageFacade).deployTranslatedMessagesAll();

        // when & then
        mockMvc.perform(get("/api/messages/translated/deploy-all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(Boolean.TRUE))
            .andDo(document("message/deploy-translated-messages-all",
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("message")
                        .build()
                )
            ));
    }
}