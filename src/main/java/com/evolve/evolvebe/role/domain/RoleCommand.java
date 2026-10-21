package com.evolve.evolvebe.role.domain;

import com.evolve.evolvebe.member.domain.Member;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mapstruct.Mapping;

public class RoleCommand {

	@Getter
	@Setter
	@ToString
	public static class CreateRole {
		private String id;
		private String name;
		private String description;
		private Boolean isActive;
        private Integer level;

		@Mapping(target = "createMemberId", ignore = true)
		@Mapping(target = "updateMemberId", ignore = true)
		public Role toEntity() {
			return Role.builder()
				.id(id)
				.name(name)
				.description(description)
				.isActive(isActive)
                .level(level)
				.build();
		}
	}

	@Getter
	@Setter
	@ToString
	public static class UpdateRole {
		private String name;
		private String description;
		private Boolean isActive;
        private Integer level;
	}

	@Getter
	@Setter
	@ToString
	public static class AddMember {
		private String id;
		private String memberId;

		public RoleMember toEntity(Role role, Member member) {
			return RoleMember.builder()
				.role(role)
				.member(member)
				.build();
		}
	}

	@Getter
	@Setter
	@ToString
	public static class DeleteMember {
		private String id;
		private String memberId;
	}

}
