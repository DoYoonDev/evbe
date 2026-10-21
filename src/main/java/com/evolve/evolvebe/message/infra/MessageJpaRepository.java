package com.evolve.evolvebe.message.infra;

import com.evolve.evolvebe.message.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageJpaRepository extends JpaRepository<Message, String> {

    List<Message> findByIsActive(boolean isActive);

    List<Message> findByIsActive(boolean isActive, Sort sort);

    Page<Message> findByIdContainingAndKoTextContainingAndEnTextContainingAndIsActive(
        Pageable pageable, String id, String koText, String enText, Boolean isActive
    );

}

