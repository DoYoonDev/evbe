package com.evolve.evolvebe.role.infra;

import com.evolve.evolvebe.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleJpaRepository extends JpaRepository<Role, String> {

    @Query("""
        SELECT r.id
        FROM Role r
        WHERE r.id IN :ids
          AND (r.isActive IS NULL OR r.isActive = :active)
        ORDER BY r.level DESC
        """)
    List<String> findIdsByIdsAndActiveOrderByLevelDesc(
            @Param("ids") List<String> ids,
            @Param("active") Boolean active
    );

    default List<String> findTopIdsByIdsOrderByLevelDesc(List<String> ids) {
        return findIdsByIdsAndActiveOrderByLevelDesc(ids, Boolean.TRUE);
    }
}