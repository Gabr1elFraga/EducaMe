package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.AnuncioAulaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnuncioAulaJpaRepository extends JpaRepository<AnuncioAulaEntity, UUID> {
	List<AnuncioAulaEntity> findByProfessor_IdAndAtivoTrue(UUID professorId);
	List<AnuncioAulaEntity> findByProfessor_IdOrderByCreatedAtDesc(UUID professorId);
	List<AnuncioAulaEntity> findByDisciplina_IdAndAtivoTrue(UUID disciplinaId);
}
