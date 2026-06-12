package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.ProfessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessorJpaRepository extends JpaRepository<ProfessorEntity, UUID> {
	List<ProfessorEntity> findTop3ByOrderByCreatedAtDesc();

	Optional<ProfessorEntity> findByPessoa_AuthUserId(UUID authUserId);
}
