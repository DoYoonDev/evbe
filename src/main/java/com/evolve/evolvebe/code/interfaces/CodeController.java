package com.evolve.evolvebe.code.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evolve.evolvebe.code.application.CodeFacade;
import com.evolve.evolvebe.code.domain.CodeCommand;
import com.evolve.evolvebe.code.domain.CodeInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/codes")
public class CodeController {

	private final CodeFacade codeFacade;
	private final CodeDtoMapper codeDtoMapper;

	@GetMapping
	public ResponseEntity<CodeResponse.CodeListResponse> getCodes(
		@PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
		@ModelAttribute CodeRequest.GetCodeList request
	) {
		CodeCommand.GetCodeList command = codeDtoMapper.of(request);
		Page<CodeInfo.Main> result = codeFacade.getCodes(pageable, command);
		CodeResponse.CodeListResponse response = codeDtoMapper.mapToCodeList(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/code-group")
	public ResponseEntity<CodeResponse.CodeGroupListResponse> getCodeGroups(
		@PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
		@ModelAttribute CodeRequest.GetCodeGroupList request
	) {
		CodeCommand.GetCodeGroupList command = codeDtoMapper.of(request);
		Page<CodeInfo.CodeGroupInfo> result = codeFacade.getCodeGroups(pageable, command);
		CodeResponse.CodeGroupListResponse response = codeDtoMapper.mapToGroupList(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CodeResponse.CodeInfoResponse> getCode(
		@PathVariable String id
	) {
		CodeInfo.Main result = codeFacade.getCode(id);
		CodeResponse.CodeInfoResponse response = codeDtoMapper.of(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/code-group/{id}")
	public ResponseEntity<CodeResponse.CodeGroupInfoResponse> getCodeGroup(
		@PathVariable String id
	) {
		CodeInfo.CodeGroupInfo result = codeFacade.getCodeGroup(id);
		CodeResponse.CodeGroupInfoResponse response = codeDtoMapper.of(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/code-group/{id}/codes")
	public ResponseEntity<CodeResponse.CodeListResponse> getCodesByCodeGroup(
		@PathVariable String id,
		@PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
		@ModelAttribute CodeRequest.GetCodeList request
	) {
		CodeCommand.GetCodeList command = codeDtoMapper.of(request);
		Page<CodeInfo.Main> result = codeFacade.getCodesByCodeGroup(id, pageable, command);
		CodeResponse.CodeListResponse response = codeDtoMapper.mapToCodeList(result);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> createCode(
		@RequestBody CodeRequest.CreateCode request
	) {
		CodeCommand.CreateCode command = codeDtoMapper.of(request);
		codeFacade.createCode(command, request.getGroupId());
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/code-group")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> createCodeGroup(
		@RequestBody CodeRequest.CreateCodeGroup request
	) {
		CodeCommand.CreateCodeGroup command = codeDtoMapper.of(request);
		codeFacade.createCodeGroup(command);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> updateCode(
		@PathVariable String id,
		@RequestBody CodeRequest.UpdateCode request
	) {
		CodeCommand.UpdateCode command = codeDtoMapper.of(request);
		codeFacade.updateCode(command, id);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PutMapping("/code-group/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> updateCodeGroup(
		@PathVariable String id,
		@RequestBody CodeRequest.UpdateCodeGroup request
	) {
		CodeCommand.UpdateCodeGroup command = codeDtoMapper.of(request);
		codeFacade.updateCodeGroup(command, id);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> deleteCode(
		@PathVariable String id
	) {
		codeFacade.deleteCode(id);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@DeleteMapping("/code-group/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> deleteCodeGroup(
			@PathVariable String id
	) {
		codeFacade.deleteCodeGroup(id);
		return ResponseEntity.ok(Boolean.TRUE);
	}

}
