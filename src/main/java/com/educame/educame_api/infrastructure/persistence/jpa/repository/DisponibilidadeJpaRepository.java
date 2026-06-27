package com.educame.educame_api.infrastructure.persistence.jpa.repository;

import com.educame.educame_api.infrastructure.persistence.jpa.entity.DisponibilidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DisponibilidadeJpaRepository extends JpaRepository<DisponibilidadeEntity, UUID> {
	List<DisponibilidadeEntity> findByProfessor_Id(UUID professorId);
	List<DisponibilidadeEntity> findByAnuncio_IdOrderByInicioAsc(UUID anuncioId);

	@Query(value = """
		select count(*)
		from disponibilidades
		where professor_id = :professorId
		  and status = cast(:status as public.disponibilidade_status)
		""", nativeQuery = true)
	long countByProfessorIdAndStatusValue(@Param("professorId") UUID professorId, @Param("status") String status);
}
