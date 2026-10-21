package com.evolve.evolvebe.member.application;

import com.evolve.evolvebe.member.domain.MemberCommand;
import com.evolve.evolvebe.member.domain.MemberInfo;
import com.evolve.evolvebe.member.domain.MemberService;
import com.evolve.evolvebe.role.domain.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberFacadeTest {

    @Mock
    private MemberService memberService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private MemberFacade memberFacade;

    @Test
    @DisplayName("사용자 단건 조회 (Main)")
    void getMember() {
        // given
        String userId = "user1";
        MemberInfo.Main main = mock(MemberInfo.Main.class);
        when(memberService.getMember(userId)).thenReturn(main);

        // when
        MemberInfo.Main result = memberFacade.getMember(userId);

        // then
        assertSame(main, result);
        verify(memberService).getMember(userId);
    }

    @Test
    @DisplayName("사용자 상세 조회 (Profile)")
    void getMemberDetail() {
        // given
        String userId = "user1";
        MemberInfo.Profile profile = mock(MemberInfo.Profile.class);
        when(memberService.getMemberDetail(userId)).thenReturn(profile);

        // when
        MemberInfo.Profile result = memberFacade.getMemberDetail(userId);

        // then
        assertSame(profile, result);
        verify(memberService).getMemberDetail(userId);
    }

    @Test
    @DisplayName("전체 사용자 목록 조회")
    void getAllMembers() {
        // given
        List<MemberInfo.Summary> summaries = List.of(mock(MemberInfo.Summary.class));
        when(memberService.getAllMembers()).thenReturn(summaries);

        // when
        List<MemberInfo.Summary> result = memberFacade.getAllMembers();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(memberService).getAllMembers();
    }

    @Test
    @DisplayName("전체 사용자 목록 조회 (Page)")
    void getAllMembersPaged() {
        // given
        String keyword = "user1";
        String column = "id";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<MemberInfo.Summary> page = new PageImpl<>(List.of(mock(MemberInfo.Summary.class)), pageable, 1);

        // facade는 (keyword, column, pageable) → service에는 (column, keyword, pageable)로 위임
        when(memberService.getAllMembersPaged(keyword, column, pageable)).thenReturn(page);

        // when
        Page<MemberInfo.Summary> result = memberFacade.getAllMembersPaged(keyword, column, pageable);

        // then
        assertEquals(1, result.getTotalElements());
        verify(memberService).getAllMembersPaged(keyword, column, pageable);
    }

    @Test
    @DisplayName("ID 유사 검색")
    void searchMembersById() {
        // given
        String idKeyword = "user";
        List<MemberInfo.Summary> summaries = List.of(mock(MemberInfo.Summary.class));
        when(memberService.getMembersByIdLike(idKeyword)).thenReturn(summaries);

        // when
        List<MemberInfo.Summary> result = memberFacade.searchMembersById(idKeyword);

        // then
        assertNotNull(result);
        verify(memberService).getMembersByIdLike(idKeyword);
    }

    @Test
    @DisplayName("사용자 등록")
    void createMember() {
        // given
        MemberCommand.Create command = mock(MemberCommand.Create.class);
        MemberInfo.Profile profile = mock(MemberInfo.Profile.class);
        when(memberService.createMember(command)).thenReturn(profile);

        // when
        MemberInfo.Profile result = memberFacade.createMember(command);

        // then
        assertSame(profile, result);
        verify(memberService).createMember(command);
    }

    @Test
    @DisplayName("사용자 수정")
    void updateMember() {
        // given
        MemberCommand.Update command = mock(MemberCommand.Update.class);
        MemberInfo.Profile profile = mock(MemberInfo.Profile.class);
        when(memberService.updateMember(command)).thenReturn(profile);

        // when
        MemberInfo.Profile result = memberFacade.updateMember(command);

        // then
        assertSame(profile, result);
        verify(memberService).updateMember(command);
    }

    @Test
    @DisplayName("패스워드 변경")
    void updatePw() {
        // given
        MemberCommand.PwUpdate command = mock(MemberCommand.PwUpdate.class);
        MemberInfo.Profile profile = mock(MemberInfo.Profile.class);
        when(memberService.updatePassword(command)).thenReturn(profile);

        // when
        MemberInfo.Profile result = memberFacade.updatePw(command);

        // then
        assertSame(profile, result);
        verify(memberService).updatePassword(command);
    }

    @Test
    @DisplayName("사용자 삭제 (역할 전체 삭제 후 회원 삭제)")
    void deleteMember() {
        // given
        String id = "user1";

        // when
        memberFacade.deleteMember(id);

        // then
        verify(roleService).removeAllRolesOfMember(id);
        verify(memberService).deleteMember(id);
    }

    @Test
    @DisplayName("패스워드 초기화")
    void resetPassword() {
        // given
        String id = "user1";

        // when
        memberFacade.resetPassword(id);

        // then
        verify(memberService).resetPassword(id);
    }
}
