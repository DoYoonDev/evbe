package com.evolve.evolvebe.role.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.evolve.evolvebe.member.application.MemberFacade;
import com.evolve.evolvebe.role.domain.RoleCommand;
import com.evolve.evolvebe.role.domain.RoleInfo;
import com.evolve.evolvebe.role.domain.RoleService;

@ExtendWith(MockitoExtension.class)
class RoleFacadeTest {

	@Mock
	private RoleService roleService;

	@Mock
	private MemberFacade memberFacade;

	@InjectMocks
	private RoleFacade roleFacade;

	@Test
	@DisplayName("역할 목록 조회")
	void getRoles() {
		// given
		List<RoleInfo.Main> roles = List.of(mock(RoleInfo.Main.class), mock(RoleInfo.Main.class));
		when(roleService.getRoles()).thenReturn(roles);

		// when
		List<RoleInfo.Main> result = roleFacade.getRoles();

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(roles.size());
		verify(roleService).getRoles();
	}

	@Test
	@DisplayName("역할 상세 조회")
	void getRole() {
		// given
		String roleId = "role001";
		RoleInfo.Main role = mock(RoleInfo.Main.class);
		when(roleService.getRole(roleId)).thenReturn(role);

		// when
		RoleInfo.Main result = roleFacade.getRole(roleId);

		// then
		assertThat(result).isNotNull();
		verify(roleService).getRole(roleId);
	}

	@Test
	@DisplayName("역할 생성")
	void createRole() {
		// given
		RoleCommand.CreateRole command = new RoleCommand.CreateRole();
		command.setId("role001");
		command.setName("Admin");

		// when
		roleFacade.createRole(command);

		// then
		verify(roleService).createRole(command);
	}

	@Test
	@DisplayName("역할 수정")
	void updateRole() {
		// given
		String roleId = "role001";
		RoleCommand.UpdateRole command = new RoleCommand.UpdateRole();
		command.setName("Updated Role");

		// when
		roleFacade.updateRole(command, roleId);

		// then
		verify(roleService).updateRole(command, roleId);
	}

	@Test
	@DisplayName("역할 삭제 성공")
	void deleteRole() {
		// given
		String roleId = "role001";

		// when
		roleFacade.deleteRole(roleId);

		// then
		verify(roleService).deleteRole(roleId);
	}

	@Test
	@DisplayName("역할 내 멤버추가")
	void addMemberToRole() {
		// given
		RoleCommand.AddMember command = new RoleCommand.AddMember();
		command.setId("role001");
		command.setMemberId("member001");

		// when
		roleFacade.addMemberToRole(command);

		// then
		verify(roleService).addMemberToRole(command);
	}

	@Test
	@DisplayName("역할 내 멤버삭제")
	void deleteMemberFromRole() {
		// given
		RoleCommand.DeleteMember command = new RoleCommand.DeleteMember();
		command.setId("role001");
		command.setMemberId("member001");

		// when
		roleFacade.deleteMemberFromRole(command);

		// then
		verify(roleService).deleteMemberFromRole(command);
	}

}
