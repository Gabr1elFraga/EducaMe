package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.AulaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AulaJpaRepository extends JpaRepository<AulaEntity, UUID> {
	List<AulaEntity> findTop3ByOrderByCreatedAtDesc();
}
