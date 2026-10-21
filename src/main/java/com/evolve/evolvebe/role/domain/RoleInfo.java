package com.evolve.evolvebe.role.domain;

import com.evolve.evolvebe.menu.domain.MenuInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class RoleInfo {

	@Getter
	@Builder
	@ToString
	public static class Main {
		private String id;
		private String name;
		private String description;
		private Boolean isActive;
        private Integer level;
	}

	@Getter
	@Builder
	@ToString
	public static class Menu {
		private MenuInfo.Main menu;
		private Boolean isChecked;
	}

	@Getter
	@Builder
	@ToString
	public static class Member {
		private String memberId;
		private String koName;
		private String enName;
	}

	@Getter
	@Builder
	@ToString
	public static class Api {
		private String url;
		private String method;
	}

}
