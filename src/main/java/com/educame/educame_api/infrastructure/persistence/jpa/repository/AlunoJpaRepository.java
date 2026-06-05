package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlunoJpaRepository extends JpaRepository<AlunoEntity, UUID> {
	List<AlunoEntity> findTop3ByOrderByCreatedAtDesc();
}
