package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.DisponibilidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DisponibilidadeJpaRepository extends JpaRepository<DisponibilidadeEntity, UUID> {
	List<DisponibilidadeEntity> findByProfessor_Id(UUID professorId);
	List<DisponibilidadeEntity> findByAnuncio_IdOrderByInicioAsc(UUID anuncioId);
}
