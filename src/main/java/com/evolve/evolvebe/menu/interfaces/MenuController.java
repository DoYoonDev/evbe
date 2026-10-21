package com.evolve.evolvebe.menu.interfaces;

import java.util.List;

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

import com.evolve.evolvebe.menu.application.MenuFacade;
import com.evolve.evolvebe.menu.domain.MenuCommand;
import com.evolve.evolvebe.menu.domain.MenuInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

	private final MenuFacade menuFacade;
	private final MenuDtoMapper menuDtoMapper;

	@GetMapping("/{id}")
	public ResponseEntity<MenuResponse.Main> getMenu(
		@PathVariable Integer id
	) {
		MenuInfo.Main result = menuFacade.getMenu(id);
		MenuResponse.Main response = menuDtoMapper.from(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<MenuResponse.MenuList> getMenus(
		@RequestParam(required = false) Boolean isActive
	) {
		List<MenuInfo.Main> result = menuFacade.getMenus(isActive);
		MenuResponse.MenuList response = menuDtoMapper.from(result);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<MenuResponse.Main> createMenu(
		@RequestBody MenuRequest.Create request
	) {
		MenuCommand.Create command = menuDtoMapper.from(request);
		MenuInfo.Main result = menuFacade.createMenu(command);
		MenuResponse.Main response = menuDtoMapper.from(result);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> updateMenu(
		@PathVariable Integer id,
		@RequestBody MenuRequest.Update request
	) {
		MenuCommand.Update command = menuDtoMapper.from(request);
		menuFacade.updateMenu(id, command);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PutMapping("/position")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> updateMenuPosition(
		@RequestBody MenuRequest.UpdatePosition request
	) {
		MenuCommand.UpdatePosition command = menuDtoMapper.from(request);
		menuFacade.updateMenuPositions(command);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> deleteMenu(
		@PathVariable Integer id
	) {
		menuFacade.deleteMenu(id);
		return ResponseEntity.ok(Boolean.TRUE);
	}
}
