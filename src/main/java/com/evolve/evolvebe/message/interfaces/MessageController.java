package com.evolve.evolvebe.message.interfaces;

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

import com.evolve.evolvebe.message.application.MessageFacade;
import com.evolve.evolvebe.message.domain.MessageCommand;
import com.evolve.evolvebe.message.domain.MessageInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

	private final MessageFacade messageFacade;
	private final MessageDtoMapper messageDtoMapper;

	@GetMapping("/{id}")
	public ResponseEntity<MessageResponse.Main> getMessage(
		@PathVariable String id
	) {
		MessageInfo.Main result = messageFacade.getMessage(id);
		MessageResponse.Main response = messageDtoMapper.of(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<MessageResponse.MessagePage> getMessages(
		@PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
		@ModelAttribute MessageRequest.GetList request
	) {
		MessageCommand.GetList command = messageDtoMapper.of(request);
		Page<MessageInfo.Main> result = messageFacade.getMessages(pageable, command);
		MessageResponse.MessagePage response = messageDtoMapper.of(result);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> createMessage(
		@RequestBody MessageRequest.Create request
	) {
		MessageCommand.Create command = messageDtoMapper.of(request);
		messageFacade.createMessage(command);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> updateMessage(
		@PathVariable String id,
		@RequestBody MessageRequest.Update request
	) {
		MessageCommand.Update command = messageDtoMapper.of(request);
		messageFacade.updateMessage(id, command);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> deleteMessage(
		@PathVariable String id
	) {
		messageFacade.deleteMessage(id);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@GetMapping("/translated/deploy-all")
	public ResponseEntity<Boolean> deployTranslatedMessagesAll() {
		messageFacade.deployTranslatedMessagesAll();
		return ResponseEntity.ok(Boolean.TRUE);
	}

}
