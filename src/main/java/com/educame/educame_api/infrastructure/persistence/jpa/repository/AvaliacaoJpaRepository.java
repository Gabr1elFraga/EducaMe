package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.AvaliacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AvaliacaoJpaRepository extends JpaRepository<AvaliacaoEntity, UUID> {
	@Query("select avg(a.nota) from AvaliacaoEntity a")
	Double averageNota();
}
