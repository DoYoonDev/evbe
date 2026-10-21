package com.evolve.evolvebe.role.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evolve.evolvebe.role.application.RoleFacade;
import com.evolve.evolvebe.role.domain.RoleCommand;
import com.evolve.evolvebe.role.domain.RoleInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {

	private final RoleFacade roleFacade;
	private final RoleDtoMapper roleDtoMapper;

	@GetMapping
	public ResponseEntity<RoleResponse.RoleListResponse> getRoles() {
		List<RoleInfo.Main> result = roleFacade.getRoles();
		RoleResponse.RoleListResponse response = roleDtoMapper.mapToRoleList(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RoleResponse.RoleInfoResponse> getRole(
		@PathVariable("id") String id
	) {
		RoleInfo.Main result = roleFacade.getRole(id);
		RoleResponse.RoleInfoResponse response = roleDtoMapper.from(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}/menus")
	public ResponseEntity<RoleResponse.MenuListResponse> getMenusByRole(
		@PathVariable("id") String id
	) {
		List<RoleInfo.Menu> result = roleFacade.getMenusByRoleId(id);
		RoleResponse.MenuListResponse response = roleDtoMapper.mapToMenuList(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/menus")
	public ResponseEntity<RoleResponse.MenuListResponse> getMenusByRoles(
		@RequestParam(required = false) List<String> ids
	) {
		List<RoleInfo.Menu> result = roleFacade.getMenusByRoleIds(ids);
		RoleResponse.MenuListResponse response = roleDtoMapper.mapToMenuList(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}/members")
	public ResponseEntity<RoleResponse.MemberListResponse> getMembersByRole(
		@PathVariable("id") String id
	) {
		List<RoleInfo.Member> result = roleFacade.getMembersByRoleId(id);
		RoleResponse.MemberListResponse response = roleDtoMapper.mapToMemberList(result);
		return ResponseEntity.ok(response);
	}

    @GetMapping("/{id}/members/paged")
    public ResponseEntity<RoleResponse.MemberPageResponse> getMembersByRolePaged(
            @PathVariable("id") String roleId,
            @RequestParam(name = "keyword", required = false) String memberId,
            @PageableDefault(page = 0, size = 10, sort = {"memberId"}, direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<RoleInfo.Member> result = roleFacade.getMembersByRoleIdPaged(roleId, memberId, pageable);
        RoleResponse.MemberPageResponse response = roleDtoMapper.mapToMemberPage(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/endpoints")
	public ResponseEntity<RoleResponse.ApiListResponse> getApisByRole(
		@PathVariable("id") String id
	) {
		List<RoleInfo.Api> result = roleFacade.getApisByRoleId(id);
		RoleResponse.ApiListResponse response = roleDtoMapper.mapToApiList(result);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> createRole(
		@RequestBody RoleRequest.CreateRole request
	) {
		RoleCommand.CreateRole command = roleDtoMapper.from(request);
		roleFacade.createRole(command);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/{id}/members/{memberId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> addMemberToRole(
		@PathVariable String id,
		@PathVariable String memberId
	) {
		RoleCommand.AddMember command = roleDtoMapper.mapToAddCommand(id, memberId);
		roleFacade.addMemberToRole(command);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> updateRole(
		@PathVariable String id,
		@RequestBody RoleRequest.UpdateRole request
	) {
		RoleCommand.UpdateRole command = roleDtoMapper.from(request);
		roleFacade.updateRole(command, id);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PutMapping("/{id}/menus")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> updateRoleMenus(
		@PathVariable("id") String id,
		@RequestBody RoleRequest.UpdateRoleMenus request
	) {
		roleFacade.updateRoleMenus(id, request.getMenuIds());
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> deleteRole(
		@PathVariable String id
	) {
		roleFacade.deleteRole(id);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@DeleteMapping("/{id}/members/{memberId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> deleteMemberFromRole(
		@PathVariable String id,
		@PathVariable String memberId
	) {
		RoleCommand.DeleteMember command = roleDtoMapper.mapToDeleteCommand(id, memberId);
		roleFacade.deleteMemberFromRole(command);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	// 특정 멤버의 역할 목록
	@GetMapping("/{memberId}/roles")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<RoleResponse.MemberRoles> getMemberRoles(
			@PathVariable String memberId
	) {
		List<String> roles = roleFacade.getMemberRoles(memberId);
        RoleResponse.MemberRoles response = roleDtoMapper.mapToMemberRole(memberId, roles);
		return ResponseEntity.ok(response);
	}

}
