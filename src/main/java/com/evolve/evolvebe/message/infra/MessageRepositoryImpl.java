package com.evolve.evolvebe.message.infra;

import com.evolve.evolvebe.message.domain.Message;
import com.evolve.evolvebe.message.domain.MessageCommand;
import com.evolve.evolvebe.message.domain.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageJpaRepository messageJpaRepository;

    @Override
    public Message getMessage(String id) {
        return messageJpaRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Message> getMessages(Pageable pageable, MessageCommand.GetList command) {
        return messageJpaRepository
            .findByIdContainingAndKoTextContainingAndEnTextContainingAndIsActive(
                pageable, command.getId(), command.getKoText(), command.getEnText(), command.getIsActive()
            );
    }

    @Override
    public List<Message> getMessages(Boolean isActive) { return messageJpaRepository.findByIsActive(isActive); }

    @Override
    public List<Message> getMessages(Boolean isActive, Sort sort) {
        return messageJpaRepository.findByIsActive(isActive, sort);
    }

    @Override
    public void saveMessage(Message message) {
        messageJpaRepository.save(message);
    }

    @Override
    public void deleteMessage(String id) { messageJpaRepository.deleteById(id); }

}
