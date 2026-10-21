package com.evolve.evolvebe.role;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import com.evolve.evolvebe.role.application.RoleFacade;
import com.evolve.evolvebe.role.domain.Role;
import com.evolve.evolvebe.role.domain.RoleCommand;
import com.evolve.evolvebe.role.domain.RoleInfo;
import com.evolve.evolvebe.role.domain.RoleMember;
import com.evolve.evolvebe.role.domain.RoleRepository;
import com.evolve.evolvebe.role.domain.RoleService;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RoleIntegrationTest {

	@Autowired
	private RoleFacade roleFacade;

	@Autowired
	private RoleService roleService;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	@DisplayName("역할 목록 조회 성공")
	void getRoles() {
		// when
		List<RoleInfo.Main> result = roleFacade.getRoles();

		// then
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	@Nested
	@DisplayName("역할 상세 조회")
	class GetRole {

		@Test
		@DisplayName("성공 케이스")
		void getRole_Success() {
			// given
			String roleId = "ROLE_ADMIN";

			// when
			RoleInfo.Main result = roleFacade.getRole(roleId);

			// then
			assertNotNull(result);
			assertEquals(roleId, result.getId());
		}

		@Test
		@DisplayName("실패 케이스 - 역할 없음")
		void getRole_Fail() {
			// given
			String roleId = "ROLE_NOT_EXIST";

			// when
			CustomException exception = assertThrows(CustomException.class, () ->
				roleFacade.getRole(roleId)
			);

			// then
			assertEquals(ErrorCode.NOT_FOUND_ROLE.name(), exception.getCode());
		}
	}

	@Nested
	@DisplayName("역할 생성")
	class CreateRole {

		@Test
		@DisplayName("성공 케이스")
		void createRole_Success() {
			// given
			RoleCommand.CreateRole command = new RoleCommand.CreateRole();
			command.setId("ROLE_NEW");
			command.setName("New Role");

			// when
			roleFacade.createRole(command);

			// then
			Role createdRole = roleRepository.getRole("ROLE_NEW").orElseThrow();
			assertEquals("ROLE_NEW", createdRole.getId());
		}
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
				String roleId = "ROLE_ADMIN";
				RoleCommand.UpdateRole command = new RoleCommand.UpdateRole();
				command.setName("Updated Role");

				// when
				roleFacade.updateRole(command, roleId);

				// then
				Role updatedRole = roleRepository.getRole(roleId).orElseThrow();
				assertEquals("Updated Role", updatedRole.getName());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("updateRole: 역할이 존재하지 않음")
			void updateRole() {
				// given
				String roleId = "ROLE_NOT_EXIST";
				RoleCommand.UpdateRole command = new RoleCommand.UpdateRole();
				command.setName("Updated Role");

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					roleFacade.updateRole(command, roleId)
				);

				// then
				assertEquals(ErrorCode.NOT_FOUND_ROLE.name(), exception.getCode());
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
			@DisplayName("deleteRole: 성공")
			void deleteRole() {
				// given
				String roleId = "ROLE_ADMIN";

				// when
				roleFacade.deleteRole(roleId);

				// then
				assertFalse(roleRepository.getRole(roleId).isPresent());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("deleteRole: 역할이 존재하지 않음")
			void deleteRole() {
				// given
				String roleId = "ROLE_NOT_EXIST";

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					roleFacade.deleteRole(roleId)
				);

				// then
				assertEquals(ErrorCode.NOT_FOUND_ROLE.name(), exception.getCode());
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
				//given
				RoleCommand.AddMember command = new RoleCommand.AddMember();
				command.setId("ROLE_USER");
				command.setMemberId("userId");

				// when
				roleFacade.addMemberToRole(command);

				// then
				RoleMember roleMember = roleRepository.getRoleMember(
					command.getId(), command.getMemberId()
				).orElseThrow();

				assertEquals(command.getId(), roleMember.getRole().getId());
				assertEquals(command.getMemberId(), roleMember.getMember().getId());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("역할 내 멤버 추가 실패 - 역할 없음")
			void addMemberToRole_NotFoundRole() {
				// given
				RoleCommand.AddMember command = new RoleCommand.AddMember();
				command.setId("ROLE_NOT_EXIST");
				command.setMemberId("userId");

				// when & then
				assertThatThrownBy(() -> roleFacade.addMemberToRole(command))
					.isInstanceOf(CustomException.class)
					.hasMessage(ErrorCode.NOT_FOUND_ROLE.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("역할 내 멤버삭제")
	class DeleteMemberFromRole {
		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("역할 내 멤버삭제 성공")
			void deleteMemberFromRole() {
				// given
				RoleCommand.DeleteMember command = new RoleCommand.DeleteMember();
				command.setId("ROLE_ADMIN");
				command.setMemberId("userId");

				// when
				roleFacade.deleteMemberFromRole(command);

				// then
				assertFalse(roleRepository.getRoleMember(command.getId(), command.getMemberId()).isPresent());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("역할 내 멤버삭제 실패 - 역할 내 멤버없음")
			void deleteMemberFromRole_NoteFoundRoleMember() {
				// given
				RoleCommand.DeleteMember command = new RoleCommand.DeleteMember();
				command.setId("ROLE_USER");
				command.setMemberId("userId");

				// when & then
				assertThatThrownBy(() -> roleFacade.deleteMemberFromRole(command))
					.isInstanceOf(CustomException.class)
					.hasMessage(ErrorCode.NOT_FOUND_ROLE_MEMBER.getMessage());
			}
		}

	}
}
