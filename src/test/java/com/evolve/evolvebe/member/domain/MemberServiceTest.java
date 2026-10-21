package com.evolve.evolvebe.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberInfoMapper memberInfoMapper;

    @InjectMocks
    private MemberService memberService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TemporaryPasswordGenerator generator;

    @Test
    @DisplayName("getMember: 성공")
    void getMember() {
        // given
        String id = "user1";
        Member member = Member.builder().id(id).build();
        when(memberRepository.getMember(id)).thenReturn(Optional.of(member));
        when(memberInfoMapper.mapToMain(member)).thenReturn(mock(MemberInfo.Main.class));

        // when
        MemberInfo.Main result = memberService.getMember(id);

        // then
        assertNotNull(result);
        verify(memberRepository).getMember(id);
        verify(memberInfoMapper).mapToMain(member);
    }

    @Test
    @DisplayName("getMembersByIdList: 성공")
    void getMembersByIdList() {
        // given
        List<String> ids = List.of("user1", "user2");
        List<Member> members = List.of(mock(Member.class), mock(Member.class));
        when(memberRepository.getMembersByIds(ids)).thenReturn(members);
        when(memberInfoMapper.mapToSummaryList(members)).thenReturn(List.of(mock(MemberInfo.Summary.class), mock(MemberInfo.Summary.class)));

        // when
        List<MemberInfo.Summary> result = memberService.getMembersByIdList(ids);

        // then
        assertEquals(2, result.size());
        verify(memberRepository).getMembersByIds(ids);
        verify(memberInfoMapper).mapToSummaryList(members);
    }

    @Test
    @DisplayName("getMembersByIdLike: 성공")
    void getMembersByIdLike() {
        // given
        String keyword = "user";
        List<Member> members = List.of(mock(Member.class));
        List<MemberInfo.Summary> summaries = List.of(mock(MemberInfo.Summary.class));
        when(memberRepository.getMembersByIdLike(keyword)).thenReturn(members);
        when(memberInfoMapper.mapToSummaryList(members)).thenReturn(summaries);

        // when
        List<MemberInfo.Summary> result = memberService.getMembersByIdLike(keyword);

        // then
        assertEquals(1, result.size());
        verify(memberRepository).getMembersByIdLike(keyword);
        verify(memberInfoMapper).mapToSummaryList(members);
    }

    @Test
    @DisplayName("getAllMembers: 성공")
    void getAllMembers() {
        // given
        List<Member> members = List.of(mock(Member.class), mock(Member.class));
        List<MemberInfo.Summary> summaries = List.of(mock(MemberInfo.Summary.class), mock(MemberInfo.Summary.class));
        when(memberRepository.findAll()).thenReturn(members);
        when(memberInfoMapper.mapToSummaryList(members)).thenReturn(summaries);

        // when
        List<MemberInfo.Summary> result = memberService.getAllMembers();

        // then
        assertEquals(2, result.size());
        verify(memberRepository).findAll();
        verify(memberInfoMapper).mapToSummaryList(members);
    }

    @Test
    @DisplayName("getAllMembersPaged: 성공")
    void getAllMembersPaged() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> members = new PageImpl<>(List.of(mock(Member.class)), pageable, 1);
        Page<MemberInfo.Summary> summaries = new PageImpl<>(List.of(mock(MemberInfo.Summary.class)), pageable, 1);

        when(memberRepository.findAllPaged(pageable)).thenReturn(members);
        when(memberInfoMapper.mapToSummaryPage(members)).thenReturn(summaries);

        // when
        Page<MemberInfo.Summary> result = memberService.getAllMembersPaged(null, null, pageable);

        // then
        assertEquals(1, result.getContent().size());
        verify(memberRepository).findAllPaged(pageable);
        verify(memberInfoMapper).mapToSummaryPage(members);
    }

    @Test
    @DisplayName("getMemberDetail: 성공")
    void getMemberDetail() {
        // given
        String id = "user1";
        Member member = Member.builder().id(id).build();
        when(memberRepository.getMember(id)).thenReturn(Optional.of(member));
        when(memberInfoMapper.mapToProfile(member)).thenReturn(mock(MemberInfo.Profile.class));

        // when
        MemberInfo.Profile result = memberService.getMemberDetail(id);

        // then
        assertNotNull(result);
    }

    @Test
    @DisplayName("createMember: 성공")
    void createMember() {
        // given
        String id = "user1";
        MemberCommand.Create command = mock(MemberCommand.Create.class);
        when(command.getId()).thenReturn(id);
        when(command.getKoName()).thenReturn("홍길동");
        when(command.getEnName()).thenReturn("Hong Gil-dong");
        when(command.getEmail()).thenReturn("hong@example.com");
        when(command.getPhoneNumber()).thenReturn("010-1234-5678");
        when(command.getPassword()).thenReturn("123456");

        when(memberRepository.existsById(id)).thenReturn(false);

        // 비밀번호 인코딩
        when(passwordEncoder.encode("123456")).thenReturn("ENC_123456");

        // 매퍼 스텁
        MemberInfo.Profile mapped = mock(MemberInfo.Profile.class);
        when(memberInfoMapper.mapToProfile(any(Member.class))).thenReturn(mapped);

        // when
        MemberInfo.Profile result = memberService.createMember(command);

        // then
        verify(memberRepository).existsById(id);
        verify(passwordEncoder).encode("123456");
        verify(memberRepository).saveMember(argThat(m ->
                id.equals(m.getId()) &&
                        "ENC_123456".equals(m.getPassword()) &&
                        Boolean.TRUE.equals(m.getIsActive()) &&
                        Boolean.FALSE.equals(m.getMustChangePassword())
        ));
        verify(memberInfoMapper).mapToProfile(any(Member.class));
        assertSame(mapped, result);
    }

    @Test
    @DisplayName("updateMember: 성공")
    void updateMember() {
        String id = "user1";
        MemberCommand.Update command = mock(MemberCommand.Update.class);
        when(command.getId()).thenReturn(id);
        when(command.getKoName()).thenReturn("홍길동");
        when(command.getEnName()).thenReturn("Hong Gil-dong");
        when(command.getEmail()).thenReturn("hong@example.com");
        when(command.getPhoneNumber()).thenReturn("010-1234-5678");
        when(command.getIsActive()).thenReturn(true);

        Member member = spy(Member.builder().id(id).build());
        when(memberRepository.getMember(id)).thenReturn(Optional.of(member));

        MemberInfo.Profile mapped = mock(MemberInfo.Profile.class);
        when(memberInfoMapper.mapToProfile(member)).thenReturn(mapped);

        // when
        MemberInfo.Profile result = memberService.updateMember(command);

        // then
        verify(member).updateInfo(
                "홍길동",
                "Hong Gil-dong",
                "hong@example.com",
                "010-1234-5678",
                true
        );

        verify(memberInfoMapper).mapToProfile(member);
        assertSame(mapped, result);

        verify(memberRepository, never()).saveMember(any());
    }

    @Test
    @DisplayName("updatePassword: 성공")
    void updatePassword() {
        // given
        String id = "user1";
        MemberCommand.PwUpdate command = mock(MemberCommand.PwUpdate.class);
        when(command.getId()).thenReturn(id);
        when(command.getNewPw()).thenReturn("newPw");

        Member member = Member.builder()
                .id(id)
                .password("oldEncPw")
                .build();
        when(memberRepository.getMember(id)).thenReturn(Optional.of(member));

        when(passwordEncoder.matches("newPw", "oldEncPw")).thenReturn(false);
        when(passwordEncoder.encode("newPw")).thenReturn("ENC_NEW");

        MemberInfo.Profile mapped = mock(MemberInfo.Profile.class);
        when(memberInfoMapper.mapToProfile(any(Member.class))).thenReturn(mapped);

        // when
        MemberInfo.Profile result = memberService.updatePassword(command);

        // then
        verify(passwordEncoder).matches("newPw", "oldEncPw");
        verify(passwordEncoder).encode("newPw");
        verify(memberRepository).saveMember(argThat(m -> "ENC_NEW".equals(m.getPassword())));
        verify(memberInfoMapper).mapToProfile(member);
        // 반환값까지 확인(옵션)
        assertSame(mapped, result);
    }

    @Test
    @DisplayName("deleteMember: 성공")
    void deleteMember() {
        // given
        String id = "user1";
        Member member = Member.builder().id(id).build();
        when(memberRepository.getMember(id)).thenReturn(Optional.of(member));

        // when
        memberService.deleteMember(id);

        // then
        verify(memberRepository).delete(member);
    }

    @Test
    @DisplayName("resetPassword: 성공")
    void resetPassword() {
        // given
        String id = "user1";
        Member member = Member.builder().id(id).password("oldEnc").build();

        when(memberRepository.getMember(id)).thenReturn(Optional.of(member));
        when(passwordEncoder.encode("test1234")).thenReturn("ENC_TEST1234");

        // when
        memberService.resetPassword(id);

        // then
        verify(passwordEncoder).encode("test1234");
        verify(memberRepository).resetPassword(argThat(m ->
                id.equals(m.getId()) && "ENC_TEST1234".equals(m.getPassword())
        ));
    }
}
