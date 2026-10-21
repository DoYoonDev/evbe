package com.evolve.evolvebe.role.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evolve.evolvebe.role.domain.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleJpaRepository extends JpaRepository<Role, String> {

    @Query("""
        SELECT r.id
        FROM Role r
        WHERE r.id IN :ids
          AND COALESCE(r.isActive, TRUE) = TRUE
        ORDER BY r.level DESC
        """)
    List<String> findTopIdsByIdsOrderByLevelDesc(@Param("ids") List<String> ids);

}
