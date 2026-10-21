package com.evolve.evolvebe.member.interfaces;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.evolve.evolvebe.global.config.SecurityConfig;
import com.evolve.evolvebe.global.security.JwtFilter;
import com.evolve.evolvebe.member.application.MemberFacade;
import com.evolve.evolvebe.member.domain.MemberCommand;
import com.evolve.evolvebe.member.domain.MemberInfo;
import com.evolve.evolvebe.role.application.RoleFacade;
import com.evolve.evolvebe.role.domain.RoleCommand;
import com.evolve.evolvebe.role.interfaces.RoleDtoMapper;
import com.evolve.evolvebe.terms.application.TermsFacade;
import com.evolve.evolvebe.terms.domain.TermsCommand;
import com.evolve.evolvebe.terms.domain.TermsInfo;
import com.evolve.evolvebe.terms.interfaces.TermsDtoMapper;
import com.evolve.evolvebe.terms.interfaces.TermsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MemberController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class, JwtFilter.class}
                )
        }
)
@AutoConfigureRestDocs
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private MemberFacade memberFacade;

    @MockitoBean
    private MemberDtoMapper memberDtoMapper;

    @MockitoBean
    private RoleDtoMapper roleDtoMapper;

    @MockitoBean
    private RoleFacade roleFacade;

    @MockitoBean
    private TermsDtoMapper termsDtoMapper;

    @MockitoBean
    private TermsFacade termsFacade;

    @Test
    @DisplayName("전체 사용자 조회")
    void getAllMembers() throws Exception {
        List<MemberInfo.Summary> summaries = List.of(
                MemberInfo.Summary.builder()
                        .id("user1")
                        .koName("홍길동")
                        .enName("Hong Gil Dong")
                        .email("hong@example.com")
                        .phoneNumber("010-1111-2222")
                        .isActive(true)
                        .mustChangePassword(false)
                        .createdAt(ZonedDateTime.now())
                        .updatedAt(ZonedDateTime.now())
                        .build(),
                MemberInfo.Summary.builder()
                        .id("user2")
                        .koName("김철수")
                        .enName("Kim Chul Soo")
                        .email("chulsoo@example.com")
                        .phoneNumber("010-3333-4444")
                        .isActive(false)
                        .mustChangePassword(false)
                        .createdAt(ZonedDateTime.now())
                        .updatedAt(ZonedDateTime.now())
                        .build(),
                MemberInfo.Summary.builder()
                        .id("user3")
                        .koName("이영희")
                        .enName("Lee Young Hee")
                        .email("younghee@example.com")
                        .phoneNumber("010-5555-6666")
                        .isActive(true)
                        .mustChangePassword(false)
                        .createdAt(ZonedDateTime.now())
                        .updatedAt(ZonedDateTime.now())
                        .build()
        );
        MemberResponse.MemberListResponse response =
                MemberResponse.MemberListResponse.builder().memberInfos(summaries).build();

        when(memberFacade.getAllMembers()).thenReturn(summaries);
        when(memberDtoMapper.mapToMemberList(summaries)).thenReturn(response);

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberInfos").isArray())
                .andExpect(jsonPath("$.memberInfos.length()").value(3))
                .andExpect(jsonPath("$.memberInfos[0].id").value("user1"))
                .andDo(document("member/get-all",
                        resource(ResourceSnippetParameters.builder()
                                .tag("member")
                                .responseFields(
                                        fieldWithPath("memberInfos").description("회원 요약 목록"),
                                        fieldWithPath("memberInfos[].id").description("사용자 ID"),
                                        fieldWithPath("memberInfos[].koName").optional().description("한글 이름"),
                                        fieldWithPath("memberInfos[].enName").optional().description("영문 이름"),
                                        fieldWithPath("memberInfos[].email").optional().description("이메일"),
                                        fieldWithPath("memberInfos[].phoneNumber").optional().description("전화번호"),
                                        fieldWithPath("memberInfos[].isActive").optional().description("활성 여부"),
                                        fieldWithPath("memberInfos[].mustChangePassword").optional().description("최초 로그인 비밀번호 변경 필요"),
                                        fieldWithPath("memberInfos[].createdAt").optional().description("생성일"),
                                        fieldWithPath("memberInfos[].updatedAt").optional().description("수정일")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("전체 사용자 조회 (페이징)")
    void getMembersPaged() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MemberInfo.Summary> memberPage = new PageImpl<>(
                List.of(
                        MemberInfo.Summary.builder()
                                .id("user1").koName("홍길동").enName("Hong Gil Dong")
                                .email("hong@example.com").phoneNumber("010-1111-2222")
                                .isActive(true).mustChangePassword(false)
                                .createdAt(ZonedDateTime.now()).updatedAt(ZonedDateTime.now()).build(),
                        MemberInfo.Summary.builder()
                                .id("user2").koName("김철수").enName("Kim Chul Soo")
                                .email("chulsoo@example.com").phoneNumber("010-3333-4444")
                                .isActive(false).mustChangePassword(false)
                                .createdAt(ZonedDateTime.now()).updatedAt(ZonedDateTime.now()).build(),
                        MemberInfo.Summary.builder()
                                .id("user3").koName("이영희").enName("Lee Young Hee")
                                .email("younghee@example.com").phoneNumber("010-5555-6666")
                                .isActive(true).mustChangePassword(false)
                                .createdAt(ZonedDateTime.now()).updatedAt(ZonedDateTime.now()).build()
                ),
                pageable,
                3
        );

        MemberResponse.MemberPageResponse response =
                MemberResponse.MemberPageResponse.builder()
                        .memberInfos(memberPage)
                        .build();

        when(memberFacade.getAllMembersPaged(eq("user1"), eq("id"), any(Pageable.class)))
                .thenReturn(memberPage);
        when(memberDtoMapper.mapToMemberPage(memberPage)).thenReturn(response);

        mockMvc.perform(get("/api/members/page")
                        .param("page", "0")
                        .param("size", "10")
                        .param("column", "id")
                        .param("keyword", "user1")
                        .param("sort", "createdAt,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberInfos").exists())
                .andExpect(jsonPath("$.memberInfos.content").isArray())
                .andExpect(jsonPath("$.memberInfos.content.length()").value(3))
                .andExpect(jsonPath("$.memberInfos.content[0].id").value("user1"))
                .andDo(document("member/get-page",
                        resource(ResourceSnippetParameters.builder()
                                .tag("member")
                                .queryParameters(
                                        parameterWithName("column").description("조회할 항목").optional(),
                                        parameterWithName("keyword").description("조회 키워드").optional(),
                                        parameterWithName("page").description("페이지").optional(),
                                        parameterWithName("size").description("페이지 크기").optional(),
                                        parameterWithName("sort").description("정렬").optional()
                                )
                                .responseFields(
                                        fieldWithPath("memberInfos.content").description("멤버 목록"),
                                        fieldWithPath("memberInfos.content[].id").description("사용자 ID"),
                                        fieldWithPath("memberInfos.content[].koName").description("이름(한글)"),
                                        fieldWithPath("memberInfos.content[].enName").description("이름(영문)"),
                                        fieldWithPath("memberInfos.content[].email").description("이메일"),
                                        fieldWithPath("memberInfos.content[].phoneNumber").description("전화번호"),
                                        fieldWithPath("memberInfos.content[].isActive").description("활성화 여부"),
                                        fieldWithPath("memberInfos.content[].mustChangePassword").description("비밀번호 변경 필요 여부"),
                                        fieldWithPath("memberInfos.content[].createdAt").description("생성일"),
                                        fieldWithPath("memberInfos.content[].updatedAt").description("최종 수정일"),

                                        fieldWithPath("memberInfos.pageable").description("페이징 정보"),
                                        fieldWithPath("memberInfos.pageable.pageNumber").description("현재 페이지 번호"),
                                        fieldWithPath("memberInfos.pageable.pageSize").description("페이지 크기"),
                                        fieldWithPath("memberInfos.pageable.offset").description("오프셋"),
                                        fieldWithPath("memberInfos.pageable.paged").description("페이징 여부"),
                                        fieldWithPath("memberInfos.pageable.unpaged").description("페이징 미적용 여부"),

                                        fieldWithPath("memberInfos.totalPages").description("총 페이지 수"),
                                        fieldWithPath("memberInfos.totalElements").description("총 데이터 수"),
                                        fieldWithPath("memberInfos.size").description("현재 페이지 크기"),
                                        fieldWithPath("memberInfos.number").description("현재 페이지 번호"),
                                        fieldWithPath("memberInfos.numberOfElements").description("현재 페이지의 데이터 수"),
                                        fieldWithPath("memberInfos.first").description("첫 번째 페이지 여부"),
                                        fieldWithPath("memberInfos.last").description("마지막 페이지 여부"),
                                        fieldWithPath("memberInfos.empty").description("페이지 비어 있는지 여부"),

                                        subsectionWithPath("memberInfos.pageable.sort").description("정렬 정보(empty/sorted/unsorted)"),
                                        subsectionWithPath("memberInfos.sort").description("현재 페이지 정렬 정보(empty/sorted/unsorted)")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 ID 중복체크")
    void checkDuplicateUserId() throws Exception {
        var found = java.util.List.of(
                MemberInfo.Summary.builder().id("user1").build(),
                MemberInfo.Summary.builder().id("user2").build(),
                MemberInfo.Summary.builder().id("user3").build()
        );

        given(memberFacade.searchMembersById("user1")).willReturn(found);

        mockMvc.perform(get("/api/members/chkId/{userId}", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @DisplayName("사용자 상세 조회")
    void getMemberById() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        MemberInfo.Profile profile = MemberInfo.Profile.builder()
                .id("user1")
                .koName("홍길동")
                .enName("Hong")
                .email("user1@example.com")
                .phoneNumber("010-3333-4444")
                .isActive(true)
                .mustChangePassword(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        MemberResponse.MemberDetailResponse response =
                MemberResponse.MemberDetailResponse.builder()
                        .id(profile.getId())
                        .koName(profile.getKoName())
                        .enName(profile.getEnName())
                        .email(profile.getEmail())
                        .phoneNumber(profile.getPhoneNumber())
                        .mustChangePassword(profile.getMustChangePassword())
                        .isActive(profile.getIsActive())
                        .createdAt(profile.getCreatedAt())
                        .updatedAt(profile.getUpdatedAt())
                        .build();

        when(memberFacade.getMemberDetail("user1")).thenReturn(profile);
        when(memberDtoMapper.toDetailResponse(profile)).thenReturn(response);

        mockMvc.perform(get("/api/members/{id}", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user1"))
                .andExpect(jsonPath("$.koName").value("홍길동"))
                .andExpect(jsonPath("$.enName").value("Hong"))
                .andExpect(jsonPath("$.email").value("user1@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("010-3333-4444"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.mustChangePassword").value(false))
                .andDo(document("member/get-detail",
                        resource(ResourceSnippetParameters.builder()
                                .tag("member")
                                .pathParameters(
                                        parameterWithName("id").description("사용자 ID")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("사용자 ID"),
                                        fieldWithPath("koName").description("이름(한글)"),
                                        fieldWithPath("enName").description("이름(영어)"),
                                        fieldWithPath("email").optional().description("이메일"),
                                        fieldWithPath("phoneNumber").optional().description("휴대폰 번호"),
                                        fieldWithPath("isActive").description("활성 여부"),
                                        fieldWithPath("mustChangePassword").description("비밀번호 초기화 여부"),
                                        fieldWithPath("createdAt").description("생성일"),
                                        fieldWithPath("updatedAt").description("최종 수정일")

                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("ID 유사 검색 (like)")
    void getMembersByIdLike() throws Exception {
        List<MemberInfo.Summary> summaries = List.of(
                MemberInfo.Summary.builder()
                        .id("user1")
                        .koName("홍길동")
                        .enName("Hong Gil Dong")
                        .email("hong@example.com")
                        .phoneNumber("010-1111-2222")
                        .isActive(true)
                        .createdAt(ZonedDateTime.now())
                        .updatedAt(ZonedDateTime.now())
                        .build()
        );
        MemberResponse.MemberListResponse response =
                MemberResponse.MemberListResponse.builder().memberInfos(summaries).build();

        when(memberFacade.searchMembersById("user")).thenReturn(summaries);
        when(memberDtoMapper.mapToMemberList(summaries)).thenReturn(response);

        mockMvc.perform(get("/api/members/search/{id}", "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberInfos").isArray())
                .andExpect(jsonPath("$.memberInfos[0].id").value("user1"))
                .andDo(document("member/search-like",
                        resource(ResourceSnippetParameters.builder()
                                .tag("member")
                                .pathParameters(
                                        parameterWithName("id").description("검색할 ID 패턴")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("사용자 등록")
    void createMember() throws Exception {
        // --- 요청에 termsAgreed 포함 (컨트롤러에서 참조함) ---
        var req = MemberRequest.Create.builder()
                .id("user1")
                .koName("홍길동")
                .enName("Hong")
                .phoneNumber("010-1234-1234")
                .email("test@test.com")
                .isActive(true)
                .password("pw")
                .termsAgreed(
                        MemberRequest.TermsAgreed.builder()
                                .service(true)   // 서비스 약관 동의
                                .privacy(true)   // 개인정보 동의
                                .build()
                )
                .tosId("100")
                .topId("200")
                .build();

        // --- 스텁: 회원 생성 흐름 ---
        var createCmd = MemberCommand.Create.builder()
                .id("user1")
                .koName("홍길동")
                .enName("Hong")
                .phoneNumber("010-1234-1234")
                .email("test@test.com")
                .isActive(true)
                .password("pw")
                .build();

        var createdProfile = MemberInfo.Profile.builder()
                .id("user1")
                .koName("홍길동")
                .enName("Hong")
                .phoneNumber("010-1234-1234")
                .email("test@test.com")
                .isActive(true)
                .mustChangePassword(false)
                .build();

        RoleCommand.AddMember addRoleCmd = new RoleCommand.AddMember();
        addRoleCmd.setId("100");
        addRoleCmd.setMemberId("user1");

        when(roleDtoMapper.mapToAddCommand(eq("ROLE_USER"), eq("user1"))).thenReturn(addRoleCmd);
        doNothing().when(roleFacade).addMemberToRole(any(RoleCommand.AddMember.class));

        // 4) 약관(TOS/SERVICE, TOP/PRIVACY) 조회/저장 스텁
        TermsCommand.GetTerms getTosCmd = TermsCommand.GetTerms.builder().build();
        TermsCommand.GetTerms getTopCmd = TermsCommand.GetTerms.builder().build();

        when(termsDtoMapper.fromTos(any(TermsRequest.GetTosRequest.class))).thenReturn(getTosCmd);
        when(termsDtoMapper.fromTop(any(TermsRequest.GetTopRequest.class))).thenReturn(getTopCmd);

        TermsInfo.Main tosInfo = TermsInfo.Main.builder().id("100").build();
        TermsInfo.Main topInfo = TermsInfo.Main.builder().id("200").build();

        when(termsFacade.getTos(eq(getTosCmd))).thenReturn(tosInfo);
        when(termsFacade.getTop(eq(getTopCmd))).thenReturn(topInfo);

        when(termsDtoMapper.fromTermsMember(any(TermsRequest.TermsMember.class)))
                .thenReturn(TermsCommand.GetTermsMember.builder().build());

        doNothing().when(termsFacade).createTermsMember(any(TermsCommand.GetTermsMember.class));

        // --- 요청/검증 ---
        mockMvc.perform(post("/api/members/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
                .andDo(document("member/create",
                        resource(ResourceSnippetParameters.builder()
                                .tag("member")
                                .requestFields(
                                        fieldWithPath("id").description("사용자 ID"),
                                        fieldWithPath("koName").description("이름(한글)"),
                                        fieldWithPath("enName").description("이름(영어)"),
                                        fieldWithPath("email").description("이메일"),
                                        fieldWithPath("phoneNumber").description("휴대폰 번호"),
                                        fieldWithPath("isActive").description("활성 여부"),
                                        fieldWithPath("password").description("비밀번호"),
                                        fieldWithPath("termsAgreed.service").optional().description("서비스 약관 동의 여부"),
                                        fieldWithPath("termsAgreed.privacy").optional().description("개인정보 처리방침 동의 여부"),
                                        fieldWithPath("tosId").description("서비스 약관 ID"),
                                        fieldWithPath("topId").description("개인정보 처리방침 ID")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("사용자 수정")
    void updateMember() throws Exception {
        String id = "user1";

        MemberRequest.Update req = MemberRequest.Update.builder()
                .koName("김철수")
                .enName("Kim Chul Soo")
                .email("chulsoo@example.com")
                .phoneNumber("010-0000-0000")
                .isActive(true)
                .build();

        MemberCommand.Update cmd = MemberCommand.Update.builder()
                .id(id)
                .koName("김철수")
                .enName("Kim Chul Soo")
                .email("chulsoo@example.com")
                .phoneNumber("010-0000-0000")
                .isActive(true)
                .build();

        MemberInfo.Profile updated = MemberInfo.Profile.builder()
                .id(id)
                .koName("김철수")
                .enName("Kim Chul Soo")
                .email("chulsoo@example.com")
                .phoneNumber("010-0000-0000")
                .isActive(true)
                .build();

        MemberResponse.MemberUpdateResponse resp =
                MemberResponse.MemberUpdateResponse.builder()
                        .id(id)
                        .koName("김철수")
                        .enName("Kim Chul Soo")
                        .email("chulsoo@example.com")
                        .phoneNumber("010-0000-0000")
                        .updatedAt(ZonedDateTime.now())
                        .createdAt(ZonedDateTime.now())
                        .isActive(true)
                        .build();

        when(memberDtoMapper.toCommand(eq(id), any(MemberRequest.Update.class))).thenReturn(cmd);
        when(memberFacade.updateMember(eq(cmd))).thenReturn(updated);
        when(memberDtoMapper.toUpdateResponse(any())).thenReturn(resp);

        mockMvc.perform(put("/api/members/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.koName").value("김철수"))
                .andExpect(jsonPath("$.enName").value("Kim Chul Soo"))
                .andExpect(jsonPath("$.email").value("chulsoo@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("010-0000-0000"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andDo(document("member/update",
                        resource(ResourceSnippetParameters.builder()
                                .tag("member")
                                .pathParameters(parameterWithName("id").description("사용자 ID"))
                                .requestFields(
                                        fieldWithPath("koName").optional().description("이름(한글)"),
                                        fieldWithPath("enName").optional().description("이름(영어)"),
                                        fieldWithPath("email").optional().description("이메일"),
                                        fieldWithPath("phoneNumber").optional().description("휴대폰 번호"),
                                        fieldWithPath("isActive").optional().description("활성 여부")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("사용자 ID"),
                                        fieldWithPath("koName").description("이름(한글)"),
                                        fieldWithPath("enName").description("이름(영어)"),
                                        fieldWithPath("email").description("이메일"),
                                        fieldWithPath("phoneNumber").description("휴대폰 번호"),
                                        fieldWithPath("isActive").description("활성 여부"),
                                        fieldWithPath("createdAt").optional().description("생성일"),
                                        fieldWithPath("updatedAt").optional().description("수정일")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("패스워드 변경")
    void updatePw() throws Exception {
        String id = "user1";

        MemberRequest.PwUpdate req = MemberRequest.PwUpdate.builder()
                .currentPw("oldPw")
                .newPw("newPw")
                .build();

        MemberCommand.PwUpdate cmd = MemberCommand.PwUpdate.builder()
                .id(id)
                .currentPw("oldPw")
                .newPw("newPw")
                .build();

        when(memberDtoMapper.toCommand(eq(id), any(MemberRequest.PwUpdate.class))).thenReturn(cmd);

        mockMvc.perform(put("/api/members/updatePw/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(document("member/update-password",
                        resource(ResourceSnippetParameters.builder()
                                .tag("member")
                                .pathParameters(parameterWithName("id").description("사용자 ID"))
                                .requestFields(
                                        fieldWithPath("currentPw").description("현재 비밀번호"),
                                        fieldWithPath("newPw").description("새 비밀번호")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteMember() throws Exception {
        String id = "user1";
        doNothing().when(memberFacade).deleteMember(id);

        mockMvc.perform(delete("/api/members/{id}", id))
                .andExpect(status().isNoContent())
                .andDo(document("member/delete",
                        resource(ResourceSnippetParameters.builder()
                                .tag("member")
                                .pathParameters(parameterWithName("id").description("삭제할 사용자 ID"))
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 초기화")
    void resetPassword() throws Exception {
        String id = "user1";
        doNothing().when(memberFacade).resetPassword(id);

        mockMvc.perform(post("/api/members/{id}/password/reset", id))
                .andExpect(status().isNoContent())
                .andDo(document("member/reset-password",
                        resource(ResourceSnippetParameters.builder()
                                .tag("member")
                                .pathParameters(parameterWithName("id").description("초기화 대상 사용자 ID"))
                                .build()
                        )
                ));
    }
}

