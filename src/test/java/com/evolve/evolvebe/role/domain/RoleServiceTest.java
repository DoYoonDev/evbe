package com.evolve.evolvebe.role.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.evolve.evolvebe.role.infra.RoleMemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import com.evolve.evolvebe.member.domain.Member;
import com.evolve.evolvebe.member.domain.MemberRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private RoleInfoMapper roleInfoMapper;

    @Mock
    private RoleMemberJpaRepository roleMemberRepository;

	@InjectMocks
	private RoleService roleService;

	@Test
	@DisplayName("역할 목록 조회")
	void getRoles() {
		// given
		List<Role> roles = List.of(mock(Role.class), mock(Role.class));
		List<RoleInfo.Main> roleInfos = List.of(mock(RoleInfo.Main.class), mock(RoleInfo.Main.class));

		when(roleRepository.getRoles()).thenReturn(roles);
		when(roleInfoMapper.from(roles)).thenReturn(roleInfos);

		// when
		List<RoleInfo.Main> result = roleService.getRoles();

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(roleInfos.size());
		verify(roleRepository).getRoles();
		verify(roleInfoMapper).from(roles);
	}

	@Test
	@DisplayName("역할 상세 조회")
	void getRole() {
		// given
		String roleId = "role001";
		Role role = mock(Role.class);
		RoleInfo.Main roleInfo = mock(RoleInfo.Main.class);

		when(roleRepository.getRole(roleId)).thenReturn(Optional.of(role));
		when(roleInfoMapper.from(role)).thenReturn(roleInfo);

		// when
		RoleInfo.Main result = roleService.getRole(roleId);

		// then
		assertThat(result).isNotNull();
		verify(roleRepository).getRole(roleId);
		verify(roleInfoMapper).from(role);
	}

	@Test
	@DisplayName("역할의 메뉴 목록 조회")
	void getMenusByRole() {
		// given
		String roleId = "role001";
		List<RoleMenu> roleMenus = List.of(mock(RoleMenu.class), mock(RoleMenu.class));
		List<RoleInfo.Menu> roleMenuInfos = List.of(mock(RoleInfo.Menu.class), mock(RoleInfo.Menu.class));

		when(roleRepository.getRoleMenus(roleId)).thenReturn(roleMenus);
		when(roleInfoMapper.mapToMenuInfoList(roleMenus)).thenReturn(roleMenuInfos);

		// when
		List<RoleInfo.Menu> result = roleService.getRoleMenus(roleId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(roleMenuInfos.size());
		verify(roleRepository).getRoleMenus(roleId);
	}

	@Test
	@DisplayName("역할 생성 성공")
	void createRole() {
		// given
		RoleCommand.CreateRole command = mock(RoleCommand.CreateRole.class);

		// when
		roleService.createRole(command);

		// then
		verify(roleRepository).createRole(command.toEntity());
	}

	@Nested
	@DisplayName("역할 수정")
	class UpdateRole {
		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("역할 수정 성공")
			void updateRole() {
				// given
				String roleId = "role001";
				RoleCommand.UpdateRole command = new RoleCommand.UpdateRole();
				command.setName("Updated Role");
				command.setDescription("Updated Description");
				command.setIsActive(true);
                command.setLevel(100);

				Role role = mock(Role.class);
				when(roleRepository.getRole(roleId)).thenReturn(Optional.of(role));

				// when
				roleService.updateRole(command, roleId);

				// then
				verify(roleRepository).getRole(roleId);
				verify(role).update(command.getName(), command.getDescription(), command.getIsActive(), command.getLevel());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("역할 수정 실패 - 역할 없음")
			void updateRole_NotFound() {
				// given
				String roleId = "role001";
				RoleCommand.UpdateRole command = new RoleCommand.UpdateRole();

				when(roleRepository.getRole(roleId)).thenReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> roleService.updateRole(command, roleId))
					.isInstanceOf(CustomException.class)
					.hasMessage(ErrorCode.NOT_FOUND_ROLE.getMessage());

				verify(roleRepository).getRole(roleId);
			}
		}
	}

	@Nested
	@DisplayName("역할 삭제")
	class DeleteRole {
		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("역할 삭제 성공")
			void deleteRole() {
				// given
				String roleId = "role001";
				Role role = mock(Role.class);

				when(roleRepository.getRole(roleId)).thenReturn(Optional.of(role));

				// when
				roleService.deleteRole(roleId);

				// then
				verify(roleRepository).getRole(roleId);
				verify(roleRepository).deleteRole(role);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("역할 삭제 실패 - 역할 없음")
			void deleteRole_NotFound() {
				// given
				String roleId = "role001";

				when(roleRepository.getRole(roleId)).thenReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> roleService.deleteRole(roleId))
					.isInstanceOf(CustomException.class)
					.hasMessage(ErrorCode.NOT_FOUND_ROLE.getMessage());

				verify(roleRepository).getRole(roleId);
			}
		}
	}

	@Nested
	@DisplayName("역할 내 멤버 추가")
	class AddMemberToRole {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("역할 내 멤버 추가 성공")
			void addMemberToRole() {
				// given
				RoleCommand.AddMember command = new RoleCommand.AddMember();
				command.setId("role001");
				command.setMemberId("member001");

				Role role = Role.create(
					"role001",
					"관리자",
					"관리자 권한",
					true,
                    100
				);
				Member member = Member.create(
						"member001",
						"사용자",
						"",
						"",
						"",
						"password",
						true,
						false
				);

                when(roleRepository.getRole(command.getId())).thenReturn(Optional.of(role));
                when(memberRepository.getMember(command.getMemberId())).thenReturn(Optional.of(member));
                when(roleMemberRepository.existsByRoleIdAndMemberId(command.getId(), command.getMemberId()))
                        .thenReturn(false);

                ArgumentCaptor<RoleMember> captor = ArgumentCaptor.forClass(RoleMember.class);

				// when
				roleService.addMemberToRole(command);

				// then
				verify(roleRepository).addMemberToRole(captor.capture());
				RoleMember capturedRoleMember = captor.getValue();

				assertEquals(role, capturedRoleMember.getRole());
				assertEquals(member, capturedRoleMember.getMember());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("역할 내 멤버 추가 실패 - 역할 없음")
			void addMemberToRole_RoleNotFound() {
				// given
				RoleCommand.AddMember command = new RoleCommand.AddMember();
				command.setId("role001");
				command.setMemberId("member001");

				when(roleRepository.getRole(command.getId())).thenReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> roleService.addMemberToRole(command))
					.isInstanceOf(CustomException.class)
					.hasMessage(ErrorCode.NOT_FOUND_ROLE.getMessage());

				verify(roleRepository).getRole(command.getId());
				verifyNoMoreInteractions(roleRepository);
			}

			@Test
			@DisplayName("역할 내 멤버 추가 실패 - 멤버 없음")
			void addMemberToRole_MemberNotFound() {
				// given
				RoleCommand.AddMember command = new RoleCommand.AddMember();
				command.setId("role001");
				command.setMemberId("member001");

				Role role = Role.create(
					"role001",
					"관리자",
					"관리자 권한",
					true,
                    100
				);

				when(roleRepository.getRole(command.getId())).thenReturn(Optional.of(role));
				when(memberRepository.getMember(command.getMemberId())).thenReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> roleService.addMemberToRole(command))
					.isInstanceOf(CustomException.class)
					.hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());

				verify(roleRepository).getRole(command.getId());
				verify(memberRepository).getMember(command.getMemberId());
			}
		}
	}

	@Nested
	@DisplayName("역할 내 멤버 삭제")
	class DeleteMemberFromRole {
		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("역할 내 멤버 삭제 성공")
			void deleteMemberFromRole() {
				// given
				RoleCommand.DeleteMember command = new RoleCommand.DeleteMember();
				command.setMemberId("member001");
				command.setId("role001");

				RoleMember roleMember = new RoleMember(
					Role.create("role001", "관리자", "관리자 권한", true, 100),
					Member.create(
						"member001",
						"사용자",
						"",
						"",
						"",
						"password",
						true,
							false
					)
				);

				when(roleRepository.getRoleMember(command.getId(), command.getMemberId()))
					.thenReturn(Optional.of(roleMember));

				// when
				roleService.deleteMemberFromRole(command);

				// then
				verify(roleRepository).getRoleMember(command.getId(), command.getMemberId());
				verify(roleRepository).deleteMemberFromRole(argThat(rm ->
					rm.getRole().getId().equals("role001") && rm.getMember().getId().equals("member001")
				));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("역할 내 멤버 삭제 실패 - 역할 내 유저 없음")
			void deleteMemberFromRole_NotFoundRoleMember() {
				// given
				RoleCommand.DeleteMember command = new RoleCommand.DeleteMember();
				command.setMemberId("member001");
				command.setId("role001");

				when(roleRepository.getRoleMember(command.getId(), command.getMemberId())).thenReturn(
					Optional.empty());

				// when & then
				assertThatThrownBy(() -> roleService.deleteMemberFromRole(command))
					.isInstanceOf(CustomException.class)
					.hasMessage(ErrorCode.NOT_FOUND_ROLE_MEMBER.getMessage());

				verify(roleRepository).getRoleMember(command.getId(), command.getMemberId());
			}
		}
	}

}
