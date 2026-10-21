package com.evolve.evolvebe.role.interfaces;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class RoleRequest {

	@Getter
	@Setter
	@ToString
	public static class CreateRole {
		private String id;
		private String name;
		private String description;
		@JsonProperty("isActive")
		private Boolean isActive;
        private Integer level;
	}

	@Getter
	@Setter
	@ToString
	public static class UpdateRole {
		private String name;
		private String description;
		@JsonProperty("isActive")
		private Boolean isActive;
        private Integer level;
	}

	@Getter
	@Setter
	@ToString
	public static class UpdateRoleMenus {
		private Integer[] menuIds;
	}

}
