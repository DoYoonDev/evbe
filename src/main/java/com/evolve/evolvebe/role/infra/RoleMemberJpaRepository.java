package com.evolve.evolvebe.role.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.evolve.evolvebe.role.domain.RoleMember;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleMemberJpaRepository extends JpaRepository<RoleMember, Long> {
	List<RoleMember> findAllByRoleId(String roleId);

    List<RoleMember> findAllByMemberId(String memberId);

	Optional<RoleMember> findByRoleIdAndMemberId(String roleId, String memberId);

	Optional<RoleMember> findByMemberId(String memberId);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("delete from RoleMember rm where rm.member.id = :memberId")
	void deleteAllByMemberId(@Param("memberId") String memberId);

    boolean existsByRoleIdAndMemberId(String roleId, String memberId);

    @EntityGraph(attributePaths = "member")
    Page<RoleMember> findByRoleIdAndMemberIdContaining(String roleId, String memberId, Pageable pageable);
}
