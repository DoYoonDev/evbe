package com.evolve.evolvebe.member;

import com.evolve.evolvebe.member.application.MemberFacade;
import com.evolve.evolvebe.member.domain.MemberCommand;
import com.evolve.evolvebe.member.domain.MemberInfo;
import com.evolve.evolvebe.member.interfaces.MemberDtoMapper;
import com.evolve.evolvebe.member.interfaces.MemberRequest;
import com.evolve.evolvebe.member.interfaces.MemberResponse;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 통합 테스트 (웹 계층 + 시큐리티 어노테이션 + 컨텍스트 기동)
 * - 필터(JWT 등)는 비활성화: addFilters=false
 * - 서비스/매퍼는 MockBean 으로 대체하여 DB 의존 제거
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
public class MemberIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

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
    @DisplayName("GET /api/members - 전체 사용자 조회")
    void getAllMembers_ok() throws Exception {
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
                        .build(),
                MemberInfo.Summary.builder()
                        .id("user2")
                        .koName("김철수")
                        .enName("Kim Chul Soo")
                        .email("chulsoo@example.com")
                        .phoneNumber("010-3333-4444")
                        .isActive(false)
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
                        .createdAt(ZonedDateTime.now())
                        .updatedAt(ZonedDateTime.now())
                        .build()
        );
        MemberResponse.MemberListResponse resp =
                MemberResponse.MemberListResponse.builder().memberInfos(summaries).build();

        when(memberFacade.getAllMembers()).thenReturn(summaries);
        when(memberDtoMapper.mapToMemberList(summaries)).thenReturn(resp);

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.memberInfos").isArray())
                .andExpect(jsonPath("$.memberInfos.length()").value(3))
                .andExpect(jsonPath("$.memberInfos[0].id").value("user1"));
    }

    @Test
    @DisplayName("GET /api/members/page - 페이징 조회")
    void getMembersPaged_ok() throws Exception {
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
                        .param("column", "id")
                        .param("keyword", "user1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc") // 소문자 권장
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberInfos").exists())
                .andExpect(jsonPath("$.memberInfos.content").isArray())
                .andExpect(jsonPath("$.memberInfos.content.length()").value(3));
    }

    @Test
    @DisplayName("GET /api/members/{id} - 상세 조회")
    void getMemberById_ok() throws Exception {
        MemberInfo.Profile profile = MemberInfo.Profile.builder()
                .id("user1").koName("홍길동").enName("Hong")
                .email("user1@example.com").phoneNumber("010-1111-2222")
                .isActive(true)
                .build();

        MemberResponse.MemberDetailResponse resp =
                MemberResponse.MemberDetailResponse.builder()
                        .id(profile.getId())
                        .koName(profile.getKoName())
                        .enName(profile.getEnName())
                        .email(profile.getEmail())
                        .phoneNumber(profile.getPhoneNumber())
                        .isActive(profile.getIsActive())
                        .build();

        when(memberFacade.getMemberDetail("user1")).thenReturn(profile);
        when(memberDtoMapper.toDetailResponse(profile)).thenReturn(resp);

        mockMvc.perform(get("/api/members/{id}", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user1"))
                .andExpect(jsonPath("$.koName").value("홍길동"))
                .andExpect(jsonPath("$.enName").value("Hong"))
                .andExpect(jsonPath("$.email").value("user1@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("010-1111-2222"))
                .andExpect(jsonPath("$.isActive").value(true));
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

        when(memberDtoMapper.toCommand(any(MemberRequest.Create.class))).thenReturn(createCmd);
        when(memberFacade.createMember(any(MemberCommand.Create.class))).thenReturn(createdProfile);

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
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("PUT /api/members/{id} - 사용자 수정")
    void updateMember_ok() throws Exception {
        String id = "user1";

        MemberRequest.Update req = MemberRequest.Update.builder()
                .koName("김철수").enName("Kim Chul Soo")
                .email("chulsoo@example.com").phoneNumber("010-0000-0000")
                .isActive(true)
                .build();

        MemberCommand.Update cmd = MemberCommand.Update.builder()
                .id(id).koName("김철수").enName("Kim Chul Soo")
                .email("chulsoo@example.com").phoneNumber("010-0000-0000")
                .isActive(true)
                .build();

        MemberInfo.Profile updated = MemberInfo.Profile.builder()
                .id(id).koName("김철수").enName("Kim Chul Soo")
                .email("chulsoo@example.com").phoneNumber("010-0000-0000")
                .isActive(true)
                .build();

        MemberResponse.MemberUpdateResponse resp =
                MemberResponse.MemberUpdateResponse.builder()
                        .id(id).koName("김철수").enName("Kim Chul Soo")
                        .email("chulsoo@example.com").phoneNumber("010-0000-0000")
                        .isActive(true)
                        .build();

        when(memberDtoMapper.toCommand(eq(id), any(MemberRequest.Update.class))).thenReturn(cmd);
        when(memberFacade.updateMember(any(MemberCommand.Update.class))).thenReturn(updated);
        when(memberDtoMapper.toUpdateResponse(any(MemberInfo.Profile.class))).thenReturn(resp);

        mockMvc.perform(put("/api/members/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.koName").value("김철수"))
                .andExpect(jsonPath("$.enName").value("Kim Chul Soo"))
                .andExpect(jsonPath("$.email").value("chulsoo@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("010-0000-0000"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @DisplayName("PUT /api/members/updatePw/{id} - 패스워드 변경")
    void updatePw_ok() throws Exception {
        String id = "user1";

        MemberRequest.PwUpdate req = MemberRequest.PwUpdate.builder()
                .currentPw("oldPw").newPw("newPw").build();

        MemberCommand.PwUpdate cmd = MemberCommand.PwUpdate.builder()
                .id(id).currentPw("oldPw").newPw("newPw").build();

        MemberInfo.Profile updated = MemberInfo.Profile.builder()
                .id(id).koName("홍길동").build();

        MemberResponse.MemberUpdateResponse resp =
                MemberResponse.MemberUpdateResponse.builder()
                        .id(id).koName("홍길동").build();

        when(memberDtoMapper.toCommand(eq(id), any(MemberRequest.PwUpdate.class))).thenReturn(cmd);
        when(memberFacade.updatePw(any(MemberCommand.PwUpdate.class))).thenReturn(updated);
        when(memberDtoMapper.toUpdateResponse(any(MemberInfo.Profile.class))).thenReturn(resp);

        mockMvc.perform(put("/api/members/updatePw/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}
