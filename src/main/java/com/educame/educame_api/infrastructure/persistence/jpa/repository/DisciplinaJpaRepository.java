package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.DisciplinaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DisciplinaJpaRepository extends JpaRepository<DisciplinaEntity, UUID> {
	List<DisciplinaEntity> findByAtivoTrueOrderByNomeAsc();
}
